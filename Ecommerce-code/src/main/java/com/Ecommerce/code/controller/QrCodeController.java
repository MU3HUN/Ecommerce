package com.Ecommerce.code.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.Ecommerce.common.annotation.Log;
import com.Ecommerce.common.core.controller.BaseController;
import com.Ecommerce.common.core.domain.AjaxResult;
import com.Ecommerce.common.enums.BusinessType;
import com.Ecommerce.code.domain.QrCode;
import com.Ecommerce.code.service.IQrCodeService;
import com.Ecommerce.common.utils.poi.ExcelUtil;
import com.Ecommerce.common.core.page.TableDataInfo;

/**
 * 贴码核销Controller
 *
 * @author huang
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/code/code")
public class QrCodeController extends BaseController
{
    @Autowired
    private IQrCodeService qrCodeService;

    @PreAuthorize("@ss.hasPermi('code:code:list')")
    @GetMapping("/stats")
    public AjaxResult getCodeStats() {
        return success(qrCodeService.getCodeStats());
    }

    /**
     * 查询贴码核销列表
     */
    @PreAuthorize("@ss.hasPermi('code:code:list')")
    @GetMapping("/list")
    public TableDataInfo list(QrCode qrCode)
    {
        startPage();
        List<QrCode> list = qrCodeService.selectQrCodeList(qrCode);
        return getDataTable(list);
    }

    /**
     * 导出贴码核销列表
     */
    @PreAuthorize("@ss.hasPermi('code:code:export')")
    @Log(title = "贴码核销", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QrCode qrCode)
    {
        List<QrCode> list = qrCodeService.selectQrCodeList(qrCode);
        ExcelUtil<QrCode> util = new ExcelUtil<QrCode>(QrCode.class);
        util.exportExcel(response, list, "贴码核销数据");
    }

    /**
     * 获取贴码核销详细信息
     */
    @PreAuthorize("@ss.hasPermi('code:code:query')")
    @GetMapping(value = "/{qrId}")
    public AjaxResult getInfo(@PathVariable("qrId") Long qrId)
    {
        return success(qrCodeService.selectQrCodeByQrId(qrId));
    }

    /**
     * 新增贴码核销
     */
    @PreAuthorize("@ss.hasPermi('code:code:add')")
    @Log(title = "贴码核销", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QrCode qrCode) {
        int result = qrCodeService.insertQrCode(qrCode);
        if (result == -1) {
            return AjaxResult.success("操作成功（二维码已存在）");
        }
        return toAjax(result);
    }

    /**
     * 修改贴码核销
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('code:code:edit')")
    @Log(title = "贴码核销", businessType = BusinessType.UPDATE)
    public AjaxResult edit(@RequestBody QrCode qrCode) {
        // 确保 qrCode 对象包含 qrId 字段
        return toAjax(qrCodeService.updateQrCode(qrCode));
    }

    /**
     * 删除贴码核销
     */
    // 创建参数接收类
    @Data
    public static class DeleteDTO {
        @NotNull(message = "二维码ID不能为空")
        private Long[] qrIds;
    }

    // 修改Controller方法
    @DeleteMapping
    @PreAuthorize("@ss.hasPermi('code:code:logicDelete')")
    @Log(title = "贴码核销", businessType = BusinessType.DELETE)
    public AjaxResult remove(@Valid @RequestBody DeleteDTO dto) {
        return toAjax(qrCodeService.deleteQrCodeByQrIds(dto.getQrIds()));
    }

    @GetMapping("/status")
    public AjaxResult getQRStatus(@RequestParam String qrNumber) {
        QrCode qrCode = qrCodeService.selectQrCodeByQrNumber(qrNumber);
        return success(qrCode != null ? qrCode.getStatus() : "未知");
    }

}