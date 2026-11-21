package com.ruoyi.department.service;

import java.util.List;
import com.ruoyi.department.domain.Department;

/**
 * 院系设置Service接口
 *
 * @author ruoyi
 * @date 2025-12-26
 */
public interface IDepartmentService
{
    /**
     * 查询院系设置
     *
     * @param deptId 院系设置主键
     * @return 院系设置
     */
    public Department selectDepartmentByDeptId(Long deptId);

    /**
     * 查询院系设置列表
     *
     * @param department 院系设置
     * @return 院系设置集合
     */
    public List<Department> selectDepartmentList(Department department);

    /**
     * 新增院系设置
     *
     * @param department 院系设置
     * @return 结果
     */
    public int insertDepartment(Department department);

    /**
     * 修改院系设置
     *
     * @param department 院系设置
     * @return 结果
     */
    public int updateDepartment(Department department);

    /**
     * 批量删除院系设置
     *
     * @param deptIds 需要删除的院系设置主键集合
     * @return 结果
     */
    public int deleteDepartmentByDeptIds(Long[] deptIds);

    /**
     * 删除院系设置信息
     *
     * @param deptId 院系设置主键
     * @return 结果
     */
    public int deleteDepartmentByDeptId(Long deptId);
    public Long selectDepartmentCount();
}
