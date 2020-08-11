package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    /**
     * 查询用户地址列表
     * @param userId
     * @return
     */
    List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     * @param addressBO
     */
    void addNewUserAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     *
     * @param addressBO
     */
    void updateUserAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     *
     * @param userId
     * @param addressId
     */
    void deleteUserAddress(String userId, String addressId);

    /**
     * 修改默认地址
     *
     * @param userId
     * @param addressId
     */
    void updateUserAddressToDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id查询具体地址
     * @param userId
     * @param addressId
     * @return
     */
    UserAddress queryUserAddress(String userId, String addressId);


}
