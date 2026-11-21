package com.ruoyi.teacher.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.department.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.teacher.mapper.TeacherMapper;
import com.ruoyi.teacher.domain.Teacher;
import com.ruoyi.teacher.service.ITeacherService;

/**
 * 教师信息Service业务层处理
 * * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class TeacherServiceImpl implements ITeacherService
{
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private IDepartmentService departmentService;

    /**
     * 查询教师信息
     */
    @Override
    public Teacher selectTeacherByStaffId(String staffId)
    {
        return teacherMapper.selectTeacherByStaffId(staffId);
    }

    /**
     * 查询教师信息列表
     */
    @Override
    public List<Teacher> selectTeacherList(Teacher teacher)
    {
        return teacherMapper.selectTeacherList(teacher);
    }

    /**
     * 新增教师信息
     */
    @Override
    public int insertTeacher(Teacher teacher)
    {
        if (teacher.getDeptId() != null)
        {
            if (departmentService.selectDepartmentByDeptId(teacher.getDeptId()) == null)
            {
                throw new ServiceException("操作失败：院系号 [" + teacher.getDeptId() + "] 不存在！");
            }
        }
        return teacherMapper.insertTeacher(teacher);
    }

    /**
     * 修改教师信息
     */
    @Override
    public int updateTeacher(Teacher teacher)
    {
        if (teacher.getDeptId() != null)
        {
            if (departmentService.selectDepartmentByDeptId(teacher.getDeptId()) == null)
            {
                throw new ServiceException("操作失败：院系号 [" + teacher.getDeptId() + "] 不存在！");
            }
        }
        return teacherMapper.updateTeacher(teacher);
    }

    /**
     * 批量删除教师信息
     */
    @Override
    public int deleteTeacherByStaffIds(String[] staffIds)
    {
        return teacherMapper.deleteTeacherByStaffIds(staffIds);
    }

    /**
     * 删除教师信息信息
     */
    @Override
    public int deleteTeacherByStaffId(String staffId)
    {
        return teacherMapper.deleteTeacherByStaffId(staffId);
    }

    /**
     * 统计全校教师总数
     */
    @Override
    public Long selectTeacherCount()
    {
        return teacherMapper.selectTeacherCount();
    }

    /**
     * 统计各学院教师人数
     */
    @Override
    public List<Map<String, Object>> selectTeacherCountByDept()
    {
        return teacherMapper.selectTeacherCountByDept();
    }

    /**
     * 统计教师职称分布
     */
    @Override
    public List<Map<String, Object>> selectTeacherTitleDistribution()
    {
        return teacherMapper.selectTeacherTitleDistribution();
    }

    // ================= 新增：查询教师任课班级的成绩分布统计实现 =================

    /**
     * 查询教师所带班级的成绩分布统计
     * * @param staffId 教师工号
     * @return 包含统计数据的列表
     */
    @Override
    public List<Teacher> selectTeacherClassStats(String staffId)
    {
        return teacherMapper.selectTeacherClassStats(staffId);
    }
}