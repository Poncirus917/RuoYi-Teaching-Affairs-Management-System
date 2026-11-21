package com.ruoyi.responsibility.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.responsibility.mapper.ResponsibilityMapper;
import com.ruoyi.responsibility.domain.Responsibility;
import com.ruoyi.responsibility.service.IResponsibilityService;
import com.ruoyi.teacher.service.ITeacherService;
import com.ruoyi.teacher.domain.Teacher;
import com.ruoyi.course.service.ICourseService;
import com.ruoyi.course.domain.Course;
import javax.annotation.Resource;

/**
 * 教师任课情况Service业务层处理
 */
@Service
public class ResponsibilityServiceImpl implements IResponsibilityService
{
    @Resource
    private ResponsibilityMapper responsibilityMapper;

    @Autowired
    private ITeacherService teacherService;

    @Autowired
    private ICourseService courseService;

    /**
     * 查询教师任课情况 (精确匹配)
     */
    @Override
    public Responsibility selectResponsibilityByStaffIdAndCourseId(Responsibility responsibility)
    {
        return responsibilityMapper.selectResponsibilityByStaffIdAndCourseId(
                responsibility.getStaffId(),
                responsibility.getCourseId()
        );
    }

    /**
     * 查询教师任课情况列表
     */
    @Override
    public List<Responsibility> selectResponsibilityList(Responsibility responsibility)
    {
        return responsibilityMapper.selectResponsibilityList(responsibility);
    }

    /**
     * 新增教师任课情况
     */
    @Transactional
    @Override
    public int insertResponsibility(Responsibility responsibility)
    {
        // 1. 校验基础数据是否存在
        Teacher teacher = teacherService.selectTeacherByStaffId(responsibility.getStaffId());
        Course course = courseService.selectCourseByCourseId(responsibility.getCourseId());

        if (teacher == null || course == null) {
            throw new ServiceException("新增失败：教师或课程信息不存在");
        }

        // 2. 核心约束：校验学院一致性
        Long tDeptId = teacher.getDeptId();
        Long cDeptId = course.getDeptId();
        if (tDeptId == null || cDeptId == null || !tDeptId.equals(cDeptId)) {
            throw new ServiceException("操作失败：教师与课程所属学院不匹配！");
        }

        // 3. 校验重复记录
        Responsibility exist = responsibilityMapper.selectResponsibilityByStaffIdAndCourseId(
                responsibility.getStaffId(),
                responsibility.getCourseId()
        );
        if (exist != null) {
            throw new ServiceException("新增失败：该任课关系已存在，请勿重复添加");
        }

        return responsibilityMapper.insertResponsibility(responsibility);
    }

    /**
     * 修改教师任课情况
     * 由于涉及联合主键修改，采用【先删旧、后增新】的原子操作
     */
    @Transactional
    @Override
    public int updateResponsibility(Responsibility responsibility)
    {
        // 1. 校验新数据的合法性
        Teacher teacher = teacherService.selectTeacherByStaffId(responsibility.getStaffId());
        Course course = courseService.selectCourseByCourseId(responsibility.getCourseId());

        if (teacher == null || course == null) {
            throw new ServiceException("修改失败：所选教师或课程不存在");
        }

        if (teacher.getDeptId() == null || !teacher.getDeptId().equals(course.getDeptId())) {
            throw new ServiceException("修改失败：教师与课程不属于同一学院");
        }

        // 2. 删除原有的任课记录 (防止出现主键冲突)
        // 使用前端传回来的 oldCourseId 来确定要删哪一条
        String oldCourseId = responsibility.getOldCourseId();
        if (oldCourseId != null && !oldCourseId.isEmpty()) {
            responsibilityMapper.deleteResponsibilityByLogic(responsibility.getStaffId(), oldCourseId);
        } else {
            // 如果没传 oldCourseId，尝试用当前的 courseId 兜底（通常发生在只改教师不改课程的情况）
            responsibilityMapper.deleteResponsibilityByLogic(responsibility.getStaffId(), responsibility.getCourseId());
        }

        // 3. 插入新记录
        return responsibilityMapper.insertResponsibility(responsibility);
    }

    /**
     * 删除单条教师任课情况
     */
    @Override
    public int deleteResponsibility(Responsibility responsibility)
    {
        return responsibilityMapper.deleteResponsibilityByLogic(
                responsibility.getStaffId(),
                responsibility.getCourseId()
        );
    }

    /**
     * 批量删除
     */
    @Override
    public int deleteResponsibilityByStaffIds(String[] staffIds)
    {
        return responsibilityMapper.deleteResponsibilityByStaffIds(staffIds);
    }
}