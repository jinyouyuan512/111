package com.jiyi.mall.controller;

import com.jiyi.common.result.Result;
import com.jiyi.mall.entity.Address;
import com.jiyi.mall.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mall/addresses")
@Tag(name = "地址管理", description = "收货地址相关接口")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @PostMapping
    @Operation(summary = "添加收货地址", description = "添加新的收货地址")
    public Result<Address> addAddress(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Address address
    ) {
        log.info("添加收货地址: userId={}, receiverName={}", userId, address.getReceiverName());
        address.setUserId(userId);
        return Result.success(addressService.addAddress(address));
    }
    
    @GetMapping
    @Operation(summary = "获取地址列表", description = "获取用户的收货地址列表")
    public Result<List<Address>> getAddressList(@RequestHeader("X-User-Id") Long userId) {
        log.info("获取地址列表: userId={}", userId);
        return Result.success(addressService.getAddressList(userId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情", description = "获取指定地址的详细信息")
    public Result<Address> getAddressById(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("获取地址详情: id={}, userId={}", id, userId);
        return Result.success(addressService.getAddressById(id, userId));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新收货地址", description = "更新收货地址信息")
    public Result<Address> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Address address
    ) {
        log.info("更新收货地址: id={}, userId={}", id, userId);
        return Result.success(addressService.updateAddress(id, userId, address));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除收货地址", description = "删除收货地址")
    public Result<Void> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("删除收货地址: id={}, userId={}", id, userId);
        addressService.deleteAddress(id, userId);
        return Result.success();
    }
    
    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址", description = "设置指定地址为默认地址")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("设置默认地址: id={}, userId={}", id, userId);
        addressService.setDefaultAddress(id, userId);
        return Result.success();
    }
    
    @GetMapping("/default")
    @Operation(summary = "获取默认地址", description = "获取用户的默认收货地址")
    public Result<Address> getDefaultAddress(@RequestHeader("X-User-Id") Long userId) {
        log.info("获取默认地址: userId={}", userId);
        return Result.success(addressService.getDefaultAddress(userId));
    }
}
