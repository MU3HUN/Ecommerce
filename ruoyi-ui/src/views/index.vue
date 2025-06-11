<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>二维码全生命周期看板</h2>
        <el-alert title="数据说明：实时统计二维码生成与核销情况，数据延迟不超过1分钟" type="info" :closable="false" style="margin-bottom: 20px" />
      </el-col>
    </el-row>

    <!-- 实时数据卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>今日生成</span>
            <i class="el-icon-data-line" style="float: right"></i>
          </div>
          <div class="data-item">
            <span class="data-value">{{ todayStats.generated }}</span>
            <span class="data-label">个条码</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>今日核销</span>
            <i class="el-icon-check" style="float: right"></i>
          </div>
          <div class="data-item">
            <span class="data-value">{{ todayStats.redeemed }}</span>
            <span class="data-label">次核销</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>未核销总量</span>
            <i class="el-icon-warning" style="float: right"></i>
          </div>
          <div class="data-item">
            <span class="data-value">{{ totalStats.unredeemed }}</span>
            <span class="data-label">待核销</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>总生成量</span>
            <i class="el-icon-pie-chart" style="float: right"></i>
          </div>
          <div class="data-item">
            <span class="data-value">{{ totalStats.generated }}</span>
            <span class="data-label">历史总量</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据趋势图 -->
    <el-row style="margin-top:20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>近30日数据趋势</span>
            <el-radio-group v-model="chartType" size="mini" style="float: right;margin-top: -5px">
              <el-radio-button label="line">折线图</el-radio-button>
              <el-radio-button label="bar">柱状图</el-radio-button>
            </el-radio-group>
          </div>
          <div id="chartContainer" style="height:400px;"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import { parseTime } from '@/utils';
import * as echarts from 'echarts';
import { getCodeStats } from "@/api/code/code";
import { getBatchStats } from "@/api/batch/batch";

export default {
  name: 'Dashboard',
  data() {
    return {
      chart: null,
      chartType: 'line',
      dateRange: [],
      loading: false,
      todayStats: {
        generated: 0,
        redeemed: 0,
        generatedGrowth: 0,
        redeemedGrowth: 0
      },
      totalStats: {
        generated: 0,
        redeemed: 0,
        unredeemed: 0,
        abnormal: 0
      },
      dailyStats: [],
      sortProp: 'date',
      sortOrder: 'descending'
    }
  },
  async mounted() {  // 从created改为mounted
    await this.loadData();
    this.initChart();  // 此时DOM已渲染
    window.addEventListener('resize', this.handleResize);
    this.$bus.$on('batch-generated', this.handleDataUpdate);
    this.$bus.$on('code-redeemed', this.handleDataUpdate);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
    this.$bus.$off('batch-generated', this.handleDataUpdate);
    this.$bus.$off('code-redeemed', this.handleDataUpdate);
    if (this.chart) this.chart.dispose();
  },
  filters: {
    toFixed(value, digits = 1) {
      return value?.toFixed(digits) || '0.0';
    }
  },
  methods: {
    // 加载统计数据
    async loadData() {
      this.loading = true;
      try {
        const [codeRes, batchRes] = await Promise.all([
          getCodeStats({
            startDate: this.dateRange[0],
            endDate: this.dateRange[1]
          }),
          getBatchStats()
        ]);

        // 合并来自code和batch的数据
        this.todayStats = {
          ...codeRes.data.todayStats,
          generated: batchRes.data.todayGenerated
        };

        this.totalStats = {
          ...codeRes.data.totalStats,
          generated: batchRes.data.totalGenerated,
          abnormal: codeRes.data.abnormalCodes
        };

        // 合并每日数据
        this.dailyStats = this.mergeDailyData(
          codeRes.data.dailyStats,
          batchRes.data.dailyGenerated
        );

        // 计算增长率
        this.calculateGrowthRate();

      } finally {
        this.loading = false;
        this.updateChart();
      }
    },

    // 合并来自两个接口的每日数据
    mergeDailyData(codeStats, batchStats) {
      const dateMap = new Map();

      // 处理批次生成数据（来自batchStats）
      batchStats.forEach(item => {
        dateMap.set(item.date, {
          generated: item.generated,
          redeemed: 0,
          unredeemed: item.generated
        });
      });

      // 叠加核销数据（来自codeStats）
      codeStats.forEach(item => {
        const exist = dateMap.get(item.date) || { generated: 0 };
        dateMap.set(item.date, {
          ...exist,
          redeemed: item.redeemed,
          unredeemed: exist.generated - item.redeemed
        });
      });

      return Array.from(dateMap.entries())
        .map(([date, stats]) => ({
          date,
          generated: stats.generated,
          redeemed: stats.redeemed,
          unredeemed: stats.unredeemed,
          redeemedRate: ((stats.redeemed / stats.generated) * 100 || 0).toFixed(1)
        }))
        .sort((a, b) => this.sortOrder === 'ascending'
          ? a.date.localeCompare(b.date)
          : b.date.localeCompare(a.date));
    },

    // 计算增长趋势
    calculateGrowthRate() {
      if (this.dailyStats.length < 2) return;

      const yesterday = this.dailyStats[1];
      const today = this.dailyStats[0];

      this.todayStats.generatedGrowth = yesterday.generated
        ? (((today.generated - yesterday.generated) / yesterday.generated) * 100).toFixed(1)
        : '100.0';

      this.todayStats.redeemedGrowth = yesterday.redeemed
        ? (((today.redeemed - yesterday.redeemed) / yesterday.redeemed) * 100).toFixed(1)
        : '100.0';
    },

    // 图表初始化
    initChart() {
      const container = document.getElementById('chartContainer');
      if (!container) {
        console.error('图表容器未找到');
        return;
      }
      this.chart = echarts.init(container);
      this.updateChart();  // 初始化后立即更新
    },

    // 更新图表
    updateChart() {
      if (!this.chart) return;
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['生成数量', '核销数量'],
          bottom: 10
        },
        xAxis: {
          type: 'category',
          data: [...this.dailyStats].reverse().map(d => d.date)
        },
        yAxis: [
          {
            type: 'value',
            name: '数量'
          }
        ],
        series: [
          {
            name: '生成数量',
            type: this.chartType,
            data: [...this.dailyStats].reverse().map(d => d.generated),
            itemStyle: { color: '#36a3f7' }
          },
          {
            name: '核销数量',
            type: this.chartType,
            data: [...this.dailyStats].reverse().map(d => d.redeemed),
            itemStyle: { color: '#34bfa3' }
          }
        ]
      };
      this.chart.setOption(option);
    },

    // 事件处理
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    handleDataUpdate() {
      this.loadData();
    },
    handleDateChange() {
      this.loadData();
    },
    handleSort({ prop, order }) {
      this.sortProp = prop;
      this.sortOrder = order;
      this.dailyStats = [...this.dailyStats].sort((a, b) => {
        const valA = prop === 'redeemedRate' ? parseFloat(a[prop]) : a[prop];
        const valB = prop === 'redeemedRate' ? parseFloat(b[prop]) : b[prop];
        return order === 'ascending' ? valA - valB : valB - valA;
      });
    },

    // 辅助方法
    compareClass(value) {
      return value > 0 ? 'success' : value < 0 ? 'danger' : '';
    },
    getRateType(rate) {
      rate = parseFloat(rate);
      if (rate >= 90) return 'success';
      if (rate >= 70) return 'warning';
      return 'danger';
    },
    sortRate(a, b) {
      return parseFloat(a.redeemedRate) - parseFloat(b.redeemedRate);
    },
    showDetail(date) {
      this.$router.push({
        path: '/code/code',
        query: { date }
      });
    }
  },
  watch: {
    chartType() {
      this.updateChart();
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  h2 {
    color: #303133;
    margin-bottom: 10px;
    font-size: 24px;
  }

  .data-item {
    text-align: center;
    padding: 15px 0;
    position: relative;

    .data-value {
      font-size: 28px;
      font-weight: bold;
      color: #409EFF;
      display: block;
      margin-bottom: 8px;
    }

    .data-label {
      font-size: 14px;
      color: #909399;
      display: block;
    }

    .data-compare {
      margin-top: 10px;
      font-size: 12px;
      color: #666;

      .success {
        color: #67C23A;
      }

      .danger {
        color: #F56C6C;
      }

      .warning {
        color: #E6A23C;
      }
    }
  }

  .el-card {
    transition: transform 0.3s;

    &:hover {
      transform: translateY(-5px);
    }
  }
}

::v-deep .el-table__row:hover {
  transform: scale(1.02);
  transition: transform 0.2s;
}
</style>