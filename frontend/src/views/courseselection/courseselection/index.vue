<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <template v-if="!isStudent">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="queryParams.studentId" placeholder="请输入学号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
      </template>
      
      <el-form-item label="教学班号" prop="classId">
        <el-input v-model="queryParams.classId" placeholder="请输入班号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程名" prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="教师姓名" prop="staffName">
        <el-input v-model="queryParams.staffName" placeholder="请输入教师姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="学期" prop="semester">
        <el-input v-model="queryParams.semester" placeholder="请输入学期" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程号" prop="courseId">
        <el-input v-model="queryParams.courseId" placeholder="请输入课程号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['courseselection:courseselection:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['courseselection:courseselection:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['courseselection:courseselection:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['courseselection:courseselection:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="courseselectionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      
      <el-table-column label="学生信息" align="center" min-width="120">
        <template #default="scope">
          <div style="font-weight: bold">{{ scope.row.studentName || '未知' }}</div>
          <div style="font-size: 12px; color: #909399">{{ scope.row.studentId }}</div>
        </template>
      </el-table-column>

      <el-table-column label="课程信息" align="center" min-width="150">
        <template #default="scope">
          <div style="font-weight: bold">{{ scope.row.courseName || '未知课程' }}</div>
          <div style="font-size: 12px; color: #909399">班级: {{ scope.row.classId }}</div>
        </template>
      </el-table-column>

      <el-table-column label="任课教师" align="center">
        <template #default="scope">
          <div>{{ scope.row.staffName || '未分配' }}</div>
          <div style="font-size: 12px; color: #909399">{{ scope.row.staffId }}</div>
        </template>
      </el-table-column>

      <el-table-column label="上课时间" align="center" min-width="120">
        <template #default="scope">
          <el-tag effect="plain" type="info">
            {{ formatCourseTime(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="学期" align="center" prop="semester" />
      <el-table-column label="平时成绩" align="center" prop="normalScore" />
      <el-table-column label="期末成绩" align="center" prop="testScore" />
      
      <el-table-column label="总成绩" align="center" prop="totalScore">
        <template #default="scope">
          <span :style="{ color: scope.row.totalScore < 60 ? 'red' : 'inherit', fontWeight: 'bold' }">
            {{ scope.row.totalScore }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="绩点" align="center" prop="gpa">
        <template #default="scope">
          <el-tag :type="Number(scope.row.gpa) === 0 ? 'danger' : 'success'">
            {{ scope.row.gpa !== null ? Number(scope.row.gpa).toFixed(1) : '0.0' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['courseselection:courseselection:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['courseselection:courseselection:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="courseselectionRef" :model="form" :rules="rules" label-width="100px">
        
        <el-form-item label="当前学号" v-if="isStudent && !isEdit">
           <span style="font-weight: bold; color: #409EFF">{{ userStore.name }}</span>
           <span style="margin-left: 10px; color: #67C23A">(当前本人账号)</span>
        </el-form-item>

        <el-form-item label="学号" prop="studentId" v-if="!isStudent || isEdit">
          <el-input v-model="form.studentId" placeholder="请输入学号" :disabled="isEdit" @blur="fetchStudentName" />
          <div v-if="extNames.studentName" style="color: #67C23A; font-size: 12px; margin-top: 5px;">
            <i class="el-icon-user"></i> 学生姓名：{{ extNames.studentName }}
          </div>
        </el-form-item>

        <el-form-item label="课程号" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程号" :disabled="isEdit" @blur="getClassesByCourse(form.courseId)" />
        </el-form-item>

        <el-form-item label="教学班号" prop="classId">
          <el-select 
            v-model="form.classId" 
            placeholder="请选择教学班" 
            :disabled="isEdit" 
            style="width: 100%"
            @change="handleClassChange"
          >
            <el-option
              v-for="item in classOptions"
              :key="item.classId"
              :label="item.classId"
              :value="item.classId"
            />
          </el-select>

          <div v-if="extNames.courseName" style="color: #409EFF; font-size: 12px; margin-top: 5px; line-height: 1.5;">
            课程：{{ extNames.courseName }}<br/>
            教师：{{ extNames.staffName }} ({{ form.staffId }})<br/>
            <span style="color: #E6A23C;">上课时间：{{ formatCourseTime(form) }}</span><br/>
            <span :style="{ color: extNames.selectedNum >= extNames.capacity ? 'red' : '#67C23A', fontWeight: 'bold' }">
                名额：{{ extNames.selectedNum }} / {{ extNames.capacity }} 
                {{ extNames.selectedNum >= extNames.capacity ? '(已满)' : '(可选)' }}
            </span>
          </div>
        </el-form-item>

        <el-form-item label="学期" prop="semester">
          <el-input v-model="form.semester" placeholder="自动带入" disabled />
        </el-form-item>
        
        <template v-if="!isStudent">
          <el-form-item label="平时成绩" prop="normalScore">
            <el-input v-model="form.normalScore" placeholder="请输入平时成绩">
              <template #append>{{ scoreConfig.normalRate }}%</template>
            </el-input>
          </el-form-item>
          <el-form-item label="期末成绩" prop="testScore">
            <el-input v-model="form.testScore" placeholder="请输入期末成绩">
              <template #append>{{ scoreConfig.testRate }}%</template>
            </el-input>
          </el-form-item>
          <el-form-item label="总成绩" prop="totalScore">
            <el-input v-model="form.totalScore" placeholder="自动计算" disabled />
          </el-form-item>
        </template>
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

<script setup name="Courseselection">
import { listCourseselection, getCourseselection, delCourseselection, addCourseselection, updateCourseselection } from "@/api/courseselection/courseselection"
import { getCourse } from "@/api/course/course" 
import { getCourseclass, listCourseclass } from "@/api/courseclass/courseclass"
import { getStudent } from "@/api/student/student"
import { ref, reactive, toRefs, watch, computed, getCurrentInstance } from 'vue'
import useUserStore from "@/store/modules/user" // 导入用户仓库

const { proxy } = getCurrentInstance()
const userStore = useUserStore() // 初始化用户仓库

const isStudent = computed(() => userStore.roles.includes('student'))
const isTeacher = computed(() => userStore.roles.includes('teacher'))

const courseselectionList = ref([])
const classOptions = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const selectedRows = ref([]) 
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isEdit = ref(false)

const scoreConfig = reactive({ normalRate: 0, testRate: 0 })
const extNames = reactive({ 
  studentName: "", 
  courseName: "", 
  staffName: "",
  selectedNum: 0, 
  capacity: 0 
})

const data = reactive({
  form: {},
  queryParams: { 
    pageNum: 1, 
    pageSize: 10, 
    studentId: null, 
    studentName: null, 
    classId: null, 
    courseName: null,  
    staffName: null,   
    semester: null,    
    courseId: null,    
    staffId: null      
  },
  rules: {
    studentId: [{ required: true, message: "学号不能为空", trigger: "blur" }],
    courseId: [{ required: true, message: "课程号不能为空", trigger: "blur" }],
    classId: [{ required: true, message: "教学班号不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询班级列表 */
function getClassesByCourse(courseId) {
  if (!courseId) {
    classOptions.value = [];
    return;
  }
  listCourseclass({ courseId: courseId }).then(res => {
    classOptions.value = res.rows;
    if (res.rows.length === 0) {
       proxy.$modal.msgError("该课程号下暂无开课班级");
    }
  });
}

/** 班级切换处理 */
function handleClassChange(classId) {
  if (!classId) return;
  getCourseclass(classId).then(res => {
    form.value.staffId = res.data.staffId;
    form.value.semester = res.data.semester;
    form.value.dayOfWeek = res.data.dayOfWeek;
    form.value.startPeriod = res.data.startPeriod;
    form.value.endPeriod = res.data.endPeriod;
    
    extNames.courseName = res.data.courseName;
    extNames.staffName = res.data.staffName;
    extNames.selectedNum = res.data.selectedNum || 0;
    extNames.capacity = res.data.capacity || 0;

    fetchCourseWeight(form.value.courseId);
  }).catch(() => {
    proxy.$modal.msgError("加载班级详情失败");
  });
}

function fetchStudentName() {
  if (!form.value.studentId) return;
  getStudent(form.value.studentId).then(res => {
    extNames.studentName = res.data.studentName || res.data.name;
  }).catch(() => { extNames.studentName = "未找到学生"; });
}

function fetchCourseWeight(courseId) {
  getCourse(courseId).then(res => {
    scoreConfig.normalRate = (res.data.usualWeight || 0) * 100;
    scoreConfig.testRate = (res.data.finalWeight || 0) * 100;
    calculateTotal();
  })
}

function calculateTotal() {
  if (isStudent.value) return; // 学生无需计算
  const normal = parseFloat(form.value.normalScore) || 0;
  const test = parseFloat(form.value.testScore) || 0;
  if (scoreConfig.normalRate > 0 || scoreConfig.testRate > 0) {
    const total = (normal * scoreConfig.normalRate / 100) + (test * scoreConfig.testRate / 100);
    form.value.totalScore = Math.round(total);
  } else {
    form.value.totalScore = 0;
  }
}

function formatCourseTime(row) {
  const day = row.dayOfWeek;
  const start = row.startPeriod;
  const end = row.endPeriod;
  if (!day || !start || !end) return "时间未定";
  const weeks = ["", "周一", "周二", "周三", "周四", "周五", "周六", "周日"];
  const weekName = weeks[day] || `周${day}`;
  return `${weekName} 第${start}-${end}节`;
}

watch([() => form.value.normalScore, () => form.value.testScore], () => {
  calculateTotal();
})

function getList() {
  loading.value = true
  listCourseselection(queryParams.value).then(res => {
    courseselectionList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  })
}

function cancel() { open.value = false; reset(); }

function reset() {
  form.value = { 
    studentId: null, 
    classId: null, 
    semester: null, 
    courseId: null, 
    staffId: null, 
    normalScore: null, 
    testScore: null, 
    totalScore: null,
    gpa: null 
  };
  classOptions.value = [];
  extNames.studentName = ""; extNames.courseName = ""; extNames.staffName = "";
  extNames.selectedNum = 0; extNames.capacity = 0;
  scoreConfig.normalRate = 0; scoreConfig.testRate = 0;
  if (proxy.$refs["courseselectionRef"]) {
    proxy.$refs["courseselectionRef"].resetFields();
  }
}

function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery(); }
function handleSelectionChange(selection) {
  selectedRows.value = selection;
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 修改点4：handleAdd 方法增加自动填充 */
function handleAdd() { 
  reset(); 
  isEdit.value = false; 
  // 如果是学生，自动填充学号（取用户名）
  if (isStudent.value) {
    form.value.studentId = userStore.name; 
    fetchStudentName();
  }
  open.value = true; 
  title.value = "添加选课记录"; 
}

function handleUpdate(row) {
  reset(); 
  isEdit.value = true;
  // 确保同时获取两个主键
  const sId = row.studentId || selectedRows.value[0].studentId;
  const cId = row.classId || selectedRows.value[0].classId;
  
  // 必须确保传入两个参数，且顺序与 API 定义一致
  getCourseselection(sId, cId).then(res => {
    form.value = res.data;
    fetchStudentName();
    // 修改模式下，加载下拉列表但禁用切换
    getClassesByCourse(res.data.courseId);
    handleClassChange(res.data.classId);
    open.value = true;
    title.value = isStudent.value ? "查看选课详情" : "修改成绩记录";
  }).catch(err => {
    proxy.$modal.msgError("获取选课信息失败，请检查网络或权限");
  });
}

function submitForm() {
  proxy.$refs["courseselectionRef"].validate(valid => {
    if (valid) {
      if (!isEdit.value && extNames.capacity > 0 && extNames.selectedNum >= extNames.capacity) {
        proxy.$modal.alertError("选课失败：该教学班名额已满。");
        return;
      }
      const action = isEdit.value ? updateCourseselection(form.value) : addCourseselection(form.value);
      loading.value = true;
      action.then(() => {
        proxy.$modal.msgSuccess(isEdit.value ? "修改成功" : "新增成功");
        open.value = false;
        getList();
      }).catch((err) => {
        // 后端抛出的分数限制等错误会在这里被捕获
        proxy.$modal.msgError(err.msg || "操作异常");
      }).finally(() => {
        loading.value = false;
      });
    }
  });
}

function handleDelete(row) {
  const rows = row.studentId ? [row] : selectedRows.value;
  proxy.$modal.confirm('是否确认删除选课记录？').then(() => {
    const promises = rows.map(item => delCourseselection(item.studentId, item.classId));
    return Promise.all(promises);
  }).then(() => { getList(); proxy.$modal.msgSuccess("删除成功"); }).catch(() => {});
}

getList();
/** 导出按钮操作 */
function handleExport() {
  proxy.$download('courseselection/courseselection/export', {
    ...queryParams.value
  }, `courseselection_${new Date().getTime()}.xlsx`)
}
</script>