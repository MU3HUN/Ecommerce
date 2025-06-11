package com.Ecommerce.batch.service;

import java.util.List;
import java.util.Map;

import com.Ecommerce.batch.domain.QrBatch;

/**
 * 二维码管理Service接口
 *
 * @author huang
 * @date 2025-03-14
 */
public interface IQrBatchService
{
    /**
     * 查询二维码管理
     *
     * @param batchId 二维码管理主键
     * @return 二维码管理
     */
    public QrBatch selectQrBatchByBatchId(Long batchId);

    /**
     * 查询二维码管理列表
     *
     * @param qrBatch 二维码管理
     * @return 二维码管理集合
     */
    public List<QrBatch> selectQrBatchList(QrBatch qrBatch);

    /**
     * 新增二维码管理
     *
     * @param qrBatch 二维码管理
     * @return 结果
     */
    QrBatch insertQrBatch(QrBatch qrBatch);

    /**
     * 修改二维码管理
     *
     * @param qrBatch 二维码管理
     * @return 结果
     */
    public int updateQrBatch(QrBatch qrBatch);

    /**
     * 批量删除二维码管理
     *
     * @param batchIds 需要删除的二维码管理主键集合
     * @return 结果
     */
    public int deleteQrBatchByBatchIds(Long[] batchIds);

    /**
     * 删除二维码管理信息
     *
     * @param batchId 二维码管理主键
     * @return 结果
     */
    public int deleteQrBatchByBatchId(Long batchId);

    Map<String, Object> getBatchStats();
}