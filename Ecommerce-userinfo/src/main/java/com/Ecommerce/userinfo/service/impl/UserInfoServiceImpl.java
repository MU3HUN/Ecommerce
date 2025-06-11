package com.Ecommerce.userinfo.service.impl;

import java.util.List;

import com.Ecommerce.common.annotation.DataScope;
import com.Ecommerce.common.core.domain.AjaxResult;
import com.Ecommerce.common.core.domain.entity.SysUser;
import com.Ecommerce.common.utils.DateUtils;
import com.Ecommerce.common.utils.SecurityUtils;
import com.Ecommerce.qrcodelist.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.userinfo.mapper.UserInfoMapper;
import com.Ecommerce.userinfo.domain.UserInfo;
import com.Ecommerce.userinfo.service.IUserInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 人员管理Service业务层处理
 * 
 * @author huang
 * @date 2025-03-14
 */
@Service
public class UserInfoServiceImpl implements IUserInfoService 
{
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 查询人员管理
     * 
     * @param usersId 人员管理主键
     * @return 人员管理
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public UserInfo selectUserInfoByUserId(Long usersId)
    {
        return userInfoMapper.selectUserInfoByUserId(usersId);
    }

    /**
     * 查询人员管理列表
     * 
     * @param userInfo 人员管理
     * @return 人员管理
     */
    @Override
    @DataScope(deptAlias = "u", userAlias = "u") // 添加数据权限注解
    public List<UserInfo> selectUserInfoList(UserInfo userInfo) {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    /**
     * 新增人员管理
     * 
     * @param userInfo 人员管理
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUserInfo(UserInfo userInfo)
    {
        userInfo.setCreateTime(DateUtils.getNowDate());
        return userInfoMapper.insertUserInfo(userInfo);
    }

    /**
     * 修改人员管理
     * 
     * @param userInfo 人员管理
     * @return 结果
     */
    @Override
    public int updateUserInfo(UserInfo userInfo)
    {
        userInfo.setUpdateTime(DateUtils.getNowDate());
        return userInfoMapper.updateUserInfo(userInfo);
    }

    /**
     * 批量删除人员管理
     * 
     * @param usersIds 需要删除的人员管理主键
     * @return 结果
     */
    @Override
    public int deleteUserInfoByUserIds(Long[] usersIds) {
        return userInfoMapper.deleteUserInfoByUserIds(usersIds);
    }
    /**
     * 删除人员管理信息
     * 
     * @param usersId 人员管理主键
     * @return 结果
     */
    @Override
    public int deleteUserInfoByUserId(Long usersId)
    {
        return userInfoMapper.deleteUserInfoByUserId(usersId);
    }


}
