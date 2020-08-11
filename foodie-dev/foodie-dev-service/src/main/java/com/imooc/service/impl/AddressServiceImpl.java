package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private Sid sid;

    @Override
    public List<UserAddress> queryAll(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

       return userAddressMapper.select(userAddress);

    }

    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        //1.判断当前用户是否存在地址。如果没有，则新增为"默认地址"
        List<UserAddress> list = this.queryAll(addressBO.getUserId());
        Integer isDefault = 0;
        if(list == null || list.size() == 0){
            isDefault =  1;
        }
        String addressId = sid.nextShort();
        //2.保存地址到数据库
        UserAddress newAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, newAddress);
        newAddress.setId(addressId);
        newAddress.setIsDefault(isDefault);
        newAddress.setCreatedTime(new Date());
        newAddress.setUpdatedTime(newAddress.getCreatedTime() );

        userAddressMapper.insert(newAddress);

    }

    @Override
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();

        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, pendingAddress);

        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(pendingAddress);

    }

    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);

        userAddressMapper.delete(userAddress);

    }

    @Override
    public void updateUserAddressToDefault(String userId, String addressId) {
        //查找默认地址,设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.yes.type);

        List<UserAddress> list = userAddressMapper.select(queryAddress);
        for (UserAddress userAddress : list) {
            userAddress.setIsDefault(YesOrNo.no.type);
            userAddressMapper.updateByPrimaryKeySelective(userAddress);
        }


        //根据地址ID设置为默认地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setId(addressId);
        defaultAddress.setUserId(userId);
        defaultAddress.setIsDefault(YesOrNo.yes.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }

    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress singalAddress = new UserAddress();
        singalAddress.setUserId(userId);
        singalAddress.setId(addressId);

        return userAddressMapper.selectOne(singalAddress);
    }
}
