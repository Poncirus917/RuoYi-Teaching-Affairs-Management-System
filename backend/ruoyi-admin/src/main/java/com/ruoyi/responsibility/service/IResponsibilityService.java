package com.ruoyi.responsibility.service;

import java.util.List;
import com.ruoyi.responsibility.domain.Responsibility;

/**
 * 教师任课情况Service接口
 * * @author ruoyi
 * @date 2025-12-27
 */
public interface IResponsibilityService
{
    /**
     * 查询教师任课情况 (联合主键精准查询)
     * 用于修改页面回显数据，根据教师工号和课程号唯一定位记录
     *
     * @param responsibility 包含 staffId 和 courseId 的对象
     * @return 教师任课情况
     */
    public Responsibility selectResponsibilityByStaffIdAndCourseId(Responsibility responsibility);

    /**
     * 查询教师任课情况列表
     * 支持教师工号、课程号、教师姓名、课程名、学院ID的组合查询
     *
     * @param responsibility 包含查询条件的实体
     * @return 教师任课情况集合
     */
    public List<Responsibility> selectResponsibilityList(Responsibility responsibility);

    /**
     * 新增教师任课情况
     * 包含：学院一致性约束检查、主键重复性检查
     *
     * @param responsibility 教师任课情况
     * @return 结果
     */
    public int insertResponsibility(Responsibility responsibility);

    /**
     * 修改教师任课情况
     * 处理逻辑：根据 staffId 和 oldCourseId 删除旧记录，再插入新记录
     *
     * @param responsibility 教师任课情况
     * @return 结果
     */
    public int updateResponsibility(Responsibility responsibility);

    /**
     * 删除教师任课情况信息 (联合主键精准删除)
     *
     * @param responsibility 包含 staffId 和 courseId 的对象
     * @return 结果
     */
    public int deleteResponsibility(Responsibility responsibility);

    /**
     * 批量删除教师任课情况
     *
     * @param staffIds 需要删除的教师工号集合
     * @return 结果
     */
    public int deleteResponsibilityByStaffIds(String[] staffIds);
}