package com.imooc.service;

import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.Users;

public interface UserService {

    public boolean queryUsernameIsExit(String username);


    public Users creatUser(UserBO userBO);


    public Users queryUserForLogin(String username,String password);
}
