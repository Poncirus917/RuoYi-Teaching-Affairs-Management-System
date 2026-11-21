<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="教学班号" prop="classId">
        <el-input v-model="queryParams.classId" placeholder="请输入教学班号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      
      <el-form-item label="所属院系" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择院系" clearable @change="handleQuery" style="width: 180px">
          <el-option 
            v-for="item in deptOptions" 
            :key="item.deptId" 
            :label="item.deptName" 
            :value="item.deptId" 
          />
        </el-select>
      </el-form-item>

      <el-form-item label="学期" prop="semester">
        <el-select v-model="queryParams.semester" placeholder="请选择学期" clearable @change="handleQuery" style="width: 180px">
          <el-option label="2025-2026秋季学期" value="2025-2026秋季学期" />
          <el-option label="2025-2026春季学期" value="2025-2026春季学期" />
        </el-select>
      </el-form-item>

      <el-form-item label="教师姓名" prop="staffName">
        <el-input v-model="queryParams.staffName" placeholder="请输入教师姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程名称" prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['courseclass:courseclass:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['courseclass:courseclass:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['courseclass:courseclass:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['courseclass:courseclass:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="courseclassList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="教学班号" align="center" prop="classId" />
      
      <el-table-column label="任教教师" align="center">
        <template #default="scope">
          <div style="line-height: 1.4;">
            <div>{{ scope.row.staffName }}</div>
            <div style="color: #909399; font-size: 12px;">({{ scope.row.staffId }})</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="任教课程" align="center">
        <template #default="scope">
          <div style="line-height: 1.4;">
            <div>{{ scope.row.courseName }}</div>
            <div style="color: #909399; font-size: 12px;">({{ scope.row.courseId }})</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="学期" align="center" prop="semester" width="160" />
      <el-table-column label="地点" align="center" prop="classroom" />
      
      <el-table-column label="选课名额" align="center" width="150">
        <template #default="scope">
          <div style="padding: 0 10px;">
            <el-progress 
              :percentage="Math.min(100, Math.round((scope.row.selectedNum / scope.row.capacity) * 100))" 
              :status="scope.row.selectedNum >= scope.row.capacity ? 'exception' : ''"
              :stroke-width="12"
              :indeterminate="scope.row.selectedNum >= scope.row.capacity"
              :duration="5"
            />
            <div style="font-size: 12px; margin-top: 5px;">
              <span :style="{ color: scope.row.selectedNum >= scope.row.capacity ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
                {{ scope.row.selectedNum || 0 }}
              </span>
              <span style="color: #909399;"> / {{ scope.row.capacity }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column label="上课星期" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.dayOfWeek >= 6 ? 'warning' : ''">
            {{ ['','周一','周二','周三','周四','周五','周六','周日'][scope.row.dayOfWeek] }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="节次" align="center" width="120">
        <template #default="scope">
          <span style="font-size: 12px;">第 {{ scope.row.startPeriod }}-{{ scope.row.endPeriod }} 节</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['courseclass:courseclass:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['courseclass:courseclass:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="courseclassRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教学班号" prop="classId">
          <el-input v-model="form.classId" placeholder="请输入8位教学班号" maxlength="8" show-word-limit :disabled="title === '修改开课情况'" />
        </el-form-item>
        
        <el-form-item label="教师工号" prop="staffId">
          <el-input 
            v-model="form.staffId" 
            placeholder="请输入教师工号" 
            :disabled="isStaffIdDisabled" 
          />
        </el-form-item>

        <el-form-item label="课程号" prop="courseId">
          <el-select 
            v-model="form.courseId" 
            placeholder="请选择课程" 
            style="width: 100%"
            :disabled="responsibleCourses.length === 0 || form.selectedNum > 0"
          >
            <el-option
              v-for="item in responsibleCourses"
              :key="item.courseId"
              :label="`${item.courseName || item.courseId} (${item.courseId})`"
              :value="item.courseId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="学期" prop="semester">
          <el-select v-model="form.semester" placeholder="请选择学期" style="width: 100%" :disabled="form.selectedNum > 0">
            <el-option label="2025-2026秋季学期" value="2025-2026秋季学期" />
            <el-option label="2025-2026春季学期" value="2025-2026春季学期" />
          </el-select>
        </el-form-item>

        <el-form-item label="上课地点" prop="classroom">
          <el-input v-model="form.classroom" placeholder="请输入上课地点" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级容量" prop="capacity">
              <el-input-number v-model="form.capacity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已选人数" prop="selectedNum">
              <el-tooltip content="该人数由系统自动统计，不可手动修改" placement="right">
                 <el-input-number v-model="form.selectedNum" :min="0" disabled style="width: 100%" />
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="center">排课时间信息</el-divider>

        <el-form-item label="上课星期" prop="dayOfWeek">
          <el-select v-model="form.dayOfWeek" placeholder="请选择星期" style="width: 100%" :disabled="form.selectedNum > 0">
            <el-option label="周一" :value="1" /><el-option label="周二" :value="2" />
            <el-option label="周三" :value="3" /><el-option label="周四" :value="4" />
            <el-option label="周五" :value="5" /><el-option label="周六" :value="6" />
            <el-option label="周日" :value="7" />
          </el-select>
        </el-form-item>

        <el-form-item label="起始节次" prop="startPeriod">
          <el-input-number 
            v-model="form.startPeriod" 
            :min="1" 
            :max="12" 
            style="width: 100%" 
            :disabled="form.selectedNum > 0"
            @change="() => proxy.$refs.courseclassRef.validateField('endPeriod')" 
          />
        </el-form-item>

        <el-form-item label="结束节次" prop="endPeriod">
          <el-input-number v-model="form.endPeriod" :min="1" :max="12" style="width: 100%" :disabled="form.selectedNum > 0" />
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

<script setup name="Courseclass">
import { listCourseclass, getCourseclass, delCourseclass, addCourseclass, updateCourseclass } from "@/api/courseclass/courseclass"
import { listResponsibility } from "@/api/responsibility/responsibility"
import { listDepartment } from "@/api/department/department"
import { ref, reactive, toRefs, getCurrentInstance, computed, watch } from 'vue'
import useUserStore from "@/store/modules/user"

const { proxy } = getCurrentInstance()
const userStore = useUserStore()

const courseclassList = ref([])
const deptOptions = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const responsibleCourses = ref([]) 

// 修改这两个计算属性
const isAdminRole = computed(() => {
  return userStore.roles.includes('admin') || userStore.roles.includes('administer');
});

// 重新定义：仅在【新增】且是【老师】时，才判定为需要自动处理工号
const isTeacherAdd = computed(() => {
  return userStore.roles.includes('teacher') && !isAdminRole.value && title.value === '添加教学班';
});

// 重新定义：什么时候禁用工号输入框
const isStaffIdDisabled = computed(() => {
  // 1. 如果是修改界面，统一禁止修改工号（防止破坏排课逻辑）
  if (title.value === "修改开课情况") return true;
  // 2. 如果是新增界面，只有老师角色才禁用（因为老师只能给自己排课）
  return userStore.roles.includes('teacher') && !isAdminRole.value;
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    classId: null,
    staffId: null,
    deptId: null,
    semester: null,
    staffName: null,  
    courseName: null  
  },
  rules: {
    classId: [
      { required: true, message: "教学班号不能为空", trigger: "blur" },
      { min: 8, max: 8, message: "必须为8位", trigger: "blur" }
    ],
    courseId: [{ required: true, message: "请选择负责的课程", trigger: "change" }],
    staffId: [{ required: true, message: "教师工号不能为空", trigger: "blur" }],
    semester: [{ required: true, message: "请选择学期", trigger: "change" }],
    dayOfWeek: [{ required: true, message: "请选择星期", trigger: "change" }],
    startPeriod: [{ required: true, message: "起始节次不能为空", trigger: "blur" }],
    endPeriod: [
      { required: true, message: "结束节次不能为空", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value < data.form.startPeriod) {
            callback(new Error('结束节次不能小于起始节次'));
          } else {
            callback();
          }
        },
        trigger: 'change'
      }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

watch(() => form.value.staffId, (newVal) => {
  // 1. 如果工号被清空，重置课程列表
  if (!newVal) {
    responsibleCourses.value = [];
    return;
  }

  // 2. 只有当工号长度达到 8 位时，才执行后端查询
  if (newVal.length === 8) {
    getResponsibleCourses();
  } else {
    // 长度不足时，保持列表为空，但不弹窗
    responsibleCourses.value = [];
  }
});

function getDeptList() {
  listDepartment().then(response => {
    deptOptions.value = response.rows || response.data;
  });
}

/** 获取该教师负责的课程 */
function getResponsibleCourses() {
  if (!form.value.staffId) return;

  const params = { staffId: form.value.staffId };
  listResponsibility(params).then(res => {
    responsibleCourses.value = res.rows;
    
    if (res.rows.length === 0) {
      if (isAdminRole.value) {
        proxy.$modal.msgWarning("该工号暂无关联的责任课程，请先分配责任教师");
      }
    }

    // 如果当前选中的课程不在新查出来的列表里，清空它（仅限新增时）
    if (title.value !== "修改开课情况" && form.value.courseId) {
      if (!res.rows.find(i => i.courseId === form.value.courseId)) {
        form.value.courseId = null;
      }
    }
  });
}

function getList() {
  loading.value = true
  listCourseclass(queryParams.value).then(response => {
    courseclassList.value = response.rows
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
    classId: null, 
    courseId: null, 
    staffId: null, 
    semester: "2025-2026秋季学期",
    classroom: null, 
    capacity: 50, 
    selectedNum: 0,
    dayOfWeek: null, 
    startPeriod: 1, 
    endPeriod: 2
  }
  responsibleCourses.value = []
  if (proxy.$refs.courseclassRef) {
    proxy.resetForm("courseclassRef")
  }
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
  ids.value = selection.map(item => item.classId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 重置新增表单 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加教学班";
  
  // 修正：使用 userStore.roles 判断或 isAdminRole 判断
  const isTeacher = userStore.roles.includes('teacher') && !isAdminRole.value;
  
  if (isTeacher) {
    // 注意：若依默认工号通常存储在 userStore.name 或 userStore.username 中，请确认
    form.value.staffId = userStore.name; 
  }
}

function handleUpdate(row) {
  reset()
  const _classId = row.classId || ids.value
  getCourseclass(_classId).then(response => {
    form.value = response.data
    if (form.value.selectedNum == null) form.value.selectedNum = 0;
    
    // --- 关键修改点：主动触发查询课程列表 ---
    // 这样在打开弹窗时，下拉框才会有数据，不会显示“无数据”
    if (form.value.staffId) {
      getResponsibleCourses(); 
    }
    
    open.value = true
    title.value = "修改开课情况"
  })
}

function submitForm() {
  proxy.$refs["courseclassRef"].validate(valid => {
    if (valid) {
      if (title.value === "修改开课情况") {
        updateCourseclass(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        })
      } else {
        addCourseclass(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        })
      }
    }
  })
}

function handleDelete(row) {
  const _classIds = row.classId || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + _classIds + '"的数据项？').then(function() {
    return delCourseclass(_classIds)
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {})
}

function handleExport() {
  proxy.download('courseclass/courseclass/export', {
    ...queryParams.value
  }, `courseclass_${new Date().getTime()}.xlsx`)
}

getDeptList();
getList();
</script>