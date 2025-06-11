<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!-- 姓名搜索 -->
      <el-form-item label="姓名" prop="userName">
        <el-select v-model="queryParams.userName" placeholder="请选择姓名" clearable @change="handleQuery">
          <el-option v-for="item in userNames" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <!-- 状态筛选 -->
      <el-form-item label="核销状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="选择状态" clearable @change="handleQuery">
          <el-option label="已核销" value="已核销" />
          <el-option label="未核销" value="未核销" />
        </el-select>
      </el-form-item>
      <!-- 核销码模糊搜索 -->
      <el-form-item label="核销码" prop="qrNumber">
        <el-input v-model="queryParams.qrNumber" placeholder="输入核销码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- 日期范围筛选 -->
      <el-form-item label="生成时间" prop="params.startTime">
        <el-date-picker v-model="queryParams.params.startTime" type="datetime" placeholder="选择开始时间"
          value-format="yyyy-MM-dd HH:mm:ss" @change="handleQuery" />
      </el-form-item>
      <el-form-item label="至" prop="params.endTime">
        <el-date-picker v-model="queryParams.params.endTime" type="datetime" placeholder="选择结束时间"
          value-format="yyyy-MM-dd HH:mm:ss" @change="handleQuery" />
      </el-form-item>
      <!-- 核销时间范围筛选 -->
      <el-form-item label="核销时间" prop="params.redeemStartTime">
        <el-date-picker v-model="queryParams.params.redeemStartTime" type="datetime" placeholder="选择核销开始时间"
          value-format="yyyy-MM-dd HH:mm:ss" @change="handleQuery" />
      </el-form-item>
      <el-form-item label="至" prop="params.redeemEndTime">
        <el-date-picker v-model="queryParams.params.redeemEndTime" type="datetime" placeholder="选择核销结束时间"
          value-format="yyyy-MM-dd HH:mm:ss" @change="handleQuery" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-scan" size="mini" @click="openScanDialog">扫码核销</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['code:code:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['code:code:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="codeList" @selection-change="handleSelectionChange" @sort-change="handleSort">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="姓名" align="center" prop="userName" />
      <el-table-column label="二维码编号" align="center" prop="qrNumber" />
      <el-table-column label="核销状态" align="center" prop="status" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" sortable="custom">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="核销时间" align="center" prop="redeemTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.redeemTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="success" @click="handleRedeem(scope.row)" :disabled="scope.row.status === '已核销'">
            核销
          </el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 扫码核销对话框 -->
    <el-dialog title="扫码核销" :visible.sync="scanDialogVisible" width="80%" top="10vh" custom-class="scan-dialog">
      <div class="scan-content">
        <div class="scan-instructions">
          <h3>请扫描二维码进行核销</h3>
          <p>将二维码对准扫描设备，系统会自动识别并完成核销操作</p>
          <p>请将系统输入法调整为英文</p>
        </div>
        <div class="scan-input-container">
          <el-input v-model="scanInput" placeholder="请扫描或粘贴核销码" @keyup.enter.native="handleScanSubmit"
            @paste.native="handlePaste" ref="scanInput" class="scan-input" />
          <div class="scan-buttons">
            <el-button type="primary" @click="handleScanSubmit" class="scan-button">确认</el-button>
            <el-button @click="scanDialogVisible = false" class="scan-button">取消</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCode, delCode, updateCode } from "@/api/code/code";
import { listInfo } from "@/api/userinfo/info";
import axios from 'axios';

export default {
  name: "Code",
  data() {
    return {
      userNames: [],
      qrNumber: undefined,
      status: undefined,
      scanDialogVisible: false,
      scanInput: "",
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      codeList: [],
      queryParams: {
        params: {
          startTime: null,
          endTime: null
        },
        orderByColumn: 'createTime',
        isAsc: 'desc',
        pageNum: 1,
        pageSize: 10,
        batchId: null,
        qrNumber: null,
        status: '未核销',
        userName: null,
        createTime: null,
      },
    };
  },
  created() {
    this.loadUserNames();
    this.getList();
  },
  methods: {

    // 加载用户列表
    async loadUserNames() {
      try {

        const res = await listInfo({
          pageNum: 1,
          pageSize: 1000
        });
        this.userNames = res.rows.map(user => ({
          value: user.name,
          label: user.name
        }));
      } catch (error) {
        console.error("加载用户列表失败:", error);
      }
    },

    // 统一核销方法
    async redeemCode(qrId) {
      try {
        await updateCode({
          qrId,
          status: '已核销',
          redeemTime: new Date().toISOString()
        });
        await this.getList();
        this.$message.success("核销成功");
      } catch (error) {
        // 安全访问嵌套属性 + 提供默认错误信息
        const errorMessage = error?.response?.data?.message
          || error.message
          || '未知错误，请联系管理员';
        this.$message.error(`核销失败: ${errorMessage}`);
      }
    },

    // 扫码核销
    async handleScanSubmit() {
      if (!this.scanInput.trim()) return this.$message.warning('请输入核销码');

      try {
        const { rows } = await listCode({ qrNumber: this.scanInput });
        if (!rows?.length) return this.$message.error('未找到对应二维码');

        const target = rows[0];
        if (target.status === '已核销') return this.$message.info('该二维码已核销');

        await this.redeemCode(target.qrId);
      } finally {
        this.scanInput = "";
      }
    },

    // 按钮核销
    handleRedeem(row) {
      this.$confirm('确定要核销此二维码吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => this.redeemCode(row.qrId));
    },

    // 统一删除方法
    handleDelete(row) {
      const qrIds = row?.qrId ? [row.qrId] : this.ids.map(item => item.qrId);

      this.$confirm(`确认删除选中的${qrIds.length}条数据吗？`, "警告", {
        type: 'warning'
      }).then(() => {
        return delCode(qrIds);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error(`删除失败: ${error.message}`);
        }
      });
    },

    // 加载数据
    getList() {
      this.loading = true;
      listCode(this.queryParams).then(response => {
        this.codeList = response.rows.sort((a, b) => {
          // 主排序：创建时间倒序
          const timeDiff = new Date(b.createTime) - new Date(a.createTime);
          // 次排序：二维码编号倒序
          return timeDiff !== 0 ? timeDiff :
            b.qrNumber.localeCompare(a.qrNumber, undefined, { numeric: true });
        });
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('/code/code/export', {
        ...this.queryParams
      }, `code_${new Date().getTime()}.xlsx`)
    },

    // 辅助方法
    handleSelectionChange(selection) {
      this.ids = selection.map(item => ({ qrId: item.qrId }));
      this.multiple = !selection.length;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    openScanDialog() {
      this.scanDialogVisible = true;
      this.$nextTick(() => this.$refs.scanInput.focus());
    },
    handlePaste() {
      setTimeout(() => this.handleScanSubmit(), 10);
    },
    handleSort({ column, prop, order }) {
      this.queryParams.orderByColumn = prop;
      this.queryParams.isAsc = order === 'ascending' ? 'asc' : 'desc';
      this.getList();
    }
  }
};

</script>
<style scoped>
.scan-dialog {
  width: 80%;
  max-width: none;
  height: 80vh;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.scan-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.scan-instructions {
  text-align: center;
  margin-bottom: 30px;
}

.scan-instructions h3 {
  margin-bottom: 15px;
  color: #333;
}

.scan-instructions p {
  color: #666;
  font-size: 14px;
}

.scan-input-container {
  width: 100%;
  max-width: 600px;
}

.scan-input {
  width: 100%;
  margin-bottom: 20px;
}

.scan-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.scan-button {
  padding: 10px 20px;
  font-size: 14px;
}
</style>