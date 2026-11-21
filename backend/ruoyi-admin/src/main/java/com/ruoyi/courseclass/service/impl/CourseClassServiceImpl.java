package com.ruoyi.courseclass.service.impl;

import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.courseclass.mapper.CourseClassMapper;
import com.ruoyi.courseclass.domain.CourseClass;
import com.ruoyi.courseclass.service.ICourseClassService;

/**
 * 开课情况Service业务层处理
 */
@Service
public class CourseClassServiceImpl implements ICourseClassService
{
    @Autowired
    private CourseClassMapper courseClassMapper;

    @Override
    public CourseClass selectCourseClassByClassId(String classId)
    {
        return courseClassMapper.selectCourseClassByClassId(classId);
    }

    @Override
    public List<CourseClass> selectCourseClassList(CourseClass courseClass)
    {
        return courseClassMapper.selectCourseClassList(courseClass);
    }

    /**
     * 新增开课情况
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCourseClass(CourseClass courseClass)
    {
        // 1. 初始新增时，强制已选人数为0
        courseClass.setSelectedNum(0L);
        // 2. 冲突校验
        checkSchedulingConflict(courseClass);
        return courseClassMapper.insertCourseClass(courseClass);
    }

    /**
     * 修改开课情况
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCourseClass(CourseClass courseClass)
    {
        // 1. 安全保护：获取数据库中的原始记录
        CourseClass oldClass = courseClassMapper.selectCourseClassByClassId(courseClass.getClassId());
        if (oldClass == null) {
            throw new ServiceException("该开课班级不存在");
        }

        // 2. 逻辑保护：禁止通过此接口修改“已选人数”，强制保持原始值
        // 该数值应仅由数据库触发器维护
        courseClass.setSelectedNum(oldClass.getSelectedNum());

        // 3. 业务保护：修改的“容量”不能小于“当前已选人数”
        if (courseClass.getCapacity() != null && courseClass.getCapacity() < oldClass.getSelectedNum()) {
            throw new ServiceException(String.format(
                    "容量修改失败：新容量(%d)不能小于当前已选人数(%d)",
                    courseClass.getCapacity(), oldClass.getSelectedNum()
            ));
        }

        // 4. 排课冲突校验
        checkSchedulingConflict(courseClass);

        return courseClassMapper.updateCourseClass(courseClass);
    }

    /**
     * 核心排课冲突校验算法
     */
    private void checkSchedulingConflict(CourseClass courseClass) {
        CourseClass query = new CourseClass();
        query.setSemester(courseClass.getSemester());
        query.setDayOfWeek(courseClass.getDayOfWeek());

        List<CourseClass> existList = courseClassMapper.selectCourseClassList(query);

        for (CourseClass exist : existList) {
            if (courseClass.getClassId() != null && courseClass.getClassId().equals(exist.getClassId())) {
                continue;
            }

            boolean isTimeOverlap = courseClass.getStartPeriod() <= exist.getEndPeriod() &&
                    courseClass.getEndPeriod() >= exist.getStartPeriod();

            if (isTimeOverlap) {
                String weekDayName = getWeekDayName(exist.getDayOfWeek());

                if (courseClass.getStaffId() != null && courseClass.getStaffId().equals(exist.getStaffId())) {
                    throw new ServiceException(String.format(
                            "教师排课冲突：该教师在 %s 第 %d-%d 节已有课（班级：%s）",
                            weekDayName, exist.getStartPeriod(), exist.getEndPeriod(), exist.getClassId()
                    ));
                }

                if (courseClass.getClassroom() != null && courseClass.getClassroom().equals(exist.getClassroom())) {
                    throw new ServiceException(String.format(
                            "教室冲突：教室 [%s] 在 %s 第 %d-%d 节已被占用（班级：%s）",
                            exist.getClassroom(), weekDayName, exist.getStartPeriod(), exist.getEndPeriod(), exist.getClassId()
                    ));
                }
            }
        }
    }

    private String getWeekDayName(Long day) {
        if (day == null) return "";
        String[] weeks = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return (day >= 1 && day <= 7) ? weeks[day.intValue()] : "周" + day;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourseClassByClassIds(String[] classIds)
    {
        // 校验：如果要删除的班级里已经有学生了，通常禁止删除
        for (String id : classIds) {
            CourseClass cc = courseClassMapper.selectCourseClassByClassId(id);
            if (cc != null && cc.getSelectedNum() > 0) {
                throw new ServiceException("班级 [" + id + "] 已有学生选课，无法删除！请先清空该班级选课记录。");
            }
        }
        return courseClassMapper.deleteCourseClassByClassIds(classIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourseClassByClassId(String classId)
    {
        CourseClass cc = courseClassMapper.selectCourseClassByClassId(classId);
        if (cc != null && cc.getSelectedNum() > 0) {
            throw new ServiceException("该班级已有学生选课，无法删除！");
        }
        return courseClassMapper.deleteCourseClassByClassId(classId);
    }
}