package com.ruoyi.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.department.mapper.DepartmentMapper;
import com.ruoyi.department.domain.Department;
import com.ruoyi.department.service.IDepartmentService;

/**
 * 院系设置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService
{
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 查询院系设置
     *
     * @param deptId 院系设置主键
     * @return 院系设置
     */
    @Override
    public Department selectDepartmentByDeptId(Long deptId)
    {
        return departmentMapper.selectDepartmentByDeptId(deptId);
    }

    /**
     * 查询院系设置列表
     *
     * @param department 院系设置
     * @return 院系设置
     */
    @Override
    public List<Department> selectDepartmentList(Department department)
    {
        return departmentMapper.selectDepartmentList(department);
    }

    /**
     * 新增院系设置
     *
     * @param department 院系设置
     * @return 结果
     */
    @Override
    public int insertDepartment(Department department)
    {
        return departmentMapper.insertDepartment(department);
    }

    /**
     * 修改院系设置
     *
     * @param department 院系设置
     * @return 结果
     */
    @Override
    public int updateDepartment(Department department)
    {
        return departmentMapper.updateDepartment(department);
    }

    /**
     * 批量删除院系设置
     *
     * @param deptIds 需要删除的院系设置主键
     * @return 结果
     */
    @Override
    public int deleteDepartmentByDeptIds(Long[] deptIds)
    {
        return departmentMapper.deleteDepartmentByDeptIds(deptIds);
    }

    /**
     * 删除院系设置信息
     *
     * @param deptId 院系设置主键
     * @return 结果
     */
    @Override
    public int deleteDepartmentByDeptId(Long deptId)
    {
        return departmentMapper.deleteDepartmentByDeptId(deptId);
    }
    @Override
    public Long selectDepartmentCount() {
        return departmentMapper.selectDepartmentCount();
    }
}
