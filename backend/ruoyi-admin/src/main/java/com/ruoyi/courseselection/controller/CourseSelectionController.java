package com.ruoyi.courseselection.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.courseselection.domain.CourseSelection;
import com.ruoyi.courseselection.service.ICourseSelectionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.courseclass.service.ICourseClassService;
import com.ruoyi.courseclass.domain.CourseClass;

/**
 * 选课记录Controller
 */
@RestController
@RequestMapping("/courseselection/courseselection")
public class CourseSelectionController extends BaseController
{
    @Autowired
    private ICourseSelectionService courseSelectionService;

    @Autowired
    private ICourseClassService courseClassService;

    /**
     * 判断是否为管理层（兼容 admin 和 administer 角色）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询选课记录列表
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseSelection courseSelection)
    {
        String username = SecurityUtils.getUsername();
        // 管理员看全部，老师看自己班的，学生看自己的
        if (!isManager()) {
            if (SecurityUtils.hasRole("teacher")) {
                courseSelection.getParams().put("currentStaffId", username);
            }
            if (SecurityUtils.hasRole("student")) {
                courseSelection.setStudentId(username);
            }
        }
        startPage();
        List<CourseSelection> list = courseSelectionService.selectCourseSelectionList(courseSelection);
        return getDataTable(list);
    }

    /**
     * 获取选课记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:query')")
    @GetMapping(value = "/{studentId}/{classId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId, @PathVariable("classId") String classId)
    {
        return success(courseSelectionService.selectCourseSelectionByCompoundKey(studentId, classId));
    }

    /**
     * 新增选课记录
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:add')")
    @Log(title = "选课记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseSelection courseSelection)
    {
        String currentUsername = SecurityUtils.getUsername();
        CourseClass targetClass = courseClassService.selectCourseClassByClassId(courseSelection.getClassId());
        if (targetClass == null) {
            return error("选课失败：教学班不存在");
        }

        courseSelection.setCourseId(targetClass.getCourseId());
        courseSelection.setSemester(targetClass.getSemester());

        // 如果不是管理员，需要进行严格校验
        if (!isManager()) {
            if (SecurityUtils.hasRole("student")) {
                courseSelection.setStudentId(currentUsername);
            }
            if (SecurityUtils.hasRole("teacher")) {
                if (!targetClass.getStaffId().equals(currentUsername)) {
                    return error("操作失败：您只能为您负责的教学班添加学生！");
                }
            }
        }

        return toAjax(courseSelectionService.insertCourseSelection(courseSelection));
    }

    /**
     * 修改选课记录 (锁定主键，仅限打分)
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:edit')")
    @Log(title = "选课记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseSelection courseSelection)
    {
        CourseSelection oldRecord = courseSelectionService.selectCourseSelectionByCompoundKey(
                courseSelection.getStudentId(), courseSelection.getClassId());

        if (oldRecord == null) {
            return error("修改失败：原选课记录不存在或主键被篡改");
        }

        // 校验权限：非管理员不可随意打分
        if (!isManager()) {
            if (SecurityUtils.hasRole("student")) {
                return error("学生无权修改选课记录及分数！");
            }
            if (SecurityUtils.hasRole("teacher")) {
                if (!oldRecord.getStaffId().equals(SecurityUtils.getUsername())) {
                    return error("操作失败：该学生不在您的教学班中，无权修改！");
                }
            }
        }

        CourseSelection updateEntity = new CourseSelection();
        updateEntity.setStudentId(courseSelection.getStudentId());
        updateEntity.setClassId(courseSelection.getClassId());
        updateEntity.setNormalScore(courseSelection.getNormalScore());
        updateEntity.setTestScore(courseSelection.getTestScore());
        updateEntity.setTotalScore(courseSelection.getTotalScore());

        return toAjax(courseSelectionService.updateCourseSelection(updateEntity));
    }

    /**
     * 删除选课记录 (退课逻辑)
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:remove')")
    @Log(title = "选课记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentId}/{classId}")
    public AjaxResult remove(@PathVariable("studentId") String studentId, @PathVariable("classId") String classId)
    {
        CourseSelection existRecord = courseSelectionService.selectCourseSelectionByCompoundKey(studentId, classId);
        if (existRecord == null) {
            return error("未找到该选课记录");
        }

        // 关键逻辑：如果是管理层(admin/administer)，直接跳过所有拦截，允许强制删除
        if (!isManager()) {
            // 1. 拦截已有成绩的记录
            if (existRecord.getNormalScore() != null || existRecord.getTestScore() != null) {
                return error("操作失败：该课程已录入分数，不可退选！");
            }
            // 2. 学生只能退选自己的
            if (SecurityUtils.hasRole("student") && !studentId.equals(SecurityUtils.getUsername())) {
                return error("操作失败：您只能退选自己的课程！");
            }
            // 3. 教师只能删除自己班的
            if (SecurityUtils.hasRole("teacher")) {
                CourseClass myClass = courseClassService.selectCourseClassByClassId(classId);
                if (myClass == null || !myClass.getStaffId().equals(SecurityUtils.getUsername())) {
                    return error("操作失败：您无权删除其他老师班级的记录！");
                }
            }
        }

        // 如果是 isManager() 为 true，会直接来到这里执行删除
        return toAjax(courseSelectionService.deleteCourseSelectionByCompoundKey(studentId, classId));
    }

    /**
     * 导出选课记录
     */
    @PreAuthorize("@ss.hasPermi('courseselection:courseselection:export')")
    @Log(title = "选课记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseSelection courseSelection)
    {
        if (!isManager()) {
            if (SecurityUtils.hasRole("student")) {
                courseSelection.setStudentId(SecurityUtils.getUsername());
            } else if (SecurityUtils.hasRole("teacher")) {
                courseSelection.getParams().put("currentStaffId", SecurityUtils.getUsername());
            }
        }
        List<CourseSelection> list = courseSelectionService.selectCourseSelectionList(courseSelection);
        ExcelUtil<CourseSelection> util = new ExcelUtil<CourseSelection>(CourseSelection.class);
        util.exportExcel(response, list, "选课记录数据");
    }
}