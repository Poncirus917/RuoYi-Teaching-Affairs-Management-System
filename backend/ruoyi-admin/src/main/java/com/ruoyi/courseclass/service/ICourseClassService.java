package com.ruoyi.courseclass.service;

import java.util.List;
import com.ruoyi.courseclass.domain.CourseClass;

/**
 * 开课情况Service接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
public interface ICourseClassService 
{
    /**
     * 查询开课情况
     * 
     * @param classId 开课情况主键
     * @return 开课情况
     */
    public CourseClass selectCourseClassByClassId(String classId);

    /**
     * 查询开课情况列表
     * 
     * @param courseClass 开课情况
     * @return 开课情况集合
     */
    public List<CourseClass> selectCourseClassList(CourseClass courseClass);

    /**
     * 新增开课情况
     * 
     * @param courseClass 开课情况
     * @return 结果
     */
    public int insertCourseClass(CourseClass courseClass);

    /**
     * 修改开课情况
     * 
     * @param courseClass 开课情况
     * @return 结果
     */
    public int updateCourseClass(CourseClass courseClass);

    /**
     * 批量删除开课情况
     * 
     * @param classIds 需要删除的开课情况主键集合
     * @return 结果
     */
    public int deleteCourseClassByClassIds(String[] classIds);

    /**
     * 删除开课情况信息
     * 
     * @param classId 开课情况主键
     * @return 结果
     */
    public int deleteCourseClassByClassId(String classId);
}
