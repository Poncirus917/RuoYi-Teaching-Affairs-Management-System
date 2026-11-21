package com.ruoyi.courseselection.service;

import java.util.List;
import com.ruoyi.courseselection.domain.CourseSelection;

/**
 * 选课记录Service接口
 * * @author ruoyi
 * @date 2025-12-28
 */
public interface ICourseSelectionService
{
    /**
     * 校验学生是否在历史学期选过这门课
     * * @param studentId 学号
     * @param courseId 课程号
     * @return 选课次数
     */
    public int countHistorySelection(String studentId, String courseId);

    /**
     * 查询选课记录 (复合主键)
     * * @param studentId 学号
     * @param classId 教学班号
     * @return 选课记录
     */
    public CourseSelection selectCourseSelectionByCompoundKey(String studentId, String classId);

    /**
     * 查询选课记录列表
     * * @param courseSelection 选课记录
     * @return 选课记录集合
     */
    public List<CourseSelection> selectCourseSelectionList(CourseSelection courseSelection);

    /**
     * 新增选课记录
     * * @param courseSelection 选课记录
     * @return 结果
     */
    public int insertCourseSelection(CourseSelection courseSelection);

    /**
     * 修改选课记录
     * * @param courseSelection 选课记录
     * @return 结果
     */
    public int updateCourseSelection(CourseSelection courseSelection);

    /**
     * 批量删除选课记录
     * * @param studentIds 需要删除的选课记录主键集合
     * @return 结果
     */
    public int deleteCourseSelectionByStudentIds(String[] studentIds);

    /**
     * 删除选课记录信息 (复合主键)
     * * @param studentId 学号
     * @param classId 教学班号
     * @return 结果
     */
    public int deleteCourseSelectionByCompoundKey(String studentId, String classId);

    /**
     * 查询某个教学班的选课人数
     * * @param classId 教学班号
     * @return 选课人数
     */
    public int countSelectionByClassId(String classId);
}