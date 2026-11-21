<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程号" prop="courseId">
        <el-input v-model="queryParams.courseId" placeholder="请输入课程号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程名" prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="开课院系" prop="deptId">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['course:course:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['course:course:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['course:course:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['student:student:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="courseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程号" align="center" prop="courseId" />
      <el-table-column label="课程名" align="center" prop="courseName" />
      <el-table-column label="学分" align="center" prop="credit" />
      <el-table-column label="学时" align="center" prop="studyHours" />
      <el-table-column label="所属学院" align="center" prop="deptName" /> <el-table-column label="平时占比" align="center" prop="usualWeight" />
      <el-table-column label="期末占比" align="center" prop="finalWeight" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['course:course:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['course:course:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="courseRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程号" prop="courseId">
          <el-input 
            v-model="form.courseId" 
            placeholder="请输入 8 位课程号" 
            maxlength="8"
            show-word-limit
            :disabled="title === '修改课程信息'" 
          />
        </el-form-item>
        <el-form-item label="课程名" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名" />
        </el-form-item>
        <el-form-item label="所属院系" prop="deptId">
          <el-select v-model="form.deptId" filterable placeholder="请选择开课院系">
            <el-option v-for="item in deptOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number 
            v-model="form.credit" 
            :min="0" 
            :step="1" 
            placeholder="请输入学分"
          />
        </el-form-item>
        <el-form-item label="学时" prop="studyHours">
          <el-input-number v-model="form.studyHours" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="平时成绩占比" prop="usualWeight">
          <el-input-number v-model="form.usualWeight" :min="0" :max="1" :precision="1" :step="0.1" />
        </el-form-item>
        <el-form-item label="期末成绩占比" prop="finalWeight">
          <el-input-number v-model="form.finalWeight" :min="0" :max="1" :precision="1" :step="0.1" />
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

<script setup name="Course">
import { listCourse, getCourse, delCourse, addCourse, updateCourse } from "@/api/course/course"
import { listDepartment } from "@/api/department/department" // 必须导入院系接口

const { proxy } = getCurrentInstance()

const courseList = ref([])
const deptOptions = ref([]) // 院系下拉数据
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
    courseId: null,
    courseName: null,
    deptId: null,
  },
  rules: {
    courseId: [
      { required: true, message: "课程号不能为空", trigger: "blur" },
      // 添加长度约束
      { min: 8, max: 8, message: "课程号必须为 8 位数字", trigger: "blur" },
      // 可选：添加正则确保只输入数字
      { pattern: /^[0-9]+$/, message: "课程号只能是数字", trigger: "blur" }
    ],
    courseName: [{ required: true, message: "课程名不能为空", trigger: "blur" }],
    deptId: [{ required: true, message: "院系不能为空", trigger: "change" }],
    usualWeight: [{ required: true, message: "平时占比不能为空", trigger: "blur" }],
    finalWeight: [{ required: true, message: "期末占比不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询院系列表 */
function getDeptList() {
  listDepartment().then(response => {
    deptOptions.value = response.rows
  })
}

/** 查询课程列表 */
function getList() {
  loading.value = true
  listCourse(queryParams.value).then(response => {
    courseList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

/** 重置表单 - 这里是设置默认值的关键位置 */
function reset() {
  form.value = {
    courseId: null,
    courseName: null,
    credit: 4,       
    studyHours: 40,  
    deptId: null,
    usualWeight: 0.5,   
    finalWeight: 0.5   
  }
  proxy.resetForm("courseRef")
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
  ids.value = selection.map(item => item.courseId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加课程信息"
}

function handleUpdate(row) {
  reset()
  const _courseId = row.courseId || ids.value
  getCourse(_courseId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改课程信息"
  })
}

import { ElMessage } from 'element-plus'

/** 提交按钮 */
function submitForm() {
  proxy.$refs["courseRef"].validate(valid => {
    if (valid) {
      // 这里的逻辑校验保持不变
      const usual = parseFloat(form.value.usualWeight || 0);
      const final = parseFloat(form.value.finalWeight || 0);
      if ((usual + final).toFixed(1) !== "1.0") {
        proxy.$modal.msgError("错误：平时占比与期末占比之和必须等于 1.0");
        return;
      }
      
      if (title.value === "修改课程信息") {
        updateCourse(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch((err) => {
          if (err === 'cancel') return;
          
          // 强行关闭系统默认的长报错 (SQL 源码)
          ElMessage.closeAll();
          
          setTimeout(() => {
            // 这里显示你要求的业务美化提示
            proxy.$modal.msgError("修改失败：该课程已有教师负责，禁止更改课程名或所属院系！");
          }, 50);
        });
      } else {
        // 新增逻辑保持正常
        addCourse(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}


/** 删除按钮操作 */
function handleDelete(row) {
  // 1. 获取要删除的 ID（支持多选和单选）
  const _courseIds = row.courseId || ids.value;
  
  // 2. 弹出确认框
  proxy.$modal.confirm('是否确认删除课程编号为 "' + _courseIds + '" 的数据项？').then(function() {
    // 3. 调用 API 进行删除
    return delCourse(_courseIds);
  }).then(() => {
    // 4. 删除成功后的处理
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((err) => {
    // 5. 异常处理
    if (err === 'cancel') return; // 用户点击了“取消”，不做处理

    // 强行关闭系统默认弹出的长串 SQL 错误信息
    ElMessage.closeAll();

    // 延迟弹出人性化的业务提示
    setTimeout(() => {
      proxy.$modal.msgError(
        "删除失败：该课程已有教师负责或已安排开课计划，请先删除关联的教学班及任课信息！"
      );
    }, 50);
  });
}

// 初始化
getDeptList()
getList()
</script>