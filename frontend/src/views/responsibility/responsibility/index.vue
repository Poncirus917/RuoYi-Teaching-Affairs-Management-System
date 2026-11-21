<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="教师工号" prop="staffId">
        <el-input v-model="queryParams.staffId" placeholder="请输入教师工号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="教师姓名" prop="teacherName">
        <el-input v-model="queryParams.teacherName" placeholder="请输入教师姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程名称" prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="开课学院" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="筛选学院" clearable style="width: 160px">
          <el-option
            v-for="item in deptOptions"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['responsibility:responsibility:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['responsibility:responsibility:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['responsibility:responsibility:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['student:student:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="responsibilityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="开课学院" align="center" prop="deptName">
        <template #default="scope">
          <span>{{ scope.row.deptName || '未分配' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任课教师" align="center">
        <template #default="scope">
          <span v-if="scope.row.teacherName">{{ scope.row.teacherName }}</span>
          <div style="font-size: 12px; color: #999">({{ scope.row.staffId }})</div>
        </template>
      </el-table-column>
      <el-table-column label="任教课程" align="center">
        <template #default="scope">
          <span v-if="scope.row.courseName">{{ scope.row.courseName }}</span>
          <div style="font-size: 12px; color: #999">({{ scope.row.courseId }})</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['responsibility:responsibility:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['responsibility:responsibility:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="responsibilityRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属学院" prop="deptId">
          <el-select 
            v-model="form.deptId" 
            placeholder="请先选择学院" 
            clearable 
            style="width: 100%"
            @change="handleDeptChange"
          >
            <el-option
              v-for="item in deptOptions"
              :key="item.deptId"
              :label="item.deptName"
              :value="item.deptId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="任课教师" prop="staffId">
          <el-select 
            v-model="form.staffId" 
            filterable 
            :placeholder="form.deptId ? '请选择教师' : '请先选择学院'" 
            :disabled="!form.deptId"
            style="width: 100%"
          >
            <el-option
              v-for="item in filteredTeachers"
              :key="item.staffId"
              :label="`${item.teacherName} (${item.staffId})`"
              :value="item.staffId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="任教课程" prop="courseId">
          <el-select 
            v-model="form.courseId" 
            filterable 
            :placeholder="form.deptId ? '请选择课程' : '请先选择学院'" 
            :disabled="!form.deptId"
            style="width: 100%"
          >
            <el-option
              v-for="item in filteredCourses"
              :key="item.courseId"
              :label="`${item.courseName} (${item.courseId})`"
              :value="item.courseId"
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

<script setup name="Responsibility">
import { listResponsibility, getResponsibility, delResponsibility, addResponsibility, updateResponsibility } from "@/api/responsibility/responsibility"
import { listTeacher } from "@/api/teacher/teacher"
import { listCourse } from "@/api/course/course"
import { listDepartment } from "@/api/department/department" 

const { proxy } = getCurrentInstance()

const responsibilityList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([]) 
const single = ref(true)
const multiple = ref(true)
const selectRows = ref([])
const total = ref(0)
const title = ref("")

const teacherOptions = ref([])
const courseOptions = ref([])
const deptOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    staffId: null,
    courseId: null,
    teacherName: null,
    courseName: null,
    deptId: null
  },
  rules: {
    deptId: [{ required: true, message: "请选择所属学院", trigger: "change" }],
    staffId: [{ required: true, message: "请选择任课教师", trigger: "change" }],
    courseId: [{ required: true, message: "请选择任教课程", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 计算属性：根据当前 form.deptId 过滤教师
const filteredTeachers = computed(() => {
  if (!form.value.deptId) return []
  return teacherOptions.value.filter(t => t.deptId === form.value.deptId)
})

// 计算属性：根据当前 form.deptId 过滤课程
const filteredCourses = computed(() => {
  if (!form.value.deptId) return []
  return courseOptions.value.filter(c => c.deptId === form.value.deptId)
})

function handleDeptChange() {
  form.value.staffId = null
  form.value.courseId = null
}

function getDeptList() {
  listDepartment().then(response => {
    deptOptions.value = response.rows || response.data
  })
}

function getTeacherList() {
  // 获取全部教师数据供计算属性过滤
  listTeacher({ pageNum: 1, pageSize: 10000 }).then(response => {
    teacherOptions.value = response.rows
  })
}

function getCourseList() {
  // 获取全部课程数据供计算属性过滤
  listCourse({ pageNum: 1, pageSize: 10000 }).then(response => {
    courseOptions.value = response.rows
  })
}

function getList() {
  loading.value = true
  listResponsibility(queryParams.value).then(response => {
    responsibilityList.value = response.rows
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
    deptId: null,
    staffId: null,
    courseId: null,
    oldCourseId: null 
  }
  proxy.resetForm("responsibilityRef")
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
  selectRows.value = selection
  ids.value = selection.map(item => item.staffId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加教师任课情况"
}

function handleUpdate(row) {
  reset()
  const target = row.staffId ? row : selectRows.value[0]
  getResponsibility(target.staffId, target.courseId).then(response => {
    form.value = response.data
    // 关键：保存旧课程ID供后端删除用
    form.value.oldCourseId = response.data.courseId
    
    // 关键回显逻辑：如果后端没带出 deptId，我们根据 staffId 补全
    if (!form.value.deptId) {
       const teacher = teacherOptions.value.find(t => t.staffId === response.data.staffId)
       if(teacher) form.value.deptId = teacher.deptId
    }
    
    open.value = true
    title.value = "修改教师任课情况"
  })
}

function submitForm() {
  proxy.$refs["responsibilityRef"].validate(valid => {
    if (valid) {
      // 提交前的最后一道防线：学院匹配检查
      const teacher = teacherOptions.value.find(t => t.staffId === form.value.staffId)
      const course = courseOptions.value.find(c => c.courseId === form.value.courseId)
      
      if (teacher && course && teacher.deptId !== course.deptId) {
        proxy.$modal.msgError("操作失败：所选教师与课程所属学院不匹配！")
        return
      }

      if (title.value === "修改教师任课情况") {
        updateResponsibility(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addResponsibility(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

import { ElMessage } from 'element-plus'

/** 删除按钮操作 */
function handleDelete(row) {
  // 1. 获取目标数据
  const target = row.staffId ? row : selectRows.value[0];
  const teacherDisplayName = target.teacherName || target.staffId;
  const courseDisplayName = target.courseName || target.courseId;

  // 2. 确认弹窗
  proxy.$modal.confirm(`确认删除教师 "${teacherDisplayName}" 负责的课程 "${courseDisplayName}" 记录吗？`).then(function() {
    return delResponsibility(target.staffId, target.courseId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((err) => {
    // 如果是用户点击取消，不处理
    if (err === 'cancel') return;

    // --- 核心美化逻辑 ---
    // 1. 强行抹除全局拦截器弹出的长串 SQL 错误
    ElMessage.closeAll();

    // 2. 弹出美化后的业务提示
    setTimeout(() => {
      proxy.$modal.msgError(
        `删除失败：该任课关系已关联具体的教学班级，请先取消该教师的相关排课！`
      );
    }, 50);
  });
}

// 初始化加载基础数据
getDeptList()
getTeacherList()
getCourseList()
getList()
</script>