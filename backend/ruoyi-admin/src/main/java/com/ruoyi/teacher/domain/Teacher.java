package com.ruoyi.teacher.domain;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师信息对象 teacher
 * * @author ruoyi
 * @date 2025-12-26
 */
public class Teacher extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 教师工号 */
    @NotBlank(message = "教师工号不能为空")
    @Size(min = 8, max = 8, message = "教师工号必须为 8 位")
    @Excel(name = "教师工号")
    private String staffId;

    /** 教师姓名 */
    @NotBlank(message = "教师姓名不能为空")
    @Size(max = 30, message = "教师姓名长度不能超过 30 个字符")
    @Excel(name = "教师姓名")
    private String teacherName;

    /** 性  别 */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "男|女|未知", message = "性别必须为 男、女 或 未知")
    private String sex;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    /** 职称 */
    @Size(max = 10, message = "职称长度不能超过 10 个字符")
    @Excel(name = "职称")
    private String professionalRank;

    /** 院系号 */
    @Excel(name = "院系号")
    private Long deptId;

    private String deptName;

    // ================= 新增统计相关字段 (用于统计图展示) =================

    private String classId;       // 班级ID

    // 平时分统计
    private Integer normalLow;    // <60
    private Integer normalMid;    // 60-85
    private Integer normalHigh;   // 85-100

    // 期末分统计
    private Integer testFail;     // <60
    private Integer testPass;     // 60-70
    private Integer testGood;     // 70-85
    private Integer testExcellent;// 85-100

    // GPA统计
    private Integer gpa0;         // 0.0
    private Integer gpa12;        // 1.0-2.3
    private Integer gpa23;        // 2.7-3.3
    private Integer gpa34;        // 3.7-4.0

    // ================= Getter & Setter =================

    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffId() { return staffId; }

    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getTeacherName() { return teacherName; }

    public void setSex(String sex) { this.sex = sex; }
    public String getSex() { return sex; }

    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Date getDateOfBirth() { return dateOfBirth; }

    public void setProfessionalRank(String professionalRank) { this.professionalRank = professionalRank; }
    public String getProfessionalRank() { return professionalRank; }

    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }

    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptName() { return deptName; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public Integer getNormalLow() { return normalLow; }
    public void setNormalLow(Integer normalLow) { this.normalLow = normalLow; }

    public Integer getNormalMid() { return normalMid; }
    public void setNormalMid(Integer normalMid) { this.normalMid = normalMid; }

    public Integer getNormalHigh() { return normalHigh; }
    public void setNormalHigh(Integer normalHigh) { this.normalHigh = normalHigh; }

    public Integer getTestFail() { return testFail; }
    public void setTestFail(Integer testFail) { this.testFail = testFail; }

    public Integer getTestPass() { return testPass; }
    public void setTestPass(Integer testPass) { this.testPass = testPass; }

    public Integer getTestGood() { return testGood; }
    public void setTestGood(Integer testGood) { this.testGood = testGood; }

    public Integer getTestExcellent() { return testExcellent; }
    public void setTestExcellent(Integer testExcellent) { this.testExcellent = testExcellent; }

    public Integer getGpa0() { return gpa0; }
    public void setGpa0(Integer gpa0) { this.gpa0 = gpa0; }

    public Integer getGpa12() { return gpa12; }
    public void setGpa12(Integer gpa12) { this.gpa12 = gpa12; }

    public Integer getGpa23() { return gpa23; }
    public void setGpa23(Integer gpa23) { this.gpa23 = gpa23; }

    public Integer getGpa34() { return gpa34; }
    public void setGpa34(Integer gpa34) { this.gpa34 = gpa34; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("staffId", getStaffId())
                .append("teacherName", getTeacherName())
                .append("sex", getSex())
                .append("dateOfBirth", getDateOfBirth())
                .append("professionalRank", getProfessionalRank())
                .append("deptId", getDeptId())
                .append("classId", getClassId())
                .toString();
    }
}