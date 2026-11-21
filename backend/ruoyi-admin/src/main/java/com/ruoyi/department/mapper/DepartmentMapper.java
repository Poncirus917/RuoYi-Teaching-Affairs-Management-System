package com.ruoyi.department.mapper;

import java.util.List;
import com.ruoyi.department.domain.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 院系设置Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-26
 */
@Mapper
public interface DepartmentMapper
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
     * 删除院系设置
     *
     * @param deptId 院系设置主键
     * @return 结果
     */
    public int deleteDepartmentByDeptId(Long deptId);

    /**
     * 批量删除院系设置
     *
     * @param deptIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDepartmentByDeptIds(Long[] deptIds);
    public Long selectDepartmentCount();
}
