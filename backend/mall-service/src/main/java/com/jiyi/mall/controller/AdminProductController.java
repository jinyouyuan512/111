package com.jiyi.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.mall.entity.Product;
import com.jiyi.mall.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 - 商品管理控制器
 */
@Tag(name = "管理员-商品管理")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductMapper productMapper;

    @Operation(summary = "获取商品列表")
    @GetMapping
    public Result<Map<String, Object>> listProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        if (StringUtils.hasText(status)) {
            // status: "on" = inStock true, "off" = inStock false
            wrapper.eq(Product::getInStock, "on".equals(status));
        }
        wrapper.orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        
        return Result.success(data);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{productId}")
    public Result<Product> getProductDetail(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        return Result.success(product);
    }

    @Operation(summary = "添加商品")
    @PostMapping
    public Result<Long> addProduct(@RequestBody Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        if (product.getInStock() == null) {
            product.setInStock(true);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        productMapper.insert(product);
        return Result.success(product.getId());
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{productId}")
    public Result<Void> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        product.setId(productId);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        productMapper.deleteById(productId);
        return Result.success();
    }

    @Operation(summary = "上架商品")
    @PostMapping("/{productId}/on")
    public Result<Void> putOnSale(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setInStock(true);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return Result.success();
    }

    @Operation(summary = "下架商品")
    @PostMapping("/{productId}/off")
    public Result<Void> putOffSale(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setInStock(false);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return Result.success();
    }

    @Operation(summary = "更新库存")
    @PostMapping("/{productId}/stock")
    public Result<Void> updateStock(@PathVariable Long productId, @RequestBody Map<String, Integer> body) {
        Integer stock = body.get("stock");
        Product product = productMapper.selectById(productId);
        if (product != null && stock != null) {
            product.setStock(stock);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return Result.success();
    }

    @Operation(summary = "获取商品统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getProductStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总商品数
        stats.put("totalProducts", productMapper.selectCount(null));
        
        // 上架商品数
        LambdaQueryWrapper<Product> onWrapper = new LambdaQueryWrapper<>();
        onWrapper.eq(Product::getInStock, true);
        stats.put("onSaleCount", productMapper.selectCount(onWrapper));
        
        // 下架商品数
        LambdaQueryWrapper<Product> offWrapper = new LambdaQueryWrapper<>();
        offWrapper.eq(Product::getInStock, false);
        stats.put("offSaleCount", productMapper.selectCount(offWrapper));
        
        // 库存不足商品数 (库存 < 10)
        LambdaQueryWrapper<Product> lowStockWrapper = new LambdaQueryWrapper<>();
        lowStockWrapper.lt(Product::getStock, 10);
        stats.put("lowStockCount", productMapper.selectCount(lowStockWrapper));
        
        return Result.success(stats);
    }
}
