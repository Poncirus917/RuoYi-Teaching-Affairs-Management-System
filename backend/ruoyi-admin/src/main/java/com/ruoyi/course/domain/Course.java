package com.ruoyi.course.domain;

import java.math.BigDecimal;
import javax.validation.constraints.*; // 必须引入校验注解
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程信息对象 course
 * * @author ruoyi
 * @date 2025-12-27
 */
public class Course extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程号 - 增加 8 位长度约束 */
    @Excel(name = "课程号")
    @NotBlank(message = "课程号不能为空")
    @Size(min = 8, max = 8, message = "课程号必须为 8 位")
    private String courseId;

    /** 课程名 */
    @Excel(name = "课程名")
    @NotBlank(message = "课程名不能为空")
    private String courseName;

    /** 学分 */
    @Excel(name = "学分")
    @NotNull(message = "学分不能为空")
    private Long credit;

    /** 学时 */
    @Excel(name = "学时")
    @NotNull(message = "学时不能为空")
    private Long studyHours;

    /** 开课院系号 */
    @Excel(name = "开课院系号")
    @NotNull(message = "开课院系不能为空")
    private Long deptId;

    /** 所属院系名称 - 新增此属性以修复 ReflectionException */
    @Excel(name = "所属院系")
    private String deptName;

    /** 平时成绩占比 - 增加 0~1 范围约束 */
    @Excel(name = "平时成绩占比")
    @DecimalMin(value = "0.0", message = "占比不能小于 0")
    @DecimalMax(value = "1.0", message = "占比不能大于 1")
    private BigDecimal usualWeight;

    /** 期末成绩占比 - 增加 0~1 范围约束 */
    @Excel(name = "期末成绩占比")
    @DecimalMin(value = "0.0", message = "占比不能小于 0")
    @DecimalMax(value = "1.0", message = "占比不能大于 1")
    private BigDecimal finalWeight;

    public void setCourseId(String courseId)
    {
        this.courseId = courseId;
    }

    public String getCourseId()
    {
        return courseId;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCredit(Long credit)
    {
        this.credit = credit;
    }

    public Long getCredit()
    {
        return credit;
    }

    public void setStudyHours(Long studyHours)
    {
        this.studyHours = studyHours;
    }

    public Long getStudyHours()
    {
        return studyHours;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    // 新增 deptName 的 Setter 和 Getter
    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setUsualWeight(BigDecimal usualWeight)
    {
        this.usualWeight = usualWeight;
    }

    public BigDecimal getUsualWeight()
    {
        return usualWeight;
    }

    public void setFinalWeight(BigDecimal finalWeight)
    {
        this.finalWeight = finalWeight;
    }

    public BigDecimal getFinalWeight()
    {
        return finalWeight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("credit", getCredit())
                .append("studyHours", getStudyHours())
                .append("deptId", getDeptId())
                .append("deptName", getDeptName()) // toString 也要记得加上
                .append("usualWeight", getUsualWeight())
                .append("finalWeight", getFinalWeight())
                .toString();
    }
}