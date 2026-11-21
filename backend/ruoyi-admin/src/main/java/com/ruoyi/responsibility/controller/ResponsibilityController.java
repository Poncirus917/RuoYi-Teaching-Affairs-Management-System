package com.ruoyi.responsibility.controller;

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
import com.ruoyi.responsibility.domain.Responsibility;
import com.ruoyi.responsibility.service.IResponsibilityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 教师任课情况Controller
 */
@RestController
@RequestMapping("/responsibility/responsibility")
public class ResponsibilityController extends BaseController
{
    @Autowired
    private IResponsibilityService responsibilityService;

    /**
     * 判断是否为管理层（兼容 admin 和 administer 角色）
     */
    private boolean isManager() {
        return SecurityUtils.isAdmin(SecurityUtils.getUserId()) || SecurityUtils.hasRole("administer");
    }

    /**
     * 查询教师任课情况列表
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:list')")
    @GetMapping("/list")
    public TableDataInfo list(Responsibility responsibility)
    {
        // 逻辑修改：如果不是管理层，且拥有教师角色，则只查自己
        if (!isManager() && SecurityUtils.hasRole("teacher")) {
            responsibility.setStaffId(SecurityUtils.getUsername());
            responsibility.getParams().put("dataScope", "");
        }

        startPage();
        List<Responsibility> list = responsibilityService.selectResponsibilityList(responsibility);
        return getDataTable(list);
    }

    /**
     * 获取教师任课情况详细信息
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:query')")
    @GetMapping(value = "/{staffId}/{courseId}")
    public AjaxResult getInfo(@PathVariable("staffId") String staffId, @PathVariable("courseId") String courseId)
    {
        // 安全校验：非管理层只能看自己
        if (!isManager()) {
            if (!staffId.equals(SecurityUtils.getUsername())) {
                return error("对不起，您无权查看其他教师的任课详情");
            }
        }
        Responsibility responsibility = new Responsibility();
        responsibility.setStaffId(staffId);
        responsibility.setCourseId(courseId);
        return success(responsibilityService.selectResponsibilityByStaffIdAndCourseId(responsibility));
    }

    /**
     * 新增教师任课情况
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:add')")
    @Log(title = "教师任课情况", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Responsibility responsibility)
    {
        if (!isManager()) {
            return error("对不起，您无权新增任课情况，请联系教务管理人员分配");
        }
        return toAjax(responsibilityService.insertResponsibility(responsibility));
    }

    /**
     * 修改教师任课情况
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:edit')")
    @Log(title = "教师任课情况", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Responsibility responsibility)
    {
        if (!isManager()) {
            return error("对不起，您无权修改任课关系信息");
        }
        return toAjax(responsibilityService.updateResponsibility(responsibility));
    }

    /**
     * 删除教师任课情况
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:remove')")
    @Log(title = "教师任课情况", businessType = BusinessType.DELETE)
    @DeleteMapping("/{staffId}/{courseId}")
    public AjaxResult remove(@PathVariable("staffId") String staffId, @PathVariable("courseId") String courseId)
    {
        if (!isManager()) {
            return error("权限不足，无法删除任课情况");
        }
        Responsibility responsibility = new Responsibility();
        responsibility.setStaffId(staffId);
        responsibility.setCourseId(courseId);
        return toAjax(responsibilityService.deleteResponsibility(responsibility));
    }

    /**
     * 批量删除教师任课情况
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:remove')")
    @Log(title = "教师任课情况", businessType = BusinessType.DELETE)
    @DeleteMapping("/{staffIds}")
    public AjaxResult removeBatch(@PathVariable String[] staffIds)
    {
        if (!isManager()) {
            return error("权限不足，无法删除任课情况");
        }
        return toAjax(responsibilityService.deleteResponsibilityByStaffIds(staffIds));
    }

    /**
     * 导出教师任课情况列表
     */
    @PreAuthorize("@ss.hasPermi('responsibility:responsibility:export')")
    @Log(title = "教师任课情况", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Responsibility responsibility)
    {
        if (!isManager() && SecurityUtils.hasRole("teacher")) {
            responsibility.setStaffId(SecurityUtils.getUsername());
        }
        List<Responsibility> list = responsibilityService.selectResponsibilityList(responsibility);
        ExcelUtil<Responsibility> util = new ExcelUtil<Responsibility>(Responsibility.class);
        util.exportExcel(response, list, "教师任课情况数据");
    }
}