package com.ruoyi.department.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.Size;

/**
 * 院系设置对象 department
 *
 * @author ruoyi
 * @date 2025-12-26
 */
public class Department extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 院系代号 */
    @Excel(name = "院系代号")
    private Long deptId;

    /** 院系名 */
    @Excel(name = "院系名")
    private String deptName;

    /** 地址 */
    @Excel(name = "地址")
    private String deptAddress;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @Size(min = 8, max = 8, message = "联系电话必须为 8 位")
    private String deptPhone;

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptAddress(String deptAddress)
    {
        this.deptAddress = deptAddress;
    }

    public String getDeptAddress()
    {
        return deptAddress;
    }

    public void setDeptPhone(String deptPhone)
    {
        this.deptPhone = deptPhone;
    }

    public String getDeptPhone()
    {
        return deptPhone;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("deptId", getDeptId())
                .append("deptName", getDeptName())
                .append("deptAddress", getDeptAddress())
                .append("deptPhone", getDeptPhone())
                .toString();
    }
}
