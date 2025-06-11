package com.Ecommerce.userinfo.service;

import java.util.List;

import com.Ecommerce.common.core.domain.AjaxResult;
import com.Ecommerce.userinfo.domain.UserInfo;

/**
 * 人员管理Service接口
 * 
 * @author huang
 * @date 2025-03-14
 */
public interface IUserInfoService 
{
    /**
     * 查询人员管理
     * 
     * @param usersId 人员管理主键
     * @return 人员管理
     */
    public UserInfo selectUserInfoByUserId(Long usersId);

    /**
     * 查询人员管理列表
     * 
     * @param userInfo 人员管理
     * @return 人员管理集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    /**
     * 新增人员管理
     * 
     * @param userInfo 人员管理
     * @return 结果
     */
    public int insertUserInfo(UserInfo userInfo);

    /**
     * 修改人员管理
     * 
     * @param userInfo 人员管理
     * @return 结果
     */
    public int updateUserInfo(UserInfo userInfo);

    /**
     * 批量删除人员管理
     * 
     * @param usersIds 需要删除的人员管理主键集合
     * @return 结果
     */
    public int deleteUserInfoByUserIds(Long[] usersIds);

    /**
     * 删除人员管理信息
     * 
     * @param usersId 人员管理主键
     * @return 结果
     */
    public int deleteUserInfoByUserId(Long usersId);


}
