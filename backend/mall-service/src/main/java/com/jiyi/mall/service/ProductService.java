package com.jiyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.mall.entity.Product;
import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 分页查询商品列表
     */
    Page<Product> getProductList(int page, int size, String keyword, String category, String sort, Boolean onlyStock, Integer minPrice, Integer maxPrice);
    
    /**
     * 根据ID获取商品详情
     */
    Product getProductById(Long id);
    
    /**
     * 创建商品
     */
    Product createProduct(Product product);
    
    /**
     * 更新商品
     */
    Product updateProduct(Long id, Product product);
    
    /**
     * 删除商品
     */
    void deleteProduct(Long id);
    
    /**
     * 减少库存（会抛出异常）
     */
    void decreaseStock(Long productId, Integer quantity);
    
    /**
     * 安全减少库存（返回是否成功，不抛异常）
     * 使用数据库级别的原子操作，防止并发超卖
     */
    boolean decreaseStockSafe(Long productId, Integer quantity);
    
    /**
     * 增加库存（用于取消订单时恢复库存）
     */
    void increaseStock(Long productId, Integer quantity);
    
    /**
     * 增加销量
     */
    void increaseSales(Long productId, Integer quantity);
}
