package com.ruoyi.courseclass.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper; // 必须增加这个导入
import com.ruoyi.courseclass.domain.CourseClass;

/**
 * 开课情况Mapper接口
 * * @author ruoyi
 * @date 2025-12-27
 */
@Mapper // 必须增加这个注解，让 Spring 能够识别并自动装配
public interface CourseClassMapper
{
    /**
     * 查询开课情况
     * * @param classId 开课情况主键
     * @return 开课情况
     */
    public CourseClass selectCourseClassByClassId(String classId);

    /**
     * 查询开课情况列表
     * * @param courseClass 开课情况
     * @return 开课情况集合
     */
    public List<CourseClass> selectCourseClassList(CourseClass courseClass);

    /**
     * 新增开课情况
     * * @param courseClass 开课情况
     * @return 结果
     */
    public int insertCourseClass(CourseClass courseClass);

    /**
     * 修改开课情况
     * * @param courseClass 开课情况
     * @return 结果
     */
    public int updateCourseClass(CourseClass courseClass);

    /**
     * 删除开课情况
     * * @param classId 开课情况主键
     * @return 结果
     */
    public int deleteCourseClassByClassId(String classId);

    /**
     * 批量删除开课情况
     * * @param classIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCourseClassByClassIds(String[] classIds);
}