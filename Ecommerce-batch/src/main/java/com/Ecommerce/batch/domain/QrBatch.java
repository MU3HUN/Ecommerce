package com.Ecommerce.batch.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.Ecommerce.common.annotation.Excel;
import com.Ecommerce.common.core.domain.BaseEntity;

/**
 * 二维码管理对象 qr_batch
 * 
 * @author huang
 * @date 2025-03-14
 */
public class QrBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Integer isDeleted;

    /** 关联系统用户ID */
    private Long userId;

    /** 部门编号 */
    private Long deptId;

    /** 角色编号 */
    private Long roleId;

    /** 批次ID */
    private Long batchId;

    /** 关联用户ID */
    @Excel(name = "关联用户ID")
    private Long usersId;

    /** 起始序号 */
    @Excel(name = "起始序号")
    private String startSeq;

    /** 结束序号 */
    @Excel(name = "结束序号")
    private String endSeq;

    /** 总数 */
    @Excel(name = "总数")
    private Integer total;

    /** 已核销 */
    @Excel(name = "已核销")
    private Integer redeemed;

    /** 未核销 */
    @Excel(name = "未核销")
    private Integer unredeemed;

    /** 生成时间*/
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Excel(name = "生成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date generateTime;

    public void setIsDeleted(Integer isDeleted) {this.isDeleted = isDeleted;}
    public Integer getIsDeleted() {return isDeleted;}

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }
    public Long getBatchId()
    {
        return batchId;
    }

    public void setUsersId(Long usersId)
    {
        this.usersId = usersId;
    }
    public Long getUsersId()
    {
        return usersId;
    }

    public void setStartSeq(String startSeq)
    {
        this.startSeq = startSeq;
    }
    public String getStartSeq()
    {
        return startSeq;
    }

    public void setEndSeq(String endSeq)
    {
        this.endSeq = endSeq;
    }
    public String getEndSeq()
    {
        return endSeq;
    }

    public void setGenerateTime(Date generateTime)
    {
        this.generateTime = generateTime;
    }
    public Date getGenerateTime()
    {
        return generateTime;
    }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public Integer getRedeemed() { return redeemed; }
    public void setRedeemed(Integer redeemed) { this.redeemed = redeemed; }

    public Integer getUnredeemed() { return unredeemed; }
    public void setUnredeemed(Integer unredeemed) { this.unredeemed = unredeemed; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("batchId", getBatchId())
                .append("usersId", getUsersId())
                .append("startSeq", getStartSeq())
                .append("endSeq", getEndSeq())
                .append("generateTime", getGenerateTime())
                .toString();
    }
}