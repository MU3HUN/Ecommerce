package com.Ecommerce.code.service.impl;

import java.util.*;

import com.Ecommerce.common.annotation.DataScope;
import com.Ecommerce.common.exception.ServiceException;
import com.Ecommerce.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.code.mapper.QrCodeMapper;
import com.Ecommerce.code.domain.QrCode;
import com.Ecommerce.code.service.IQrCodeService;

/**
 * 贴码核销Service业务层处理
 *
 * @author huang
 * @date 2025-03-14
 */
@Service
public class QrCodeServiceImpl implements IQrCodeService
{
    @Autowired
    private QrCodeMapper qrCodeMapper;

    /**
     * 查询贴码核销
     *
     * @param qrId 贴码核销主键
     * @return 贴码核销
     */
    @Override
    @DataScope(deptAlias = "q", userAlias = "q")
    public QrCode selectQrCodeByQrId(Long qrId)
    {
        return qrCodeMapper.selectQrCodeByQrId(qrId);
    }

    /**
     * 查询贴码核销列表
     *
     * @param qrCode 贴码核销
     * @return 贴码核销
     */
    @Override
    @DataScope(deptAlias = "q", userAlias = "q")
    public List<QrCode> selectQrCodeList(QrCode qrCode)
    {
        return qrCodeMapper.selectQrCodeList(qrCode);
    }

    /**
     * 新增贴码核销
     *
     * @param qrCode 贴码核销
     * @return 结果
     */
    @Override
    public int insertQrCode(QrCode qrCode) {

        if (qrCodeMapper.existsWithQrNumber(qrCode.getQrNumber()) > 0) {
            return -1;
        }
        return qrCodeMapper.insertQrCode(qrCode);
    }
    /**
     * 修改贴码核销
     *
     * @param qrCode 贴码核销
     * @return 结果
     */
    @Override
    public int updateQrCode(QrCode qrCode)
    {
        return qrCodeMapper.updateQrCode(qrCode);
    }

    /**
     * 批量删除贴码核销
     *
     * @param qrIds 需要删除的贴码核销主键
     * @return 结果
     */
    @Override
    public int deleteQrCodeByQrIds(Long[] qrIds) {
        if (qrIds == null || qrIds.length == 0) {
            throw new ServiceException("请选择要删除的二维码");
        }
        if (Arrays.stream(qrIds).anyMatch(Objects::isNull)) {
            throw new ServiceException("包含无效的二维码ID");
        }
        return qrCodeMapper.deleteQrCodeByQrIds(qrIds);
    }

    /**
     * 删除贴码核销信息
     *
     * @param qrId 贴码核销主键
     * @return 结果
     */
    @Override
    public int deleteQrCodeByQrId(Long qrId)
    {
        return qrCodeMapper.deleteQrCodeByQrId(qrId);
    }

    @Override
    public Map<String, Object> getCodeStats() {
        Map<String, Object> stats = new HashMap<>();

        int abnormal = qrCodeMapper.countAbnormal();
        // 从关联批次表获取生成数据
        int todayGenerated = qrCodeMapper.countTodayGenerated();
        int totalGenerated = qrCodeMapper.countTotalGenerated();
        // 核销数据仍从qr_code获取
        int todayRedeemed = qrCodeMapper.countTodayRedeemed();
        int totalRedeemed = qrCodeMapper.countTotalRedeemed();
        // 未核销总量
        int totalUnredeemed = totalGenerated - totalRedeemed;
        // 近7日趋势数据
        List<Map<String, Object>> trendData = qrCodeMapper.getSevenDayTrend();

        stats.put("todayStats", new HashMap<String, Integer>(){{
            put("generated", todayGenerated);
            put("redeemed", todayRedeemed);
            put("unredeemed", todayGenerated - todayRedeemed);
        }});

        stats.put("totalStats", new HashMap<String, Integer>(){{
            put("abnormal", abnormal);
            put("generated", totalGenerated);
            put("redeemed", totalRedeemed);
            put("unredeemed", totalUnredeemed);
        }});

        stats.put("dailyStats", trendData);

        return stats;
    }

    @Override
    public QrCode selectQrCodeByQrNumber(String qrNumber) {
        return qrCodeMapper.selectQrCodeByQrNumber(qrNumber);
    }

}