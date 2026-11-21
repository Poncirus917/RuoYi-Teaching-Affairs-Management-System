<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="教师工号" prop="staffId">
        <el-input v-model="queryParams.staffId" placeholder="请输入教师工号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="教师姓名" prop="teacherName">
        <el-input v-model="queryParams.teacherName" placeholder="请输入教师姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="职称" prop="professionalRank">
        <el-input v-model="queryParams.professionalRank" placeholder="请输入职称" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['teacher:teacher:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['teacher:teacher:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['teacher:teacher:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['teacher:teacher:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="teacherList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="教师工号" align="center" prop="staffId" />
      <el-table-column label="教师姓名" align="center" prop="teacherName" />
      
      <el-table-column label="性别" align="center" prop="sex">
        <template #default="scope">
          <el-tag :type="scope.row.sex === '男' ? '' : scope.row.sex === '女' ? 'danger' : 'info'">
            {{ scope.row.sex }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="出生日期" align="center" prop="dateOfBirth" width="150">
        <template #default="scope">
          <span>{{ parseTime(scope.row.dateOfBirth, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="职称" align="center" prop="professionalRank" />

      <el-table-column label="所属学院" align="center" min-width="120">
        <template #default="scope">
          <div style="line-height: 1.4;">
            <div>{{ scope.row.deptName || '未分配' }}</div>
            <div style="color: #909399; font-size: 12px;">({{ scope.row.deptId }})</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['teacher:teacher:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['teacher:teacher:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="teacherRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教师工号" prop="staffId">
          <el-input v-model="form.staffId" placeholder="请输入教师工号" maxlength="8" show-word-limit :disabled="title === '修改教师信息'" />
        </el-form-item>
        <el-form-item label="教师姓名" prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="请输入教师姓名" />
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
        <el-form-item label="职称" prop="professionalRank">
          <el-input v-model="form.professionalRank" placeholder="请输入职称" />
        </el-form-item>
        <el-form-item label="所属院系" prop="deptId">
          <el-select v-model="form.deptId" filterable placeholder="请选择学院(支持搜索)" style="width: 100%">
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

<script setup name="Teacher">
import { listTeacher, getTeacher, delTeacher, addTeacher, updateTeacher } from "@/api/teacher/teacher"
import { listDepartment } from "@/api/department/department"
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()

const teacherList = ref([])
const deptOptions = ref([]) 
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
    staffId: null,
    teacherName: null,
    professionalRank: null,
    deptId: null,
    deptName: null
  },
  rules: {
    staffId: [
      { required: true, message: "教师工号不能为空", trigger: "blur" },
      { min: 8, max: 8, message: "工号必须为 8 位", trigger: "blur" }
    ],
    teacherName: [{ required: true, message: "教师姓名不能为空", trigger: "blur" }],
    sex: [{ required: true, message: "请选择性别", trigger: "change" }],
    deptId: [{ required: true, message: "请选择院系", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getDeptList() {
  listDepartment().then(response => {
    deptOptions.value = response.rows || response.data;
  });
}

function getList() {
  loading.value = true
  listTeacher(queryParams.value).then(response => {
    teacherList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    staffId: null,
    teacherName: null,
    sex: null,
    dateOfBirth: null,
    professionalRank: null,
    deptId: null
  }
  proxy.resetForm("teacherRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.staffId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  getDeptList()
  open.value = true
  title.value = "添加教师信息"
}

function handleUpdate(row) {
  reset()
  getDeptList()
  const _staffId = row.staffId || ids.value
  getTeacher(_staffId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改教师信息"
  })
}

// ... 其他 import 保持不变
/** 提交按钮 */
function submitForm() {
  proxy.$refs["teacherRef"].validate(valid => {
    if (valid) {
      if (title.value === "修改教师信息") {
        updateTeacher(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch((err) => {
          // 关键：用户点击取消不处理
          if (err === 'cancel') return;
          // 关键：清除长串报错，显示友好提示
          ElMessage.closeAll();
          setTimeout(() => {
            proxy.$modal.msgError("修改失败：该教师已有任课记录，禁止更改所属院系！");
          }, 50);
        });
      } else {
        addTeacher(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch((err) => {
          if (err === 'cancel') return;
          ElMessage.closeAll();
          setTimeout(() => {
            proxy.$modal.msgError("新增失败：工号可能已存在或数据不合法。");
          }, 50);
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _staffIds = row.staffId || ids.value;
  proxy.$modal.confirm('是否确认删除教师信息编号为"' + _staffIds + '"的数据项？').then(function() {
    return delTeacher(_staffIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((err) => {
    // 关键：如果是点击取消确认框，直接退出
    if (err === 'cancel') return;

    // 关键：强制关闭全局 request.js 弹出的长串英文
    ElMessage.closeAll();

    // 延时弹出美化提示，避免动画冲突
    setTimeout(() => {
      proxy.$modal.msgError("删除失败：该教师已有任课记录或负责教学班，请先清理关联数据！");
    }, 50);
  });
}

function handleExport() {
  proxy.download('teacher/teacher/export', {
    ...queryParams.value
  }, `teacher_${new Date().getTime()}.xlsx`)
}

getDeptList();
getList();
</script>