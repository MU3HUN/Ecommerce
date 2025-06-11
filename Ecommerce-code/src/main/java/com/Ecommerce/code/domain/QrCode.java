package com.Ecommerce.code.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.Ecommerce.common.annotation.Excel;
import com.Ecommerce.common.core.domain.BaseEntity;

/**
 * 贴码核销对象 qr_code
 *
 * @author huang
 * @date 2025-03-14
 */
public class QrCode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer isDeleted;

    /** 关联系统用户ID */
    private Long userId;

    /** 部门编号 */
    private Long deptId;

    /** 角色编号 */
    private Long roleId;

    /** 唯一标识 */
    private Long qrId;

    /** 用户姓名 */
    @Excel(name = "用户姓名")
    private String userName;

    /** 关联批次ID */
    private Long batchId;

    /** 二维码编号 */
    @Excel(name = "二维码编号")
    private String qrNumber;

    /** 核销状态 */
    @Excel(name = "核销状态")
    private String status;

    /** 核销时间(精确到秒) */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Excel(name = "核销时间", width = 30, dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date redeemTime;

    /** 创建时间（精确到秒） */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date createTime;

    public void setIsDeleted(Integer isDeleted) {this.isDeleted = isDeleted;}
    public Integer getIsDeleted() {return isDeleted;}

    public void setQrId(Long qrId)
    {
        this.qrId = qrId;
    }
    public Long getQrId()
    {
        return qrId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }
    public Long getBatchId() {return batchId;}

    public void setQrNumber(String qrNumber)
    {
        this.qrNumber = qrNumber;
    }
    public String getQrNumber()
    {
        return qrNumber;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getStatus()
    {
        return status;
    }

    public void setRedeemTime(Date redeemTime)
    {
        this.redeemTime = redeemTime;
    }
    public Date getRedeemTime()
    {
        return redeemTime;
    }

    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getUserName()
    {
        return userName;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("qrId", getQrId())
                .append("batchId", getBatchId())
                .append("qrNumber", getQrNumber())
                .append("status", getStatus())
                .append("redeemTime", getRedeemTime())
                .append("createTime", getCreateTime())
                .append("userName", getUserName())
                .toString();
    }
}