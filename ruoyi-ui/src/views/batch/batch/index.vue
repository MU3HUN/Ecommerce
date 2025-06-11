<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="姓名" prop="userName">
        <el-select v-model="queryParams.userName" placeholder="请选择姓名" clearable @change="handleQuery">
          <el-option v-for="item in userNames" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-scan" size="mini" @click="openScanDialog">扫码核销</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['batch:batch:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['batch:batch:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['batch:batch:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="batchList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次ID" align="center" prop="batchId" sortable>
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="用户" align="center">
        <template slot-scope="scope">
          {{ getUserName(scope.row.usersId) || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="起始序号" align="center" prop="startSeq" />
      <el-table-column label="结束序号" align="center" prop="endSeq" />
      <el-table-column label="总数" align="center" prop="total" />
      <el-table-column label="已核销" align="center" prop="redeemed" />
      <el-table-column label="未核销" align="center" prop="unredeemed" />
      <template>
        <el-table-column label="生成时间" align="center" prop="generateTime" width="180">
          <template slot-scope="scope">
            {{ parseTime(scope.row.generateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </template>
        </el-table-column>
      </template>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['batch:batch:edit']">修改及打印</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['batch:batch:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改二维码管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1600px" top="1vh" custom-class="fullscreen-dialog"
      @closed="reset">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" @submit.native.prevent>
        <el-alert v-if="title === '添加二维码管理'" title="若一次性新建大批量贴码请耐心等待" type="warning" :closable="false"
          style="margin-bottom: 20px" />

        <el-alert v-if="title === '修改二维码批次'" title="若有新增贴码，请点击[生成预览]后点击[保存批次]" type="warning" :closable="false"
          style="margin-bottom: 20px" />
        <el-row :gutter="20">
          <!-- 用户选择 -->
          <el-col :span="6">
            <el-form-item label="关联用户" prop="usersId">
              <el-select v-model="form.usersId" filterable placeholder="请选择用户" @change="handleUserChange"
                style="width:100%">
                <el-option v-for="user in userList" :key="user.usersId" :label="`${user.name} (${user.phone})`"
                  :value="user.usersId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="系统用户" prop="userId">
              <el-select v-model="form.userId" placeholder="请选择系统用户" filterable clearable style="width:100%">
                <el-option v-for="user in sysUserOptions" :key="user.userId"
                  :label="`${user.userName} (${user.nickName})`" :value="user.userId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="部门" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择部门" style="width:100%" disabled>
                <el-option v-for="dept in deptOptions" :key="dept.deptId" :label="dept.deptName" :value="dept.deptId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <!-- 起始序号 -->
          <el-col :span="5">
            <el-form-item label="起始序号" prop="startSeq">
              <el-input v-model="form.startSeq" placeholder="10001" maxlength="5" style="width:120px"></el-input>
            </el-form-item>
          </el-col>

          <!-- 生成数量 -->
          <el-col :span="5">
            <el-form-item label="生成数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" :max="1000" @change="calcEndSeq" style="width:140px" />
            </el-form-item>
          </el-col>

          <!-- 结束序号 -->
          <el-col :span="5">
            <el-form-item label="结束序号" prop="endSeq">
              <el-input v-model="form.endSeq" disabled style="width:120px" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 二维码预览 -->
      <div v-if="qrPreviews.length" class="qr-preview">
        <div v-for="(qr, index) in qrPreviews" :key="index" class="qr-item"
          :class="{ 'redeemed': qr.status === '已核销' }">
          <img :src="qr.data" class="qr-img">
          <div class="qr-number">{{ qr.number }}</div>
        </div>
      </div>

      <div slot="footer">
        <el-button type="primary" @click="submitForm">保存批次</el-button>
        <el-button @click="generateQRCodes">生成预览</el-button>
        <el-button @click="printQRCodes" :disabled="!qrPreviews.length">打印</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>

    </el-dialog>

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
import { listBatch, getBatch, delBatch, addBatch, updateBatch } from "@/api/batch/batch";
import QRCode from "qrcode";
import { listInfo } from "@/api/userinfo/info";
import { pinyin } from 'pinyin-pro';
import { addCode } from "@/api/code/code";
import { updateCode, listCode, getQRStatus } from '@/api/code/code';
import { listUser } from "@/api/system/user";
import { listDept } from "@/api/system/dept";


export default {
  name: "Batch",
  data() {
    return {
      sysUserOptions: [],
      deptOptions: [],
      codeStatusMap: {},
      scanDialogVisible: false,
      scanInput: "",
      originalForm: { startSeq: null, endSeq: null },
      userOptions: [],
      currentDateCode: this.getDateCode(),
      userNames: [],
      isEditing: false,
      userList: [],
      lastSequence: localStorage.getItem('lastQRSequence') || 0,
      qrPreviews: [],
      pinyinCache: new Map(),
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 二维码管理表格数据
      batchList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        orderByColumn: 'generateTime',
        isAsc: 'desc',
        pageNum: 1,
        pageSize: 10,
        generateTime: null,
      },
      // 表单参数
      form: {
        usersId: null,
        userName: '',
        startSeq: '10001',
        endSeq: '',
        quantity: 1
      },
      // 表单校验
      rules: {
        usersId: [{ required: true, message: '请选择用户', trigger: 'change' }],
        startSeq: [
          { required: true, message: '请输入起始序号', trigger: 'blur' },
          { pattern: /^\d{5}$/, message: '必须为5位数字' }
        ],
        quantity: [
          { required: true, message: '请输入生成数量', trigger: 'blur' },
          { type: 'number', message: '请输入有效数字', trigger: 'blur' }
        ]
      }
    };
  },

  async created() {
    await this.loadSysUsers();
    await this.loadDepts();
    await this.loadUserNames();
    await this.loadUsers();
    this.getList();
  },


  methods: {
    // 加载系统用户
    async loadSysUsers() {
      try {
        const res = await listUser();
        this.sysUserOptions = res.rows;
      } catch (error) {
        console.error("加载系统用户失败:", error);
      }
    },

    // 加载部门
    async loadDepts() {
      try {
        const res = await listDept();
        this.deptOptions = res.data;
      } catch (error) {
        console.error("加载部门失败:", error);
      }
    },

    async checkQRStatus(number) {
      try {
        const res = await getQRStatus(number);
        return res.msg === '已核销' ? '已核销' : '未核销'; // 根据后台返回的msg字段判断
      } catch (e) {
        return '未核销';
      }
    },

    openScanDialog() {
      this.scanDialogVisible = true;
      this.$nextTick(() => {
        // 添加空值保护
        if (this.$refs.scanInput && this.$refs.scanInput.focus) {
          this.$refs.scanInput.focus();
        }
      });
    },

    async handleScanSubmit() {
      if (!this.scanInput.trim()) return this.$message.warning('请输入核销码');
      try {
        const { rows } = await listCode({ qrNumber: this.scanInput });
        if (!rows?.length) return this.$message.error('未找到对应二维码');

        const target = rows[0];
        if (target.status === '已核销') return this.$message.info('该二维码已核销');

        await updateCode({
          qrId: target.qrId,
          status: '已核销',
          redeemTime: new Date().toISOString()
        });

        this.$message.success("核销成功");
        this.scanInput = "";
      } catch (error) {
        const msg = error?.response?.data?.message || error.message || '核销失败';
        this.$message.error(msg);
      }
    },

    handlePaste() {
      setTimeout(() => this.handleScanSubmit(), 10);
    },

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
        console.error('获取用户姓名列表失败:', error);
      }
    },

    handleUserChange(usersId) {
      const selectedUser = this.userList.find(u => u.usersId === usersId);
      if (selectedUser) {
        // 自动填充系统用户和部门
        this.form.deptId = selectedUser.deptId;
        this.form.userName = selectedUser.name;
      } else {
        this.form.deptId = null;
        this.form.userName = '';
      }
    },

    getUserName(usersId) {
      const user = this.userList.find(u => u.usersId === usersId);
      return user?.name;
    },

    //中文转拼音首字母方法
    getInitials(name) {
      if (!name) return '';
      if (this.pinyinCache.has(name)) {
        return this.pinyinCache.get(name);
      }

      const result = pinyin(name, {
        pattern: 'first',
        toneType: 'none',
        type: 'array'
      }).join('').toLowerCase();

      this.pinyinCache.set(name, result);
      return result;
    },


    // 加载用户列表
    async loadUsers() {
      const res = await listInfo({
        pageNum: 1,
        pageSize: 1000
      });
      this.userList = res.rows;
      this.userOptions = res.rows;
    },

    // 计算结束序号
    calcEndSeq() {
      if (this.form.startSeq && this.form.quantity) {
        const start = parseInt(this.form.startSeq);
        this.form.endSeq = String(start + this.form.quantity - 1)
          .padStart(5, '0');
      }
    },

    // 生成二维码方法
    async generateQRCodes() {
      try {
        this.qrPreviews = [];
        const user = this.userList.find(u => u.usersId === this.form.usersId);
        if (!user) {
          this.$message.error('请先选择用户');
          return { success: false, error: new Error('未选择用户') };
        }

        // 生成二维码前缀（姓名首字母+日期编码）
        const todayCode = this.getDateCode();
        const initials = this.getInitials(user.name);
        const prefix = `${initials}${todayCode}`;
        const start = parseInt(this.form.startSeq);
        const end = start + this.form.quantity - 1;
        const duplicates = [];

        // 循环生成每个二维码
        for (let seq = start; seq <= end; seq++) {
          const number = `${prefix}${String(seq).padStart(5, '0')}`;

          // 检查二维码状态
          const status = await this.checkQRStatus(number);

          try {
            // 生成二维码图片
            const dataUrl = await QRCode.toDataURL(number, { width: 200, margin: 1 });
            this.qrPreviews.push({ number, data: dataUrl, status });

            // 保存到数据库
            await addCode({
              qrNumber: number,
              batchId: this.form.batchId,
              userName: user.name,
              status: '未核销',
              createTime: new Date().toISOString(),
              redeemTime: null,
              userId: this.form.userId,
              deptId: this.form.deptId
            }).catch(() => { });
          } catch (err) {
            if (err.message.includes('Duplicate entry')) {
              duplicates.push(number);
            }
          }
        }

        return { success: true };
      } catch (err) {
        console.error('生成流程失败:', err);
        return { success: false };
      }
    },

    // 获取日期编码
    getDateCode() {
      const now = new Date();
      // 每天生成独立缓存
      return now.toISOString().slice(2, 4) +
        String(now.getUTCMonth() + 1).padStart(2, '0') +
        String(now.getUTCDate()).padStart(2, '0');
    },

    // 打印功能
    async printQRCodes() {
      if (!this.qrPreviews.length) return;

      const printContent = window.document.createElement('div');
      printContent.innerHTML = `
    <html>
      <head>
        <title>批量打印二维码</title>
        <style>
          @page { 
            size: 38mm 28mm;
            margin: 0; 
          }
          .qr-page {  
            width: 38mm; 
            height: 28mm;
            display: flex; 
            align-items: center;
            flex-direction: column;
            page-break-after: always;
          }
          .qr-image {
            flex: 1;
            max-width: 95%;
            max-height: 75%;
          }
          .qr-text {
            font-size: 3mm;
            text-align: center;
            word-break: break-all;
            padding: 0 1mm;
            font-family: 'Arial Narrow', sans-serif;
            letter-spacing: 0.2mm;
          }
        </style>
      </head>
      <body>
        ${this.qrPreviews.map(qr => `
          <div class="qr-page">
            <img class="qr-image" src="${qr.data}" />
            <div class="qr-text">${qr.number}</div>
          </div>
        `).join('')}
      </body>
    </html>
  `;

      const printWindow = window.open('', '_blank');
      printWindow.document.write(printContent.innerHTML);
      printWindow.document.close();
      printWindow.onafterprint = () => printWindow.close();
      printWindow.print();
    },

    // 自动填充序号方法
    autoFillStartSeq() {
      const todayCode = this.getDateCode();
      const storedData = JSON.parse(localStorage.getItem('qrCodeData') || '[]');
      const existingNumbers = new Set(
        storedData
          .filter(item => item.qrNumber.includes(todayCode))
          .map(item => parseInt(item.qrNumber.slice(-5)))
      );

      let newSeq = 1;
      while (existingNumbers.has(newSeq)) {
        newSeq++;
      }
      this.form.startSeq = String(newSeq).padStart(5, '0');
      this.calcEndSeq();
    },

    async submitForm() {
      try {
        // 日期检测逻辑
        if (this.title === '添加二维码管理') {
          const today = new Date().toISOString().split('T')[0];
          const existing = this.batchList.some(item =>
            item.usersId === this.form.usersId &&
            item.generateTime &&
            item.generateTime.startsWith(today)
          );

          if (existing) {
            this.$modal.msgError('该用户今日已创建过批次，请点击[修改及打印]按钮新增二维码');
            return;
          }
        }

        // 确保选择了关联用户
        if (!this.form.usersId) {
          this.$modal.msgError('请选择关联用户');
          return;
        }

        // 从关联用户获取系统用户和部门信息
        const selectedUser = this.userList.find(u => u.usersId === this.form.usersId);
        if (!selectedUser) {
          this.$modal.msgError('选择的用户信息无效');
          return;
        }

        // 提交批次数据
        const payload = {
          batchId: this.form.batchId,
          usersId: this.form.usersId,
          startSeq: this.form.startSeq,
          endSeq: this.form.endSeq,
          generateTime: this.form.generateTime || new Date().toISOString(),
          userId: this.form.userId,
          deptId: selectedUser.deptId
        };

        const apiMethod = this.form.batchId ? updateBatch : addBatch;
        const res = await apiMethod(payload);

        if (!this.form.batchId && res.data?.batchId) {
          this.form.batchId = res.data.batchId;
        }

        await this.$refs.form.validate();
        if (this.qrPreviews.length === 0) {
          const generateResult = await this.generateQRCodes();
          if (!generateResult || generateResult.error) {
            this.$modal.msgError('二维码生成失败，请检查参数');
            return;
          }
        }

        // 提示成功并刷新列表
        this.$modal.msgSuccess('批次保存成功');
        await this.getList();
        this.open = false;
      } catch (error) {
        if (error instanceof Error) {
          console.error('保存失败:', error);
          this.$modal.msgError(`保存失败: ${error.message}`);
        } else {
          this.$modal.msgError('请完善表单信息');
        }
      }
    },

    // getList方法
    async getList() {
      this.loading = true;
      try {
        const res = await listBatch(this.queryParams);
        let batchList = res.rows;

        // 添加排序逻辑
        batchList.sort((a, b) => {
          return new Date(b.generateTime) - new Date(a.generateTime);
        });

        // 根据姓名筛选
        if (this.queryParams.userName) {
          batchList = batchList.filter(item => this.getUserName(item.usersId).includes(this.queryParams.userName));
        }

        this.batchList = batchList;
        this.total = res.total;
      } finally {
        this.loading = false;
      }
    },

    // 获取用户名的辅助方法
    getUserName(usersId) {
      const user = this.userList.find(u => u.usersId === usersId);
      return user ? user.name : '未知用户';
    },

    // 表单重置
    reset() {
      this.form = {
        batchId: null,
        usersId: null,
        startSeq: '10001',
        endSeq: null,
        generateTime: null
      };
      this.resetForm("form");
      this.qrPreviews = [];
    },

    /** 重置按钮操作 */
    handleQuery() {
      if (this.queryParams.generateTime) {
        this.queryParams.startTime = this.queryParams.generateTime
        this.queryParams.endTime = this.queryParams.generateTime
      } else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      this.queryParams.pageNum = 1
      this.getList()
    },

    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.generateTime = null;
      this.handleQuery();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => ({ batchId: item.batchId }));
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      // 清空用户选择
      this.form.usersId = null;
      this.reset();
      this.qrPreviews = [];
      this.open = true;
      this.title = "添加二维码管理";
    },
    /** 修改按钮操作 */
    async handleUpdate(row) {
      this.reset();
      const batchId = row.batchId;
      const loadingInstance = this.$loading({
        lock: true,
        text: '数据加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      try {
        const res = await getBatch(batchId);

        this.originalForm = {
          startSeq: parseInt(res.data.startSeq),
          endSeq: parseInt(res.data.endSeq)
        };

        // 设置表单数据
        this.form = {
          batchId: res.data.batchId,
          usersId: res.data.usersId,
          startSeq: res.data.startSeq,
          endSeq: res.data.endSeq,
          quantity: parseInt(res.data.endSeq) - parseInt(res.data.startSeq) + 1,
          userId: res.data.userId,
          deptId: res.data.deptId
        };

        // 自动生成预览
        await this.$nextTick();
        await this.generateQRCodes();

        this.open = true;
        this.title = "修改二维码批次";
      } catch (error) {
        this.$message.error("加载数据失败：" + error.message);
      } finally {
        loadingInstance.close();  //关闭加载动画
      }
    },

    cancel() {
      this.open = false;
      this.reset();
      this.qrPreviews = [];
    },

    // 修改删除方法
    handleDelete(row) {
      const batchIds = row?.batchId ? [row.batchId] : this.ids.map(item => item.batchId);
      this.$confirm(`确认删除选中的${batchIds.length}个批次吗？`, "警告", {
        type: 'warning'  // 添加确认框类型配置
      }).then(() => {
        return delBatch(batchIds);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(error => {
        if (error !== 'cancel') {  // 处理取消操作
          this.$message.error(`删除失败: ${error.message}`);
        }
      });
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('batch/batch/export', {
        ...this.queryParams
      }, `batch_${new Date().getTime()}.xlsx`);
    },
  }
};
</script>

<style scoped>
.qr-item.redeemed {
  background: #e1f3d8 !important;
  border: 2px solid #67c23a !important;
}

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

:deep(.el-dialog__body) {
  max-height: calc(100vh - 150px);
  overflow-y: auto;
}

.el-loading-spinner {
  font-size: 18px;
}

.el-loading-spinner i {
  font-size: 28px;
  margin-bottom: 8px;
}

.qr-preview {
  max-height: calc(100vh - 300px);
  min-height: 200px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 15px;
  padding: 10px;
  border: 1px solid #ebeef5;
}

.qr-preview::-webkit-scrollbar {
  width: 6px;
}

.qr-preview::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.qr-item {
  text-align: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.qr-img {
  width: 120px !important;
  height: 120px !important;
  object-fit: contain;
}

.qr-number {
  font-size: 0.8em;
  margin-top: 5px;
  word-break: break-all;
}

@page {
  size: 38mm 28mm landscape;
  margin: 0;
}
</style>