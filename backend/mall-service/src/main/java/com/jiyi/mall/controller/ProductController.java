package com.jiyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiyi.common.result.Result;
import com.jiyi.mall.entity.Product;
import com.jiyi.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/products")
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    @Operation(summary = "获取商品列表", description = "分页查询商品列表，支持搜索、筛选、排序")
    public Result<Page<Product>> getProductList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "商品分类") @RequestParam(required = false) String category,
            @Parameter(description = "排序方式") @RequestParam(required = false) String sort,
            @Parameter(description = "仅显示有货") @RequestParam(required = false) Boolean onlyStock,
            @Parameter(description = "最低价格") @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) Integer maxPrice
    ) {
        log.info("查询商品列表: page={}, size={}, keyword={}, category={}, sort={}, onlyStock={}, minPrice={}, maxPrice={}", 
                page, size, keyword, category, sort, onlyStock, minPrice, maxPrice);
        return Result.success(productService.getProductList(page, size, keyword, category, sort, onlyStock, minPrice, maxPrice));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息")
    public Result<Product> getProductById(@Parameter(description = "商品ID") @PathVariable Long id) {
        log.info("查询商品详情: id={}", id);
        return Result.success(productService.getProductById(id));
    }
    
    @PostMapping
    @Operation(summary = "创建商品", description = "创建新商品")
    public Result<Product> createProduct(@RequestBody Product product) {
        log.info("创建商品: {}", product.getName());
        return Result.success(productService.createProduct(product));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "更新商品信息")
    public Result<Product> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @RequestBody Product product
    ) {
        log.info("更新商品: id={}", id);
        return Result.success(productService.updateProduct(id, product));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "删除商品（逻辑删除）")
    public Result<Void> deleteProduct(@Parameter(description = "商品ID") @PathVariable Long id) {
        log.info("删除商品: id={}", id);
        productService.deleteProduct(id);
        return Result.success();
    }
}
