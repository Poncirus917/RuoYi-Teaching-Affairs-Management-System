package com.ruoyi.courseclass.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 开课情况对象 course_class
 * @author ruoyi
 * @date 2025-12-27
 */
public class CourseClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 教学班号 */
    @Excel(name = "教学班号")
    @NotBlank(message = "教学班号不能为空")
    private String classId;

    /** 课程号 */
    @Excel(name = "课程号")
    @NotBlank(message = "课程号不能为空")
    private String courseId;

    /** 课程名称（扩展字段，由关联查询获得） */
    @Excel(name = "课程名称")
    private String courseName;

    /** 教师工号 */
    @Excel(name = "教师工号")
    @NotBlank(message = "教师工号不能为空")
    private String staffId;

    /** 教师姓名（扩展字段，由关联查询获得） */
    @Excel(name = "教师姓名")
    private String staffName;

    /** 院系ID（扩展字段，用于前端按院系筛选查询） */
    private Long deptId;

    /** 学期（增加正则约束，只允许指定的两个字符串） */
    @Excel(name = "学期")
    @NotBlank(message = "学期不能为空")
    @Pattern(regexp = "2025-2026秋季学期|2025-2026春季学期", message = "学期只能选择'2025-2026秋季学期'或'2025-2026春季学期'")
    private String semester;

    /** 上课地点 */
    @Excel(name = "上课地点")
    private String classroom;

    /** 容量 */
    @Excel(name = "容量")
    private Long capacity;

    /** 已选人数 */
    @Excel(name = "已选人数")
    private Long selectedNum;

    /** 上课星期 (1-7表示周一到周日) */
    @Excel(name = "上课星期", readConverterExp = "1=周一,2=周二,3=周三,4=周四,5=周五,6=周六,7=周日")
    private Long dayOfWeek;

    /** 上课节次（始） */
    @Excel(name = "起始节次")
    private Long startPeriod;

    /** 上课节次（末） */
    @Excel(name = "结束节次")
    private Long endPeriod;

    // Getter 和 Setter 方法保持不变 ...
    public void setClassId(String classId) { this.classId = classId; }
    public String getClassId() { return classId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public String getStaffId() { return staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getDeptId() { return deptId; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getSemester() { return semester; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public String getClassroom() { return classroom; }
    public void setCapacity(Long capacity) { this.capacity = capacity; }
    public Long getCapacity() { return capacity; }
    public void setSelectedNum(Long selectedNum) { this.selectedNum = selectedNum; }
    public Long getSelectedNum() { return selectedNum; }
    public void setDayOfWeek(Long dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Long getDayOfWeek() { return dayOfWeek; }
    public void setStartPeriod(Long startPeriod) { this.startPeriod = startPeriod; }
    public Long getStartPeriod() { return startPeriod; }
    public void setEndPeriod(Long endPeriod) { this.endPeriod = endPeriod; }
    public Long getEndPeriod() { return endPeriod; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("classId", getClassId())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("staffId", getStaffId())
                .append("staffName", getStaffName())
                .append("deptId", getDeptId())
                .append("semester", getSemester())
                .append("classroom", getClassroom())
                .append("capacity", getCapacity())
                .append("selectedNum", getSelectedNum())
                .append("dayOfWeek", getDayOfWeek())
                .append("startPeriod", getStartPeriod())
                .append("endPeriod", getEndPeriod())
                .toString();
    }
}