package com.ruoyi.responsibility.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师任课情况对象 responsibility
 *
 * @author ruoyi
 * @date 2025-12-27
 */
public class Responsibility extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 教师工号 */
    @Excel(name = "教师工号")
    @NotBlank(message = "教师工号不能为空")
    @Size(min = 8, max = 8, message = "教师工号必须为 8 位")
    private String staffId;

    /** 课程号 */
    @Excel(name = "课程号")
    @NotBlank(message = "课程号不能为空")
    @Size(min = 8, max = 8, message = "课程号必须为 8 位")
    private String courseId;

    /** 用于处理修改逻辑的旧课程号 */
    private String oldCourseId;

    /** 扩展字段：教师姓名 */
    private String teacherName;

    /** 扩展字段：课程名称 */
    private String courseName;

    /** 扩展字段：学院名称 */
    private String deptName;

    /** 扩展字段：学院ID（用于搜索过滤） */
    private Long deptId;

    // --- Getter and Setter ---

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getOldCourseId() {
        return oldCourseId;
    }

    public void setOldCourseId(String oldCourseId) {
        this.oldCourseId = oldCourseId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("staffId", getStaffId())
                .append("courseId", getCourseId())
                .append("oldCourseId", getOldCourseId())
                .append("teacherName", getTeacherName())
                .append("courseName", getCourseName())
                .append("deptName", getDeptName())
                .append("deptId", getDeptId())
                .toString();
    }
}