package com.ruoyi.web.controller.system;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.teacher.service.ITeacherService;
import com.ruoyi.student.service.IStudentService;
import com.ruoyi.course.service.ICourseService;
import com.ruoyi.department.service.IDepartmentService;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Autowired
    private ITeacherService teacherService;

    @Autowired
    private IStudentService studentService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IDepartmentService departmentService;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }

    /**
     * 获取首页统计数据
     */
    @GetMapping("/getIndexData")
    public AjaxResult getIndexData()
    {
        Map<String, Object> res = new HashMap<>();

        // 1. 顶部四个卡片的数据
        res.put("teacherCount", teacherService.selectTeacherCount());
        res.put("studentCount", studentService.selectStudentCount());
        res.put("courseCount", courseService.selectCourseCount());
        res.put("deptCount", departmentService.selectDepartmentCount());

        // 2. 三个饼图的数据
        res.put("studentDeptData", studentService.selectStudentCountByDept());
        res.put("teacherDeptData", teacherService.selectTeacherCountByDept());
        res.put("teacherTitleData", teacherService.selectTeacherTitleDistribution());

        return AjaxResult.success(res);
    }
}