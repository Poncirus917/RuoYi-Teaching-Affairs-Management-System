<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="院系代号" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入院系代号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="院系名" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入院系名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['department:department:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['department:department:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['department:department:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['department:department:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="departmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="院系代号" align="center" prop="deptId" />
      <el-table-column label="院系名" align="center" prop="deptName" />
      <el-table-column label="地址" align="center" prop="deptAddress" />
      <el-table-column label="联系电话" align="center" prop="deptPhone" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['department:department:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['department:department:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改院系设置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="departmentRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="院系代号" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入院系代号" 
            :disabled="form.deptId !== null && title === '修改院系设置'" />
        </el-form-item>
        <el-form-item label="院系名" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入院系名" />
        </el-form-item>
        <el-form-item label="地址" prop="deptAddress">
          <el-input v-model="form.deptAddress" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="deptPhone">
          <el-input 
            v-model="form.deptPhone" 
            placeholder="请输入 8 位联系电话" 
            maxlength="8"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Department">
import { listDepartment, getDepartment, delDepartment, addDepartment, updateDepartment } from "@/api/department/department"

const { proxy } = getCurrentInstance()

const departmentList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deptId: null,
    deptName: null,
  },
  rules: {
    deptId: [
      { required: true, message: "院系代号不能为空", trigger: "blur" }
    ],
    deptName: [
      { required: true, message: "院系名不能为空", trigger: "blur" }
    ],
    deptPhone: [
      { min: 8, max: 8, message: "联系电话必须为 8 位", trigger: "blur" },
      { pattern: /^\d{8}$/, message: "请输入 8 位数字电话", trigger: "blur" }
    ], 
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询院系设置列表 */
function getList() {
  loading.value = true
  listDepartment(queryParams.value).then(response => {
    departmentList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    deptId: null,
    deptName: null,
    deptAddress: null,
    deptPhone: null
  }
  proxy.resetForm("departmentRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.deptId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加院系设置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _deptId = row.deptId || ids.value
  getDepartment(_deptId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改院系设置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["departmentRef"].validate(valid => {
    if (valid) {
      // 依靠对话框标题 title 判断是新增还是修改，而不是依靠 ID 是否为空
      if (title.value === "修改院系设置") {
        updateDepartment(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDepartment(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

import { ElMessage } from 'element-plus'

/** 删除按钮操作 */
function handleDelete(row) {
  const _deptIds = row.deptId || ids.value;
  proxy.$modal.confirm('确定要删除院系代号为 "' + _deptIds + '" 的数据项吗？').then(() => {
    return delDepartment(_deptIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((err) => {
    if (err === 'cancel') return;
    
    // 1. 立即强行关闭所有已经弹出的消息气泡（包括 request.js 弹出的那长串）
    ElMessage.closeAll(); 
    
    // 2. 延迟极短时间弹出美化后的消息，防止气泡重叠动画冲突
    setTimeout(() => {
      proxy.$modal.msgError("操作失败：该院系下仍有教师、学生或课程，无法删除！");
    }, 50);
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('department/department/export', {
    ...queryParams.value
  }, `department_${new Date().getTime()}.xlsx`)
}

getList()
</script>
