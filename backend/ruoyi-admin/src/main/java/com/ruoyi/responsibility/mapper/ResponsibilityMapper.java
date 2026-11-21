package com.ruoyi.responsibility.mapper;

import java.util.List;
import com.ruoyi.responsibility.domain.Responsibility;
import org.apache.ibatis.annotations.Param;

/**
 * 教师任课情况Mapper接口
 * 涉及表：responsibility (关联 teacher, course, department)
 * * 注意：由于该表是联合主键 (staff_id, course_id)，
 * 涉及单条数据的查询和删除必须同时传入这两个参数。
 */
public interface ResponsibilityMapper
{
    /**
     * 查询教师任课情况 (精准匹配联合主键)
     * * @param staffId 教师工号
     * @param courseId 课程号
     * @return 教师任课情况
     */
    public Responsibility selectResponsibilityByStaffIdAndCourseId(@Param("staffId") String staffId, @Param("courseId") String courseId);

    /**
     * 查询教师任课情况列表
     * 用于前端表格展示及模糊搜索（支持教师名、课程名、学院名过滤）
     *
     * @param responsibility 包含查询条件的实体
     * @return 教师任课情况集合
     */
    public List<Responsibility> selectResponsibilityList(Responsibility responsibility);

    /**
     * 新增教师任课情况
     *
     * @param responsibility 教师任课情况
     * @return 结果
     */
    public int insertResponsibility(Responsibility responsibility);

    /**
     * 修改教师任课情况
     *
     * @param responsibility 教师任课情况
     * @return 结果
     */
    public int updateResponsibility(Responsibility responsibility);

    /**
     * 删除单条教师任课情况 (根据联合主键删除)
     * 对应 XML 中的 id: deleteResponsibilityByLogic
     *
     * @param staffId 教师工号
     * @param courseId 课程号
     * @return 结果
     */
    public int deleteResponsibilityByLogic(@Param("staffId") String staffId, @Param("courseId") String courseId);

    /**
     * 批量删除教师任课情况
     *
     * @param staffIds 需要删除的教师工号数组
     * @return 结果
     */
    public int deleteResponsibilityByStaffIds(String[] staffIds);
}