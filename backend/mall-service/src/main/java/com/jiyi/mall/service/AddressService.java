package com.jiyi.mall.service;

import com.jiyi.mall.entity.Address;
import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService {
    
    /**
     * 添加收货地址
     */
    Address addAddress(Address address);
    
    /**
     * 获取用户地址列表
     */
    List<Address> getAddressList(Long userId);
    
    /**
     * 获取地址详情
     */
    Address getAddressById(Long id, Long userId);
    
    /**
     * 更新收货地址
     */
    Address updateAddress(Long id, Long userId, Address address);
    
    /**
     * 删除收货地址
     */
    void deleteAddress(Long id, Long userId);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long id, Long userId);
    
    /**
     * 获取默认地址
     */
    Address getDefaultAddress(Long userId);
}
