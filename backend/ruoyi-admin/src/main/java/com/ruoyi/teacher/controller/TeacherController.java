package com.ruoyi.teacher.controller;

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
import com.ruoyi.teacher.domain.Teacher;
import com.ruoyi.teacher.service.ITeacherService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 教师信息Controller
 */
@RestController
@RequestMapping("/teacher/teacher")
public class TeacherController extends BaseController
{
    @Autowired
    private ITeacherService teacherService;

    /**
     * 判断是否为管理层（兼容 admin 和 administer 角色）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询教师信息列表
     * 修改逻辑：如果是管理层，查询全部；如果是普通教师，强制只能查询到自己
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:list')")
    @GetMapping("/list")
    public TableDataInfo list(Teacher teacher)
    {
        // 如果不是管理层且是教师，则强制设置工号限制
        if (!isManager() && SecurityUtils.hasRole("teacher")) {
            teacher.setStaffId(SecurityUtils.getUsername());
            // 同时清除数据权限拦截器的干扰
            teacher.getParams().put("dataScope", "");
        }

        startPage();
        List<Teacher> list = teacherService.selectTeacherList(teacher);
        return getDataTable(list);
    }

    /**
     * 获取教师信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:query')")
    @GetMapping(value = "/{staffId}")
    public AjaxResult getInfo(@PathVariable("staffId") String staffId)
    {
        // 校验：非管理层只能看自己
        if (!isManager()) {
            if (!staffId.equals(SecurityUtils.getUsername())) {
                return error("对不起，您无权查看其他教师的详细信息");
            }
        }
        return success(teacherService.selectTeacherByStaffId(staffId));
    }

    /**
     * 新增教师信息
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:add')")
    @Log(title = "教师信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Teacher teacher)
    {
        if (!isManager()) {
            return error("对不起，您无权新增教师信息");
        }
        return toAjax(teacherService.insertTeacher(teacher));
    }

    /**
     * 修改教师信息
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:edit')")
    @Log(title = "教师信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Teacher teacher)
    {
        if (!isManager()) {
            return error("对不起，您无权修改教师基础信息");
        }
        return toAjax(teacherService.updateTeacher(teacher));
    }

    /**
     * 删除教师信息
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:remove')")
    @Log(title = "教师信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{staffIds}")
    public AjaxResult remove(@PathVariable String[] staffIds)
    {
        if (!isManager()) {
            return error("对不起，权限不足，无法执行删除操作");
        }
        return toAjax(teacherService.deleteTeacherByStaffIds(staffIds));
    }

    /**
     * 导出教师信息列表
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:export')")
    @Log(title = "教师信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Teacher teacher)
    {
        if (!isManager() && SecurityUtils.hasRole("teacher")) {
            teacher.setStaffId(SecurityUtils.getUsername());
        }
        List<Teacher> list = teacherService.selectTeacherList(teacher);
        ExcelUtil<Teacher> util = new ExcelUtil<Teacher>(Teacher.class);
        util.exportExcel(response, list, "教师信息数据");
    }

    /**
     * 获取教师任课班级的成绩分布统计数据
     * 访问路径：GET /teacher/teacher/classStats
     */
    @PreAuthorize("@ss.hasPermi('teacher:teacher:query')")
    @GetMapping("/classStats")
    public AjaxResult getClassStats(String staffId)
    {
        // 1. 获取当前登录用户的工号（SecurityUtils.getUsername() 获取登录账号）
        String loginUserStaffId = SecurityUtils.getUsername();

        // 2. 权限校验逻辑
        if (!isManager()) {
            // 如果不是管理员，强制只能查询当前登录教师的数据
            staffId = loginUserStaffId;
        } else {
            // 如果是管理员，且没有传入 staffId，则默认查自己或返回空
            if (staffId == null || staffId.isEmpty()) {
                staffId = loginUserStaffId;
            }
        }

        // 3. 调用 service 获取统计结果并返回
        return success(teacherService.selectTeacherClassStats(staffId));
    }
}