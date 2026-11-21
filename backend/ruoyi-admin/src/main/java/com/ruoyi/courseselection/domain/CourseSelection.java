package com.ruoyi.courseselection.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 选课记录对象 course_selection
 * * @author ruoyi
 * @date 2025-12-28
 */
public class CourseSelection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 学生姓名 (扩展字段) */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 学期 */
    @Excel(name = "学期")
    private String semester;

    /** 课程号 */
    @Excel(name = "课程号")
    private String courseId;

    /** 课程名称 (扩展字段) */
    @Excel(name = "课程名称")
    private String courseName;

    /** 教师工号 */
    @Excel(name = "教师工号")
    private String staffId;

    /** 教师姓名 (扩展字段) */
    @Excel(name = "教师姓名")
    private String staffName;

    /** 教学班号 */
    @Excel(name = "教学班号")
    private String classId;

    /** 星期几 (扩展字段) */
    @Excel(name = "星期", readConverterExp = "1=周一,2=周二,3=周三,4=周四,5=周五,6=周六,7=周日")
    private Long dayOfWeek;

    /** 起始节次 (扩展字段) */
    @Excel(name = "开始节次")
    private Long startPeriod;

    /** 结束节次 (扩展字段) */
    @Excel(name = "结束节次")
    private Long endPeriod;

    /** 平时成绩 */
    @Excel(name = "平时成绩")
    private Long normalScore;

    /** 期末成绩 */
    @Excel(name = "期末成绩")
    private Long testScore;

    /** 总成绩 */
    @Excel(name = "总成绩")
    private Long totalScore;

    /** 绩点 (新增字段：由数据库自动计算) */
    @Excel(name = "绩点")
    private Double gpa;

    /** 是否为编辑模式 (用于前后端逻辑判断) */
    private boolean isEdit;

    // --- Getter 和 Setter 方法 ---

    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentId() { return studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public void setSemester(String semester) { this.semester = semester; }
    public String getSemester() { return semester; }

    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseId() { return courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffId() { return staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public void setClassId(String classId) { this.classId = classId; }
    public String getClassId() { return classId; }

    public Long getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Long dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Long getStartPeriod() { return startPeriod; }
    public void setStartPeriod(Long startPeriod) { this.startPeriod = startPeriod; }

    public Long getEndPeriod() { return endPeriod; }
    public void setEndPeriod(Long endPeriod) { this.endPeriod = endPeriod; }

    public void setNormalScore(Long normalScore) { this.normalScore = normalScore; }
    public Long getNormalScore() { return normalScore; }

    public void setTestScore(Long testScore) { this.testScore = testScore; }
    public Long getTestScore() { return testScore; }

    public void setTotalScore(Long totalScore) { this.totalScore = totalScore; }
    public Long getTotalScore() { return totalScore; }

    public void setGpa(Double gpa) { this.gpa = gpa; }
    public Double getGpa() { return gpa; }

    public boolean isEdit() { return isEdit; }
    public void setEdit(boolean edit) { isEdit = edit; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("studentId", getStudentId())
                .append("studentName", getStudentName())
                .append("semester", getSemester())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("staffId", getStaffId())
                .append("staffName", getStaffName())
                .append("classId", getClassId())
                .append("normalScore", getNormalScore())
                .append("testScore", getTestScore())
                .append("totalScore", getTotalScore())
                .append("gpa", getGpa())
                .append("isEdit", isEdit())
                .toString();
    }
}