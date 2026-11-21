package com.ruoyi.student.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.student.domain.Student;
import com.ruoyi.student.service.IStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 学生Controller
 */
@RestController
@RequestMapping("/student/student")
public class StudentController extends BaseController
{
    @Autowired
    private IStudentService studentService;

    /**
     * 判断是否为管理层（兼容 admin 和 administer 角色）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询学生列表
     */
    @PreAuthorize("@ss.hasPermi('student:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        // 逻辑：如果是学生且不是管理层，强制只能查自己
        if (SecurityUtils.hasRole("student") && !isManager()) {
            student.setStudentId(SecurityUtils.getUsername());
            student.getParams().put("dataScope", "");
        }

        startPage();
        List<Student> list = studentService.selectStudentList(student);
        return getDataTable(list);
    }

    /**
     * 获取学生详细信息
     */
    @PreAuthorize("@ss.hasPermi('student:student:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        // 安全校验：非管理层的学生只能查自己
        if (SecurityUtils.hasRole("student") && !isManager()) {
            if (!studentId.equals(SecurityUtils.getUsername())) {
                return error("对不起，您无权查看其他学生的详细档案");
            }
        }
        return success(studentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增学生
     */
    @PreAuthorize("@ss.hasPermi('student:student:add')")
    @Log(title = "学生", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        if (!isManager()) {
            return error("操作失败：非管理员无权手动新增学生信息");
        }
        return toAjax(studentService.insertStudent(student));
    }

    /**
     * 修改学生
     */
    @PreAuthorize("@ss.hasPermi('student:student:edit')")
    @Log(title = "学生", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        if (!isManager()) {
            return error("操作失败：您的权限不足，档案修改请联系教务人员");
        }
        return toAjax(studentService.updateStudent(student));
    }

    /**
     * 删除学生
     */
    @PreAuthorize("@ss.hasPermi('student:student:remove')")
    @Log(title = "学生", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        if (!isManager()) {
            return error("警告：您无权执行删除学生档案的操作！");
        }
        return toAjax(studentService.deleteStudentByStudentIds(studentIds));
    }

    /**
     * 导出学生列表
     */
    @PreAuthorize("@ss.hasPermi('student:student:export')")
    @Log(title = "学生", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        if (SecurityUtils.hasRole("student") && !isManager()) {
            student.setStudentId(SecurityUtils.getUsername());
        }
        List<Student> list = studentService.selectStudentList(student);
        ExcelUtil<Student> util = new ExcelUtil<Student>(Student.class);
        util.exportExcel(response, list, "学生数据");
    }

    /**
     * 获取当前登录学生的成绩统计（各学期学分、绩点及总计）
     * 访问路径：GET /student/student/myGpaStats
     */
    @PreAuthorize("@ss.hasRole('student')")
    @GetMapping("/myGpaStats")
    public AjaxResult getMyGpaStats()
    {
        // 1. 获取当前登录学生的学号（即登录账号名）
        String studentId = SecurityUtils.getUsername();

        // 2. 调用 Service 获取统计列表（包含各学期和个人总计）
        List<Student> list = studentService.selectStudentGpaStats(studentId);

        return success(list);
    }
}