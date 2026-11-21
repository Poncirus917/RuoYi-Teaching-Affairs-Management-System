package com.ruoyi.teacher.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.teacher.domain.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师信息Mapper接口
 * * @author ruoyi
 * @date 2025-12-26
 */
@Mapper
public interface TeacherMapper
{
    /**
     * 查询教师信息
     */
    public Teacher selectTeacherByStaffId(String staffId);

    /**
     * 查询教师信息列表
     */
    public List<Teacher> selectTeacherList(Teacher teacher);

    /**
     * 新增教师信息
     */
    public int insertTeacher(Teacher teacher);

    /**
     * 修改教师信息
     */
    public int updateTeacher(Teacher teacher);

    /**
     * 删除教师信息
     */
    public int deleteTeacherByStaffId(String staffId);

    /**
     * 批量删除教师信息
     */
    public int deleteTeacherByStaffIds(String[] staffIds);

    /** 统计教师总数 */
    public Long selectTeacherCount();

    /** 统计各学院教师人数 */
    public List<Map<String, Object>> selectTeacherCountByDept();

    /** 统计职称分布 */
    public List<Map<String, Object>> selectTeacherTitleDistribution();

    // ================= 新增：查询教师任课班级的成绩分布统计 =================

    /**
     * 查询教师所带班级的成绩分布统计
     * * @param staffId 教师工号
     * @return 包含统计数据的教师对象列表（每个班级对应列表中的一项）
     */
    public List<Teacher> selectTeacherClassStats(String staffId);
}