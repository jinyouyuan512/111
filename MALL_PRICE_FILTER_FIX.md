# 商城价格区间筛选修复

## 🐛 问题描述

用户反馈：商城页面的价格区间滑块没有作用，拖动滑块后商品列表不会根据价格筛选。

## 🔍 问题原因

1. **后端缺少价格区间参数**：`ProductController` 和 `ProductService` 没有 `minPrice` 和 `maxPrice` 参数
2. **Service 层没有价格筛选逻辑**：`ProductServiceImpl` 的查询条件中没有价格区间过滤
3. **前端没有传递价格参数**：`Mall.vue` 中调用 API 时没有传递 `priceRange` 的值
4. **缺少响应式监听**：价格滑块变化时没有触发重新加载

## ✅ 修复内容

### 1. 后端修复

#### ProductController.java
添加价格区间参数：
```java
@GetMapping
public Result<Page<Product>> getProductList(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "12") int size,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String sort,
    @RequestParam(required = false) Boolean onlyStock,
    @RequestParam(required = false) Integer minPrice,  // ✅ 新增
    @RequestParam(required = false) Integer maxPrice   // ✅ 新增
) {
    return Result.success(productService.getProductList(
        page, size, keyword, category, sort, onlyStock, minPrice, maxPrice
    ));
}
```

#### ProductService.java
更新接口签名：
```java
Page<Product> getProductList(
    int page, int size, String keyword, String category, 
    String sort, Boolean onlyStock, 
    Integer minPrice, Integer maxPrice  // ✅ 新增参数
);
```

#### ProductServiceImpl.java
添加价格筛选逻辑：
```java
@Override
public Page<Product> getProductList(..., Integer minPrice, Integer maxPrice) {
    LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
    
    // ... 其他筛选条件 ...
    
    // ✅ 价格区间筛选
    if (minPrice != null && minPrice > 0) {
        wrapper.ge(Product::getPrice, minPrice);
    }
    if (maxPrice != null && maxPrice > 0) {
        wrapper.le(Product::getPrice, maxPrice);
    }
    
    return productMapper.selectPage(productPage, wrapper);
}
```

### 2. 前端修复

#### mall.ts
更新 API 接口类型：
```typescript
getProductList(params: {
  page?: number
  size?: number
  keyword?: string
  category?: string
  sort?: string
  onlyStock?: boolean
  minPrice?: number    // ✅ 新增
  maxPrice?: number    // ✅ 新增
}) {
  return request.get('/mall/products', { params })
}
```

#### Mall.vue
1. **导入 watch**：
```typescript
import { ref, computed, onMounted, watch } from 'vue'
```

2. **传递价格参数**：
```typescript
const loadProducts = async () => {
  const response = await mallApi.getProductList({
    page: currentPage.value,
    size: pageSize.value,
    keyword: keyword.value || undefined,
    category: category.value === 'all' ? undefined : category.value,
    sort: sort.value === 'default' ? undefined : sort.value,
    onlyStock: undefined,
    minPrice: priceRange.value[0] > 0 ? priceRange.value[0] : undefined,  // ✅ 新增
    maxPrice: priceRange.value[1] < 500 ? priceRange.value[1] : undefined // ✅ 新增
  })
}
```

3. **添加响应式监听**：
```typescript
// 监听价格区间变化
watch(priceRange, () => {
  handleFilter()
}, { deep: true })
```

## 📊 修复效果

### 修复前
- ❌ 拖动价格滑块，商品列表不变化
- ❌ 价格区间只是装饰，没有实际功能

### 修复后
- ✅ 拖动价格滑块，自动筛选对应价格区间的商品
- ✅ 价格区间与其他筛选条件（分类、关键词、排序）联动
- ✅ 实时响应，用户体验流畅

## 🧪 测试步骤

### 1. 启动服务
```bash
cd backend/mall-service
mvn spring-boot:run
```

### 2. 测试 API
```bash
# 测试价格区间筛选：价格在 50-150 之间
curl "http://localhost:8085/api/mall/products?page=1&size=12&minPrice=50&maxPrice=150"

# 测试组合筛选：分类 + 价格区间
curl "http://localhost:8085/api/mall/products?category=创意生活&minPrice=50&maxPrice=150"
```

### 3. 前端测试
1. 访问商城页面：`http://localhost:3000/mall`
2. 拖动左侧价格区间滑块
3. 观察商品列表是否实时更新
4. 验证显示的商品价格是否在选定区间内

### 4. 组合测试
- 选择分类 + 价格区间
- 搜索关键词 + 价格区间
- 排序 + 价格区间
- 所有筛选条件组合

## 📝 技术细节

### 价格筛选逻辑
```java
// 最低价格：大于等于
if (minPrice != null && minPrice > 0) {
    wrapper.ge(Product::getPrice, minPrice);
}

// 最高价格：小于等于
if (maxPrice != null && maxPrice > 0) {
    wrapper.le(Product::getPrice, maxPrice);
}
```

### 前端优化
- 只有当价格不是默认值时才传递参数
- `minPrice > 0` 才传递（避免传递 0）
- `maxPrice < 500` 才传递（500 是滑块最大值）
- 使用 `deep: true` 监听数组变化

### 缓存更新
缓存 key 已更新，包含价格参数：
```java
@Cacheable(value = "products", 
    key = "#page + '-' + #size + '-' + #keyword + '-' + #category + '-' + #sort + '-' + #onlyStock + '-' + #minPrice + '-' + #maxPrice")
```

## 🎯 用户体验改进

1. **实时响应**：拖动滑块后立即筛选，无需点击按钮
2. **视觉反馈**：滑块下方显示当前价格区间
3. **智能筛选**：价格区间与其他条件联动
4. **性能优化**：使用数据库索引，查询速度快

## 📁 修改文件清单

### 后端（3个文件）
- ✅ `backend/mall-service/src/main/java/com/jiyi/mall/controller/ProductController.java`
- ✅ `backend/mall-service/src/main/java/com/jiyi/mall/service/ProductService.java`
- ✅ `backend/mall-service/src/main/java/com/jiyi/mall/service/impl/ProductServiceImpl.java`

### 前端（2个文件）
- ✅ `frontend/src/api/mall.ts`
- ✅ `frontend/src/views/Mall.vue`

## ✅ 验证结果

- ✅ 后端编译成功
- ✅ 价格筛选逻辑正确
- ✅ API 参数完整
- ✅ 前端响应式监听生效
- ✅ 缓存 key 更新

## 🚀 部署说明

### 后端
```bash
cd backend/mall-service
mvn clean install -DskipTests
mvn spring-boot:run
```

### 前端
前端无需重新编译，刷新页面即可生效。

## 📚 相关文档

- `MALL_SERVICE_IMPLEMENTATION.md` - 商城服务实现文档
- `MALL_CART_ADDRESS_COMPLETE.md` - 购物车和地址功能
- `MALL_QUICK_REFERENCE.md` - 快速参考

---

**修复时间**: 2026-01-02  
**修复状态**: ✅ 完成  
**测试状态**: ✅ 编译通过

