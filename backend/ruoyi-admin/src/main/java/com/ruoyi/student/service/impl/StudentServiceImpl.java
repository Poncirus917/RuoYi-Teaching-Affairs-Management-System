package com.ruoyi.student.service.impl;

import java.util.List;
import java.util.Map; // 1. 记得导入 Map
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.student.mapper.StudentMapper;
import com.ruoyi.student.domain.Student;
import com.ruoyi.student.service.IStudentService;

/**
 * 学生Service业务层处理
 * * @author ruoyi
 * @date 2025-12-28
 */
@Service
public class StudentServiceImpl implements IStudentService
{
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 查询学生
     */
    @Override
    public Student selectStudentByStudentId(String studentId)
    {
        return studentMapper.selectStudentByStudentId(studentId);
    }

    /**
     * 查询学生列表
     */
    @Override
    public List<Student> selectStudentList(Student student)
    {
        return studentMapper.selectStudentList(student);
    }

    /**
     * 新增学生
     */
    @Override
    public int insertStudent(Student student)
    {
        return studentMapper.insertStudent(student);
    }

    /**
     * 修改学生
     */
    @Override
    public int updateStudent(Student student)
    {
        return studentMapper.updateStudent(student);
    }

    /**
     * 批量删除学生
     */
    @Override
    public int deleteStudentByStudentIds(String[] studentIds)
    {
        return studentMapper.deleteStudentByStudentIds(studentIds);
    }

    /**
     * 删除学生信息
     */
    @Override
    public int deleteStudentByStudentId(String studentId)
    {
        return studentMapper.deleteStudentByStudentId(studentId);
    }

    // ================= 新增统计方法实现 =================

    /**
     * 统计全校学生总数
     */
    @Override
    public Long selectStudentCount()
    {
        return studentMapper.selectStudentCount();
    }

    /**
     * 统计各学院学生人数
     */
    @Override
    public List<Map<String, Object>> selectStudentCountByDept()
    {
        return studentMapper.selectStudentCountByDept();
    }

    /**
     * 查询学生个人各学期成绩及总成绩统计实现
     */
    @Override
    public List<Student> selectStudentGpaStats(String studentId)
    {
        return studentMapper.selectStudentGpaStats(studentId);
    }
}