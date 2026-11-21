package com.ruoyi.courseselection.service.impl;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.ruoyi.common.exception.ServiceException; // 引入若依通用异常
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.courseselection.mapper.CourseSelectionMapper;
import com.ruoyi.courseselection.domain.CourseSelection;
import com.ruoyi.courseselection.service.ICourseSelectionService;
import com.ruoyi.course.mapper.CourseMapper;
import com.ruoyi.course.domain.Course;

/**
 * 选课记录Service业务层处理
 */
@Service
public class CourseSelectionServiceImpl implements ICourseSelectionService
{
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 新增选课记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCourseSelection(CourseSelection courseSelection)
    {
        // 1. 跨学期重复选课硬拦截
        if (courseSelection.getStudentId() != null && courseSelection.getCourseId() != null) {
            int historyCount = courseSelectionMapper.countHistorySelection(
                    courseSelection.getStudentId(), courseSelection.getCourseId());
            if (historyCount > 0) {
                throw new ServiceException("选课失败：你已在历史学期修读过课程[" + courseSelection.getCourseId() + "]，不可重复选课！");
            }
        }

        // 2. 原有逻辑：计算分数
        calculateAndSetTotalScore(courseSelection);

        try {
            // 这里会触发数据库中的 BEFORE INSERT 校验
            return courseSelectionMapper.insertCourseSelection(courseSelection);
        } catch (Exception e) {
            // 1. 获取最原始的异常原因
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            String message = rootCause.getMessage();

            // 2. 核心清洗逻辑：如果包含自定义的“选课失败”关键字
            if (message != null && message.contains("选课失败")) {
                // 找到“选课失败”出现的位置
                int startIndex = message.indexOf("选课失败");
                // 尝试找到句尾或分号，如果没有则取到最后
                int endIndex = message.indexOf(";", startIndex);
                if (endIndex == -1) endIndex = message.length();

                // 提取最干净的一句话：选课失败：本学期你已选过该课程的其他班级！
                String cleanMessage = message.substring(startIndex, endIndex).trim();

                // 抛出 ServiceException，若依的全局处理器会直接把这个字符串展示在前端
                throw new ServiceException(cleanMessage);
            }

            // 3. 处理主键冲突的情况
            if (message != null && message.contains("Duplicate entry")) {
                throw new ServiceException("选课失败：该学生已在此班级名册中");
            }

            // 4. 其他未知异常
            throw new ServiceException("系统异常，请稍后再试");
        }
    }

    /**
     * 修改选课记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCourseSelection(CourseSelection courseSelection)
    {
        calculateAndSetTotalScore(courseSelection);
        return courseSelectionMapper.updateCourseSelection(courseSelection);
    }

    /**
     * 自动计算总成绩的私有方法
     */
    private void calculateAndSetTotalScore(CourseSelection cs) {
        if (cs.getNormalScore() != null && cs.getTestScore() != null && cs.getCourseId() != null) {
            long normalVal = cs.getNormalScore();
            if (normalVal > 100) normalVal = 100;
            else if (normalVal < 0) normalVal = 0;
            cs.setNormalScore(normalVal);

            long testVal = cs.getTestScore();
            if (testVal > 100) testVal = 100;
            else if (testVal < 0) testVal = 0;
            cs.setTestScore(testVal);

            Course course = courseMapper.selectCourseByCourseId(cs.getCourseId());
            if (course != null && course.getUsualWeight() != null && course.getFinalWeight() != null) {
                BigDecimal normal = new BigDecimal(cs.getNormalScore());
                BigDecimal test = new BigDecimal(cs.getTestScore());
                BigDecimal total = normal.multiply(course.getUsualWeight())
                        .add(test.multiply(course.getFinalWeight()));
                cs.setTotalScore(total.setScale(0, RoundingMode.HALF_UP).longValue());
            }
        }
    }

    // ... 其他方法保持不变 ...

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourseSelectionByStudentIds(String[] studentIds)
    {
        return courseSelectionMapper.deleteCourseSelectionByStudentIds(studentIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourseSelectionByCompoundKey(String studentId, String classId)
    {
        return courseSelectionMapper.deleteCourseSelectionByCompoundKey(studentId, classId);
    }

    @Override
    public CourseSelection selectCourseSelectionByCompoundKey(String studentId, String classId)
    {
        return courseSelectionMapper.selectCourseSelectionByCompoundKey(studentId, classId);
    }

    @Override
    public List<CourseSelection> selectCourseSelectionList(CourseSelection courseSelection)
    {
        return courseSelectionMapper.selectCourseSelectionList(courseSelection);
    }

    @Override
    public int countHistorySelection(String studentId, String courseId)
    {
        return courseSelectionMapper.countHistorySelection(studentId, courseId);
    }

    @Override
    public int countSelectionByClassId(String classId)
    {
        return courseSelectionMapper.countSelectionByClassId(classId);
    }
}