package com.Ecommerce.batch.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Ecommerce.common.annotation.Log;
import com.Ecommerce.common.core.controller.BaseController;
import com.Ecommerce.common.core.domain.AjaxResult;
import com.Ecommerce.common.enums.BusinessType;
import com.Ecommerce.batch.domain.QrBatch;
import com.Ecommerce.batch.service.IQrBatchService;
import com.Ecommerce.common.utils.poi.ExcelUtil;
import com.Ecommerce.common.core.page.TableDataInfo;

/**
 * 二维码管理Controller
 * 
 * @author huang
 * @date 2025-03-14
 */
@RestController
@RequestMapping("/batch/batch")
public class QrBatchController extends BaseController
{
    @Autowired
    private IQrBatchService qrBatchService;

    /**
     * 查询二维码管理列表
     */
    @PreAuthorize("@ss.hasPermi('batch:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(QrBatch qrBatch)
    {
        startPage();
        List<QrBatch> list = qrBatchService.selectQrBatchList(qrBatch);
        return getDataTable(list);
    }

    /**
     * 导出二维码管理列表
     */
    @PreAuthorize("@ss.hasPermi('batch:batch:export')")
    @Log(title = "二维码管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QrBatch qrBatch)
    {
        List<QrBatch> list = qrBatchService.selectQrBatchList(qrBatch);
        ExcelUtil<QrBatch> util = new ExcelUtil<QrBatch>(QrBatch.class);
        util.exportExcel(response, list, "二维码管理数据");
    }

    /**
     * 获取二维码管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('batch:batch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId)
    {
        return success(qrBatchService.selectQrBatchByBatchId(batchId));
    }

    /**
     * 新增二维码管理
     */
    @PreAuthorize("@ss.hasPermi('batch:batch:add')")
    @Log(title = "二维码管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QrBatch qrBatch) {
        System.out.println("接收到的数据: " + qrBatch); // 打印接收到的数据
        QrBatch insertedBatch = qrBatchService.insertQrBatch(qrBatch);
        System.out.println("插入后的数据: " + insertedBatch); // 打印插入后的数据
        return AjaxResult.success("操作成功", insertedBatch);
    }

    /**
     * 修改二维码管理
     */
    @PreAuthorize("@ss.hasPermi('batch:batch:edit')")
    @Log(title = "二维码管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QrBatch qrBatch)
    {
        return toAjax(qrBatchService.updateQrBatch(qrBatch));
    }

    /**
     * 删除二维码管理
     */
    @Data
    public static class DeleteDTO {
        @NotNull(message = "二维码ID不能为空")
        private Long[] batchIds;
    }
    @PreAuthorize("@ss.hasPermi('batch:batch:logicDelete')")
    @Log(title = "二维码管理", businessType = BusinessType.DELETE)
    @DeleteMapping()
    public AjaxResult remove(@Valid @RequestBody DeleteDTO dto)
    {
        return toAjax(qrBatchService.deleteQrBatchByBatchIds(dto.getBatchIds()));
    }

    @PreAuthorize("@ss.hasPermi('batch:batch:list')")
    @GetMapping("/stats")
    public AjaxResult getBatchStats() {
        return success(qrBatchService.getBatchStats());
    }

}
