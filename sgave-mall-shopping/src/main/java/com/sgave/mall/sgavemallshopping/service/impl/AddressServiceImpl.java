package com.sgave.mall.sgavemallshopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sgave.mall.sgavemallshopping.pojo.SgaveAddress;
import com.sgave.mall.sgavemallshopping.mapper.AddressMapper;
import com.sgave.mall.sgavemallshopping.service.AddressService;
import com.sgave.mall.util.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : zeping
 * @description :
 * @createDate : 2025/6/13
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    /**
     * 查询收货地址列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<SgaveAddress> list(Integer userId) {
        LambdaQueryWrapper<SgaveAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SgaveAddress::getUserId, userId).eq(SgaveAddress::getDeleted, 0).orderByDesc(SgaveAddress::getAddTime);
        return addressMapper.selectList(queryWrapper);
    }

    /**
     * 删除收货地址
     *
     * @param addressId
     */
    @Override
    public void delete(Integer addressId) {
        LambdaUpdateWrapper<SgaveAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SgaveAddress::getId, addressId).set(SgaveAddress::getDeleted, 1);
        addressMapper.update(updateWrapper);
    }

    /**
     * 新增收货地址
     *
     * @param userId
     * @param address
     * @return
     */
    @Override
    public Object save(Integer userId, SgaveAddress address) {
        if (address.getIsDefault()) {
            // 重置其他收货地址的默认选项
            resetDefault(userId);
        }
        address.setId(null);
        address.setUserId(userId);
        address.setAddTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.insert(address);
        return ResponseUtil.ok(address.getId());
    }

    /**
     * 编辑收货地址
     *
     * @param userId
     * @param address
     * @return
     */
    @Override
    public Object update(Integer userId, SgaveAddress address) {
        LambdaUpdateWrapper<SgaveAddress> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(SgaveAddress::getUserId, userId).eq(SgaveAddress::getId, address.getId());
        SgaveAddress sgaveAddress = addressMapper.selectOne(queryWrapper);
        if (sgaveAddress == null) {
            return ResponseUtil.badArgumentValue();
        }

        if (address.getIsDefault()) {
            // 重置其他收货地址的默认选项
            resetDefault(userId);
        }
        address.setUserId(userId);
        addressMapper.updateById(address);
        return ResponseUtil.ok("编辑收货地址成功");
    }

    @Override
    public SgaveAddress query(Integer userId, Integer addressId) {
        LambdaQueryWrapper<SgaveAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SgaveAddress::getUserId, userId).eq(SgaveAddress::getId, addressId);
        return addressMapper.selectOne(queryWrapper);
    }


    /**
     * 重设用户默认收货地址
     *
     * @param userId 用户ID
     * @return 重设操作结果
     */
    private void resetDefault(Integer userId) {
        SgaveAddress address = new SgaveAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.update(address, new LambdaUpdateWrapper<SgaveAddress>().eq(SgaveAddress::getUserId, userId));
    }
}
