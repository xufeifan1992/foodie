package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     *
     * @param userId
     * @param faceUrl
     */
    public Users updateUserFace(String userId, String faceUrl);
}
