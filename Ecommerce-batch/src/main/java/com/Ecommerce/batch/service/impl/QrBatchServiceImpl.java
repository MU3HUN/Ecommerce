package com.Ecommerce.batch.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Ecommerce.common.annotation.DataScope;
import com.Ecommerce.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.batch.mapper.QrBatchMapper;
import com.Ecommerce.batch.domain.QrBatch;
import com.Ecommerce.batch.service.IQrBatchService;

/**
 * 二维码管理Service业务层处理
 *
 * @author huang
 * @date 2025-03-14
 */
@Service
public class QrBatchServiceImpl implements IQrBatchService
{
    @Autowired
    private QrBatchMapper qrBatchMapper;

    /**
     * 查询二维码管理
     *
     * @param batchId 二维码管理主键
     * @return 二维码管理
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public QrBatch selectQrBatchByBatchId(Long batchId)
    {
        return qrBatchMapper.selectQrBatchByBatchId(batchId);
    }

    /**
     * 查询二维码管理列表
     *
     * @param qrBatch 二维码管理
     * @return 二维码管理
     */
    @Override
    @DataScope(deptAlias = "b", userAlias = "b")
    public List<QrBatch> selectQrBatchList(QrBatch qrBatch)
    {
        return qrBatchMapper.selectQrBatchList(qrBatch);
    }

    /**
     * 新增二维码管理
     *
     * @param qrBatch 二维码管理
     * @return 结果
     */
    @Override
    public QrBatch insertQrBatch(QrBatch qrBatch) {

        if(qrBatch.getGenerateTime() == null){
            qrBatch.setGenerateTime(new Date());
        }
        qrBatchMapper.insertQrBatch(qrBatch);
        return qrBatch;
    }

    /**
     * 修改二维码管理
     *
     * @param qrBatch 二维码管理
     * @return 结果
     */
    @Override
    public int updateQrBatch(QrBatch qrBatch)
    {
        return qrBatchMapper.updateQrBatch(qrBatch);
    }

    /**
     * 批量删除二维码管理
     *
     * @param batchIds 需要删除的二维码管理主键
     * @return 结果
     */
    @Override
    public int deleteQrBatchByBatchIds(Long[] batchIds) {
        if(batchIds == null || batchIds.length == 0) {
            throw new IllegalArgumentException("批次ID不能为空");
        }
        return qrBatchMapper.deleteQrBatchByBatchIds(batchIds);
    }

    /**
     * 删除二维码管理信息
     *
     * @param batchId 二维码管理主键
     * @return 结果
     */
    @Override
    public int deleteQrBatchByBatchId(Long batchId)
    {
        return qrBatchMapper.deleteQrBatchByBatchId(batchId);
    }

    @Override
    public Map<String, Object> getBatchStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取批次维度统计数据
        stats.put("todayGenerated", qrBatchMapper.countTodayGenerated());
        stats.put("totalGenerated", qrBatchMapper.countTotalGenerated());
        stats.put("dailyGenerated", qrBatchMapper.getDailyGenerated());

        return stats;
    }


}