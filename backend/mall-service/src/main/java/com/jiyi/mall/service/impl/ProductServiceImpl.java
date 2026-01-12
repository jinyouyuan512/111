package com.jiyi.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.entity.Product;
import com.jiyi.mall.mapper.ProductMapper;
import com.jiyi.mall.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Cacheable(value = "products", key = "#page + '-' + #size + '-' + #keyword + '-' + #category + '-' + #sort + '-' + #onlyStock + '-' + #minPrice + '-' + #maxPrice")
    public Page<Product> getProductList(int page, int size, String keyword, String category, String sort, Boolean onlyStock, Integer minPrice, Integer maxPrice) {
        Page<Product> productPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword)
                    .or().like(Product::getCulturalBackground, keyword));
        }
        
        // 分类筛选
        if (StringUtils.hasText(category) && !"all".equals(category) && !"全部商品".equals(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        
        // 价格区间筛选
        if (minPrice != null && minPrice > 0) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null && maxPrice > 0) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        
        // 只显示有货商品
        if (onlyStock != null && onlyStock) {
            wrapper.eq(Product::getInStock, true);
        }
        
        // 排序
        if (StringUtils.hasText(sort)) {
            switch (sort) {
                case "price_asc":
                    wrapper.orderByAsc(Product::getPrice);
                    break;
                case "price_desc":
                    wrapper.orderByDesc(Product::getPrice);
                    break;
                case "sales_desc":
                    wrapper.orderByDesc(Product::getSales);
                    break;
                default:
                    wrapper.orderByDesc(Product::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }
        
        return productMapper.selectPage(productPage, wrapper);
    }
    
    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(Product product) {
        productMapper.insert(product);
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product updateProduct(Long id, Product product) {
        product.setId(id);
        productMapper.updateById(product);
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
                .ge(Product::getStock, quantity)
                .setSql("stock = stock - " + quantity);
        
        int updated = productMapper.update(null, wrapper);
        if (updated == 0) {
            throw new RuntimeException("库存不足");
        }
        
        // 检查库存是否为0，更新 inStock 状态
        Product product = productMapper.selectById(productId);
        if (product != null && product.getStock() <= 0) {
            product.setInStock(false);
            productMapper.updateById(product);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseStockSafe(Long productId, Integer quantity) {
        // 使用数据库级别的原子操作，确保库存不会变成负数
        // WHERE stock >= quantity 条件确保只有库存足够时才会扣减
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
                .ge(Product::getStock, quantity)  // 关键：只有库存 >= 购买数量时才更新
                .setSql("stock = stock - " + quantity);
        
        int updated = productMapper.update(null, wrapper);
        
        if (updated > 0) {
            // 扣减成功，检查库存是否为0，更新 inStock 状态
            Product product = productMapper.selectById(productId);
            if (product != null && product.getStock() <= 0) {
                product.setInStock(false);
                productMapper.updateById(product);
            }
            log.info("库存扣减成功: productId={}, quantity={}", productId, quantity);
            return true;
        }
        
        log.warn("库存扣减失败（库存不足）: productId={}, quantity={}", productId, quantity);
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
                .setSql("stock = stock + " + quantity);
        
        productMapper.update(null, wrapper);
        
        // 恢复库存后，更新 inStock 状态
        Product product = productMapper.selectById(productId);
        if (product != null && product.getStock() > 0) {
            product.setInStock(true);
            productMapper.updateById(product);
        }
        
        log.info("库存恢复成功: productId={}, quantity={}", productId, quantity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseSales(Long productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
                .setSql("sales = sales + " + quantity);
        
        productMapper.update(null, wrapper);
    }
}
