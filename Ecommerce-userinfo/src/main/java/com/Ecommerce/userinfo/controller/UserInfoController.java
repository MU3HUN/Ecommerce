package com.Ecommerce.userinfo.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.Ecommerce.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Ecommerce.common.annotation.Log;
import com.Ecommerce.common.core.controller.BaseController;
import com.Ecommerce.common.core.domain.AjaxResult;
import com.Ecommerce.common.enums.BusinessType;
import com.Ecommerce.userinfo.domain.UserInfo;
import com.Ecommerce.userinfo.service.IUserInfoService;
import com.Ecommerce.common.utils.poi.ExcelUtil;
import com.Ecommerce.common.core.page.TableDataInfo;

/**
 * 人员管理Controller
 * 
 * @author huang
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/userinfo/info")
public class UserInfoController extends BaseController
{
    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 查询人员管理列表
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserInfo userInfo)
    {
        startPage();
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        return getDataTable(list);
    }

    /**
     * 导出人员管理列表
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:export')")
    @Log(title = "人员管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserInfo userInfo)
    {
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        ExcelUtil<UserInfo> util = new ExcelUtil<UserInfo>(UserInfo.class);
        util.exportExcel(response, list, "人员管理数据");
    }

    /**
     * 获取人员管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:query')")
    @GetMapping(value = "/{usersId}")
    public AjaxResult getInfo(@PathVariable("usersId") Long usersId)
    {
        return success(userInfoService.selectUserInfoByUserId(usersId));
    }

    /**
     * 新增人员管理
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:add')")
    @Log(title = "人员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserInfo userInfo) {
        userInfo.setUsersId(getUserId()); // 当前登录用户ID
        return toAjax(userInfoService.insertUserInfo(userInfo));
    }

    /**
     * 修改人员管理
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:edit')")
    @Log(title = "人员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserInfo userInfo)
    {
        return toAjax(userInfoService.updateUserInfo(userInfo));
    }

    /**
     * 删除人员管理
     */
    @PreAuthorize("@ss.hasPermi('userinfo:info:logicDelete')")
    @Log(title = "人员管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{usersIds}")
    public AjaxResult remove(@PathVariable Long[] usersIds)
    {
        return toAjax(userInfoService.deleteUserInfoByUserIds(usersIds));
    }
}
