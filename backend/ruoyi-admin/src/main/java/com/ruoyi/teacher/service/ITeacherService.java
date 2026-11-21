package com.ruoyi.teacher.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.teacher.domain.Teacher;

/**
 * 教师信息Service接口
 * * @author ruoyi
 * @date 2025-12-26
 */
public interface ITeacherService
{
    /**
     * 查询教师信息
     * * @param staffId 教师信息主键
     * @return 教师信息
     */
    public Teacher selectTeacherByStaffId(String staffId);

    /**
     * 查询教师信息列表
     * * @param teacher 教师信息
     * @return 教师信息集合
     */
    public List<Teacher> selectTeacherList(Teacher teacher);

    /**
     * 新增教师信息
     * * @param teacher 教师信息
     * @return 结果
     */
    public int insertTeacher(Teacher teacher);

    /**
     * 修改教师信息
     * * @param teacher 教师信息
     * @return 结果
     */
    public int updateTeacher(Teacher teacher);

    /**
     * 批量删除教师信息
     * * @param staffIds 需要删除的教师信息主键集合
     * @return 结果
     */
    public int deleteTeacherByStaffIds(String[] staffIds);

    /**
     * 删除教师信息信息
     * * @param staffId 教师信息主键
     * @return 结果
     */
    public int deleteTeacherByStaffId(String staffId);

    /** 统计教师总数 */
    public Long selectTeacherCount();

    /** 统计各学院教师人数 */
    public List<Map<String, Object>> selectTeacherCountByDept();

    /** 统计教师职称分布 */
    public List<Map<String, Object>> selectTeacherTitleDistribution();

    // ================= 新增：查询教师任课班级的成绩分布统计 =================

    /**
     * 查询教师所带班级的成绩分布统计
     * * @param staffId 教师工号
     * @return 包含统计数据的教师对象列表
     */
    public List<Teacher> selectTeacherClassStats(String staffId);
}