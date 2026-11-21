package com.ruoyi.student.service;

import java.util.List;
import java.util.Map; // 1. 必须导入 Map 用于处理饼图数据
import com.ruoyi.student.domain.Student;

/**
 * 学生Service接口
 * * @author ruoyi
 * @date 2025-12-28
 */
public interface IStudentService
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
     * 批量删除学生
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /**
     * 删除学生信息
     */
    public int deleteStudentByStudentId(String studentId);

    // ================= 以下是为首页统计新增加的方法声明 =================

    /**
     * 统计全校学生总数
     * * @return 学生总数
     */
    public Long selectStudentCount();

    /**
     * 统计各学院学生人数
     * * @return 包含学院名称和人数的列表
     */
    public List<Map<String, Object>> selectStudentCountByDept();

    /**
     * 查询学生个人各学期成绩及总成绩统计
     * * @param studentId 学生学号
     * @return 统计列表
     */
    public List<Student> selectStudentGpaStats(String studentId);
}