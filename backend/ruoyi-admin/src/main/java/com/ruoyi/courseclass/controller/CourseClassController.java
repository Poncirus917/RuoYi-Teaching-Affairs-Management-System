package com.ruoyi.courseclass.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.courseselection.service.ICourseSelectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.courseclass.domain.CourseClass;
import com.ruoyi.courseclass.service.ICourseClassService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 开课情况Controller
 */
@RestController
@RequestMapping("/courseclass/courseclass")
public class CourseClassController extends BaseController
{
    @Autowired
    private ICourseClassService courseClassService;
    @Autowired
    private ICourseSelectionService courseSelectionService;

    /**
     * 判断是否为管理层（兼容 ID=1 的 admin 和 角色为 administer 的系统管理员）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询开课情况列表
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseClass courseClass)
    {
        // 如果不是管理层，则清除可能存在的数据权限干扰
        if (!isManager()) {
            courseClass.getParams().put("dataScope", "");
        }

        startPage();
        List<CourseClass> list = courseClassService.selectCourseClassList(courseClass);
        return getDataTable(list);
    }

    /**
     * 导出开课情况列表
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:export')")
    @Log(title = "开课情况", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseClass courseClass)
    {
        List<CourseClass> list = courseClassService.selectCourseClassList(courseClass);
        ExcelUtil<CourseClass> util = new ExcelUtil<CourseClass>(CourseClass.class);
        util.exportExcel(response, list, "开课情况数据");
    }

    /**
     * 获取开课情况详细信息
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:query')")
    @GetMapping(value = "/{classId}")
    public AjaxResult getInfo(@PathVariable("classId") String classId)
    {
        return success(courseClassService.selectCourseClassByClassId(classId));
    }

    /**
     * 新增教学班
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:add')")
    @Log(title = "教学班", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseClass courseClass)
    {
        // 只有非管理层的教师，才强制绑定工号
        if (SecurityUtils.hasRole("teacher") && !isManager()) {
            courseClass.setStaffId(SecurityUtils.getUsername());
        }
        return toAjax(courseClassService.insertCourseClass(courseClass));
    }

    /**
     * 修改开课情况
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:edit')")
    @Log(title = "开课情况", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CourseClass courseClass)
    {
        if (SecurityUtils.hasRole("student")) {
            return error("操作失败：学生无权修改开课信息！");
        }

        // 获取数据库中现有的原始数据
        CourseClass oldData = courseClassService.selectCourseClassByClassId(courseClass.getClassId());
        if (oldData == null) {
            return error("修改失败：记录不存在");
        }

        // 校验 1：如果不是管理层，必须是本人负责的班级
        if (!isManager()) {
            if (!oldData.getStaffId().equals(SecurityUtils.getUsername())) {
                return error("操作失败：您无权修改其他教师负责的开课记录！");
            }
        }

        // ================== 新增校验逻辑开始 ==================
        // 校验 2：检查是否已经有人选课
        // 假设你的选课表 Service 有个方法叫 countSelectionByClassId
        int selectionCount = courseSelectionService.countSelectionByClassId(courseClass.getClassId());

        if (selectionCount > 0) {
            // 检查核心字段是否发生了变化
            boolean isCourseIdChanged = !oldData.getCourseId().equals(courseClass.getCourseId());
            boolean isSemesterChanged = !oldData.getSemester().equals(courseClass.getSemester());

            // 检查时间相关的三个字段
            boolean isDayChanged = !oldData.getDayOfWeek().equals(courseClass.getDayOfWeek());
            boolean isStartChanged = !oldData.getStartPeriod().equals(courseClass.getStartPeriod());
            boolean isEndChanged = !oldData.getEndPeriod().equals(courseClass.getEndPeriod());

            if (isCourseIdChanged || isSemesterChanged || isDayChanged || isStartChanged || isEndChanged) {
                return error("修改失败：该班级已有学生选课，禁止修改课程、学期及上课时间（星期/节次）！");
            }
        }
        // ================== 新增校验逻辑结束 ==================

        return toAjax(courseClassService.updateCourseClass(courseClass));
    }

    /**
     * 删除开课情况
     */
    @PreAuthorize("@ss.hasPermi('courseclass:courseclass:remove')")
    @Log(title = "开课情况", businessType = BusinessType.DELETE)
    @DeleteMapping("/{classIds}")
    public AjaxResult remove(@PathVariable String[] classIds)
    {
        if (SecurityUtils.hasRole("student")) {
            return error("操作失败：学生无权删除开课档案！");
        }

        // 校验：如果不是管理层，检查是否有权删除
        if (!isManager()) {
            for (String id : classIds) {
                CourseClass data = courseClassService.selectCourseClassByClassId(id);
                if (data != null && !data.getStaffId().equals(SecurityUtils.getUsername())) {
                    return error("操作失败：删除列表中包含其他教师负责的记录！");
                }
            }
        }
        return toAjax(courseClassService.deleteCourseClassByClassIds(classIds));
    }
}