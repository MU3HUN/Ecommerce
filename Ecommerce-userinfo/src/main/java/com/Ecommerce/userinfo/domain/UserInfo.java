package com.Ecommerce.userinfo.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.Ecommerce.common.annotation.Excel;
import com.Ecommerce.common.core.domain.BaseEntity;

/**
 * 人员管理对象 user_info
 * 
 * @author huang
 * @date 2025-03-14
 */
public class UserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer isDeleted;

    private Long roleId;

    private Long deptId;

    private String deptName;

    /** 关联系统用户ID */
    @Excel(name = "系统用户ID")
    private Long userId;

    /** 用户唯一标识 */
    @Excel(name = "人员ID")
    private Long usersId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 性别 */
    @Excel(name = "性别")
    private String gender;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 联系地址 */
    @Excel(name = "联系地址")
    private String address;

    public String getDeptName() {return deptName;}
    public void setDeptName(String deptName) {this.deptName = deptName;}

    public void setIsDeleted(Integer isDeleted) {this.isDeleted = isDeleted;}
    public Integer getIsDeleted() {return isDeleted;}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public void setUsersId(Long usersId)
    {
        this.usersId = usersId;
    }
    public Long getUsersId()
    {
        return usersId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }
    public String getName() 
    {
        return name;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }
    public String getGender() 
    {
        return gender;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }
    public String getPhone() 
    {
        return phone;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }
    public String getAddress() 
    {
        return address;
    }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Long getRoleId() {return roleId;}
    public void setRoleId(Long roleId) {this.roleId = roleId;}

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("usersId", getUsersId())
            .append("name", getName())
            .append("gender", getGender())
            .append("phone", getPhone())
            .append("address", getAddress())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
