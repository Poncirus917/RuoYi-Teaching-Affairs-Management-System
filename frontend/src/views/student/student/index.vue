<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
        <el-input v-model="queryParams.studentId" placeholder="请输入学号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="所属院系" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择院系" clearable>
          <el-option v-for="item in deptOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['student:student:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['student:student:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['student:student:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['student:student:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="姓名" align="center" prop="studentName" />
      <el-table-column label="性别" align="center" prop="sex">
        <template #default="scope">
          <el-tag :type="scope.row.sex === '男' ? '' : scope.row.sex === '女' ? 'danger' : 'info'">
            {{ scope.row.sex }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="所属学院" align="center">
        <template #default="scope">
          <div style="line-height: 1.4;">
            <div>{{ scope.row.deptName || '未分配' }}</div>
            <div style="color: #909399; font-size: 12px;">({{ scope.row.deptId }})</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="出生日期" align="center" prop="dateOfBirth" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.dateOfBirth, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="mobilePhone" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['student:student:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['student:student:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="studentRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入8位学号" :disabled="isUpdate" maxlength="8" show-word-limit />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="form.sex" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="未知" value="未知" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期" prop="dateOfBirth">
          <el-date-picker clearable v-model="form.dateOfBirth" type="date" value-format="YYYY-MM-DD" placeholder="请选择出生日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobilePhone">
          <el-input v-model="form.mobilePhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属院系" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择院系" style="width: 100%" filterable clearable>
            <el-option
              v-for="item in deptOptions"
              :key="item.deptId"
              :label="item.deptId + ' - ' + item.deptName"
              :value="item.deptId"
            />
          </el-select>
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

<script setup name="Student">
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/student/student";
// 修正：根据你提供的真实路径引入院系接口
import { listDepartment } from "@/api/department/department"; 

const { proxy } = getCurrentInstance();

const studentList = ref([]);
const deptOptions = ref([]); 
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const isUpdate = ref(false);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentId: null,
    studentName: null,
    deptName: null 
  },
  rules: {
    studentId: [
      { required: true, message: "学号不能为空", trigger: "blur" },
      { min: 8, max: 8, message: "学号长度必须为8位", trigger: "blur" }
    ],
    studentName: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
    sex: [{ required: true, message: "性别不能为空", trigger: "change" }],
    deptId: [{ required: true, message: "请选择院系", trigger: "change" }],
    mobilePhone: [{ pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询院系列表用于下拉框 */
function getDeptList() {
  listDepartment().then(response => {
    // 适配 RuoYi 常见的两种返回结构
    deptOptions.value = response.rows || response.data;
  }).catch(error => {
    console.error("加载院系列表失败:", error);
  });
}

/** 查询学生列表 */
function getList() {
  loading.value = true;
  listStudent(queryParams.value).then(response => {
    studentList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    studentId: null,
    studentName: null,
    sex: '未知',
    dateOfBirth: null,
    mobilePhone: null,
    deptId: null
  };
  proxy.resetForm("studentRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.studentId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  getDeptList(); // 打开弹窗时获取最新院系
  isUpdate.value = false;
  open.value = true;
  title.value = "添加学生信息";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getDeptList(); // 修改前也获取院系，防止列表为空
  isUpdate.value = true;
  const _studentId = row.studentId || ids.value;
  getStudent(_studentId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改学生信息";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["studentRef"].validate(valid => {
    if (valid) {
      if (isUpdate.value) {
        updateStudent(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addStudent(form.value).then(response => {
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
  // 核心修复：改为驼峰命名的 studentId
  const _studentIds = row.studentId || ids.value; 
  
  if (!_studentIds || _studentIds.length === 0) {
    proxy.$modal.msgWarning("请选择要删除的学生数据");
    return;
  }

  proxy.$modal.confirm('是否确认删除学号为 "' + _studentIds + '" 的学生数据项？').then(function() {
    return delStudent(_studentIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((err) => {
    // 如果点击“取消”按钮，不报任何错
    if (err === 'cancel' || err === undefined) return;

    // 强行关闭系统默认报错
    ElMessage.closeAll();

    // 业务化提示：这里做个判断，让提示更准确
    setTimeout(() => {
      // 如果后端传回了报错信息，显示后端信息；否则显示通用提示
      const errorMsg = err.msg || "删除失败：该学生可能已有选课记录或关联用户，请先清除关联数据！";
      proxy.$modal.msgError(errorMsg);
    }, 50);
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('student/student/export', {
    ...queryParams.value
  }, `student_${new Date().getTime()}.xlsx`)
}

// 初始化
getList();
getDeptList(); // 页面加载时就初始化院系选项，保证查询和表格显示时数据可用
</script>