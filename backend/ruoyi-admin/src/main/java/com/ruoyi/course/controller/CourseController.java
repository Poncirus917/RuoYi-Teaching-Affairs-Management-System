package com.ruoyi.course.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.course.domain.Course;
import com.ruoyi.course.service.ICourseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 课程信息Controller
 */
@RestController
@RequestMapping("/course/course")
public class CourseController extends BaseController
{
    @Autowired
    private ICourseService courseService;

    /**
     * 判断是否为管理层（兼容 ID 为 1 的 admin 以及 角色字符为 administer 的系统管理员）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询课程信息列表
     */
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(Course course)
    {
        // 逻辑说明：如果不是管理层，确保数据权限不被恶意拦截（若后台已配置全部权限，此行可作为双保险）
        if (!isManager()) {
            course.getParams().put("dataScope", "");
        }

        startPage();
        List<Course> list = courseService.selectCourseList(course);
        return getDataTable(list);
    }

    /**
     * 获取课程信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('course:course:query')")
    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") String courseId)
    {
        return success(courseService.selectCourseByCourseId(courseId));
    }

    /**
     * 新增课程信息
     */
    @PreAuthorize("@ss.hasPermi('course:course:add')")
    @Log(title = "课程信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Course course)
    {
        // 使用统一的管理层判断
        if (!isManager()) {
            return error("操作失败：非管理员无权新增课程档案");
        }

        if (!checkWeightSum(course)) {
            return AjaxResult.error("新增失败：平时成绩占比与期末成绩占比之和必须等于 1.0");
        }
        return toAjax(courseService.insertCourse(course));
    }

    /**
     * 修改课程信息
     */
    @PreAuthorize("@ss.hasPermi('course:course:edit')")
    @Log(title = "课程信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Course course)
    {
        // 使用统一的管理层判断
        if (!isManager()) {
            return error("操作失败：非管理员无权修改课程基础档案");
        }

        if (!checkWeightSum(course)) {
            return AjaxResult.error("修改失败：平时成绩占比与期末成绩占比之和必须等于 1.0");
        }
        return toAjax(courseService.updateCourse(course));
    }

    /**
     * 删除课程信息
     */
    @PreAuthorize("@ss.hasPermi('course:course:remove')")
    @Log(title = "课程信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable String[] courseIds)
    {
        // 使用统一的管理层判断
        if (!isManager()) {
            return error("警告：您无权执行删除课程档案的操作！");
        }
        return toAjax(courseService.deleteCourseByCourseIds(courseIds));
    }

    /**
     * 导出课程信息列表
     */
    @PreAuthorize("@ss.hasPermi('course:course:export')")
    @Log(title = "课程信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Course course)
    {
        List<Course> list = courseService.selectCourseList(course);
        ExcelUtil<Course> util = new ExcelUtil<Course>(Course.class);
        util.exportExcel(response, list, "课程信息数据");
    }

    /**
     * 私有校验方法：检查成绩占比之和是否为 1.0
     */
    private boolean checkWeightSum(Course course) {
        if (course.getUsualWeight() != null && course.getFinalWeight() != null) {
            return course.getUsualWeight().add(course.getFinalWeight()).compareTo(new java.math.BigDecimal("1.0")) == 0;
        }
        return true;
    }
}