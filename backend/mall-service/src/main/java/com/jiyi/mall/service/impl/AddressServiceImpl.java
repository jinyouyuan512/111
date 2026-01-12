package com.jiyi.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jiyi.mall.entity.Address;
import com.jiyi.mall.mapper.AddressMapper;
import com.jiyi.mall.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address addAddress(Address address) {
        // 如果是第一个地址或设置为默认地址，需要取消其他默认地址
        if (address.getIsDefault() == null) {
            address.setIsDefault(false);
        }
        
        if (address.getIsDefault()) {
            clearDefaultAddress(address.getUserId());
        }
        
        // 如果是用户的第一个地址，自动设为默认
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, address.getUserId());
        Long count = addressMapper.selectCount(wrapper);
        if (count == 0) {
            address.setIsDefault(true);
        }
        
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreatedAt);
        return addressMapper.selectList(wrapper);
    }
    
    @Override
    public Address getAddressById(Long id, Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getId, id)
                .eq(Address::getUserId, userId);
        return addressMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address updateAddress(Long id, Long userId, Address address) {
        Address existingAddress = getAddressById(id, userId);
        if (existingAddress == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 如果设置为默认地址，需要取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearDefaultAddress(userId);
        }
        
        address.setId(id);
        address.setUserId(userId);
        addressMapper.updateById(address);
        return address;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id, Long userId) {
        Address address = getAddressById(id, userId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        addressMapper.deleteById(id);
        
        // 如果删除的是默认地址，将第一个地址设为默认
        if (address.getIsDefault()) {
            LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Address::getUserId, userId)
                    .orderByDesc(Address::getCreatedAt)
                    .last("LIMIT 1");
            Address firstAddress = addressMapper.selectOne(wrapper);
            if (firstAddress != null) {
                firstAddress.setIsDefault(true);
                addressMapper.updateById(firstAddress);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long id, Long userId) {
        Address address = getAddressById(id, userId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 取消其他默认地址
        clearDefaultAddress(userId);
        
        // 设置当前地址为默认
        address.setIsDefault(true);
        addressMapper.updateById(address);
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, true);
        return addressMapper.selectOne(wrapper);
    }
    
    /**
     * 取消用户的所有默认地址
     */
    private void clearDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, true)
                .set(Address::getIsDefault, false);
        addressMapper.update(null, wrapper);
    }
}
