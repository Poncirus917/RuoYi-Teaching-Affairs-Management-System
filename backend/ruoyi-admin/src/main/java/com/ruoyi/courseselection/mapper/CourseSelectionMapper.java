package com.ruoyi.courseselection.mapper;

import java.util.List;
import com.ruoyi.courseselection.domain.CourseSelection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper; // 必须引入此注解

/**
 * 选课记录Mapper接口
 * * @author ruoyi
 * @date 2025-12-28
 */
@Mapper // 添加此注解，解决 ServiceImpl 无法自动装配的问题
public interface CourseSelectionMapper
{
    /**
     * 校验学生是否在任何学期选过这门课（跨学期重复选课校验）
     * * @param studentId 学号
     * @param courseId 课程号
     * @return 选课记录数
     */
    public int countHistorySelection(@Param("studentId") String studentId, @Param("courseId") String courseId);

    /**
     * 查询选课记录 (根据复合主键：学号 + 教学班号)
     * * @param studentId 学号
     * @param classId 教学班号
     * @return 选课记录
     */
    public CourseSelection selectCourseSelectionByCompoundKey(@Param("studentId") String studentId, @Param("classId") String classId);

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
     * 修改选课记录 (内部 SQL 将根据复合主键更新)
     * * @param courseSelection 选课记录
     * @return 结果
     */
    public int updateCourseSelection(CourseSelection courseSelection);

    /**
     * 删除选课记录 (根据复合主键)
     * * @param studentId 学号
     * @param classId 教学班号
     * @return 结果
     */
    public int deleteCourseSelectionByCompoundKey(@Param("studentId") String studentId, @Param("classId") String classId);

    /**
     * 批量删除选课记录
     * 注意：复合主键场景下，建议使用单条循环删除
     */
    public int deleteCourseSelectionByStudentIds(String[] studentIds);

    public int countSelectionByClassId(String classId);
}