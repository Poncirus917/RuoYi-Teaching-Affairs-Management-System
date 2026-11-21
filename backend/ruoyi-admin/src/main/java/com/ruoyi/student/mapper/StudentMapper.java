package com.ruoyi.student.mapper;

import java.util.List;
import java.util.Map; // 确保导入了 Map
import org.apache.ibatis.annotations.Mapper; // 1. 必须导入这个注解类
import com.ruoyi.student.domain.Student;

/**
 * 学生Mapper接口
 * * @author ruoyi
 * @date 2025-12-28
 */
@Mapper // 2. 加上这个注解，解决“无法自动装配”的问题
public interface StudentMapper
{
    /**
     * 查询学生
     */
    public Student selectStudentByStudentId(String studentId);

    /**
     * 查询学生列表
     */
    public List<Student> selectStudentList(Student student);

    /**
     * 新增学生
     */
    public int insertStudent(Student student);

    /**
     * 修改学生
     */
    public int updateStudent(Student student);

    /**
     * 删除学生
     */
    public int deleteStudentByStudentId(String studentId);

    /**
     * 批量删除学生
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /** * 统计学生总数
     */
    public Long selectStudentCount();

    /** * 统计各学院学生人数
     * 返回结果为 [{name: '计算机学院', value: 100}, ...]
     */
    public List<Map<String, Object>> selectStudentCountByDept();

    /**
     * 查询学生个人各学期成绩及总成绩统计
     * * @param studentId 学生学号
     * @return 包含统计结果的学生对象列表
     */
    public List<Student> selectStudentGpaStats(String studentId);
}