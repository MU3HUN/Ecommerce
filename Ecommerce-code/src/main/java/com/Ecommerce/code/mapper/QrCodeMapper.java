package com.Ecommerce.code.mapper;

import java.util.List;
import java.util.Map;

import com.Ecommerce.code.domain.QrCode;

/**
 * 贴码核销Mapper接口
 *
 * @author huang
 * @date 2025-03-14
 */
public interface QrCodeMapper
{
    /**
     * 查询贴码核销
     *
     * @param qrId 贴码核销主键
     * @return 贴码核销
     */
    public QrCode selectQrCodeByQrId(Long qrId);

    /**
     * 查询贴码核销列表
     *
     * @param qrCode 贴码核销
     * @return 贴码核销集合
     */
    public List<QrCode> selectQrCodeList(QrCode qrCode);

    /**
     * 新增贴码核销
     *
     * @param qrCode 贴码核销
     * @return 结果
     */
    public int insertQrCode(QrCode qrCode);

    /**
     * 修改贴码核销
     *
     * @param qrCode 贴码核销
     * @return 结果
     */
    public int updateQrCode(QrCode qrCode);

    /**
     * 删除贴码核销
     *
     * @param qrId 贴码核销主键
     * @return 结果
     */
    public int deleteQrCodeByQrId(Long qrId);

    /**
     * 批量删除贴码核销
     *
     * @param qrIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQrCodeByQrIds(Long[] qrIds);

    // 统计相关方法
    int countTodayGenerated();
    int countTodayRedeemed();
    int countTotalGenerated();
    int countTotalRedeemed();
    int countAbnormal();
    List<Map<String, Object>> getSevenDayTrend();
    int existsWithQrNumber(String qrNumber);

    QrCode selectQrCodeByQrNumber(String qrNumber);

}