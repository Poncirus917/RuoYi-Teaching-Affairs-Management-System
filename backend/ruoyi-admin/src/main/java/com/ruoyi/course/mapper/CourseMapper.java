package com.ruoyi.course.mapper;

import java.util.List;
import com.ruoyi.course.domain.Course;
import org.apache.ibatis.annotations.Mapper; // 1. 引入注解类

/**
 * 课程信息Mapper接口
 * * @author ruoyi
 * @date 2025-12-27
 */
@Mapper // 2. 添加注解，告诉 Spring 这是一个 MyBatis 的 Mapper 接口
public interface CourseMapper
{
    /**
     * 查询课程信息
     * * @param courseId 课程信息主键
     * @return 课程信息
     */
    public Course selectCourseByCourseId(String courseId);

    /**
     * 查询课程信息列表
     * 
     * @param course 课程信息
     * @return 课程信息集合
     */
    public List<Course> selectCourseList(Course course);

    /**
     * 新增课程信息
     * 
     * @param course 课程信息
     * @return 结果
     */
    public int insertCourse(Course course);

    /**
     * 修改课程信息
     * 
     * @param course 课程信息
     * @return 结果
     */
    public int updateCourse(Course course);

    /**
     * 删除课程信息
     * 
     * @param courseId 课程信息主键
     * @return 结果
     */
    public int deleteCourseByCourseId(String courseId);

    /**
     * 批量删除课程信息
     * 
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCourseByCourseIds(String[] courseIds);
    public Long selectCourseCount();
}
