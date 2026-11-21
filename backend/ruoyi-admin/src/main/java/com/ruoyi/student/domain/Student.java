package com.ruoyi.student.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生对象 student
 * * @author ruoyi
 * @date 2025-12-28
 */
public class Student extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String studentName;

    /** 性别 */
    @Excel(name = "性别")
    private String sex;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateOfBirth;

    /** 手机号 */
    @Excel(name = "手机号")
    private String mobilePhone;

    /** 院系号 */
    @Excel(name = "院系号")
    private Long deptId;

    /** 院系名称 (新增) */
    @Excel(name = "所属学院")
    private String deptName;

    // ================= 以下是为学生个人成绩统计新增的字段 =================

    /** 学期名称 (对应 SQL 的 semester_name) */
    private String semesterName;

    /** 累计学分 (对应 SQL 的 total_credits) */
    private Double totalCredits;

    /** 已获得学分 (对应 SQL 的 earned_credits) - 这是新增的 */
    private Double earnedCredits;

    /** 平均绩点 (对应 SQL 的 avg_gpa) */
    private Double avgGpa;

    /** 排序字段 (对应 SQL 的 sort_order) */
    private Integer sortOrder;

    // =================================================================

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSex()
    {
        return sex;
    }

    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

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

    // 新增字段的 Getter 和 Setter 方法

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = totalCredits;
    }

    public Double getEarnedCredits() {
        return earnedCredits;
    }

    public void setEarnedCredits(Double earnedCredits) {
        this.earnedCredits = earnedCredits;
    }

    public Double getAvgGpa() {
        return avgGpa;
    }

    public void setAvgGpa(Double avgGpa) {
        this.avgGpa = avgGpa;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("studentId", getStudentId())
                .append("studentName", getStudentName())
                .append("sex", getSex())
                .append("dateOfBirth", getDateOfBirth())
                .append("mobilePhone", getMobilePhone())
                .append("deptId", getDeptId())
                .append("deptName", getDeptName())
                .append("semesterName", getSemesterName())
                .append("totalCredits", getTotalCredits())
                .append("avgGpa", getAvgGpa())
                .toString();
    }
}