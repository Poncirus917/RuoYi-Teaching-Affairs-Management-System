<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statistics" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">{{ item.title }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card shadow="always">
          <div ref="studentPie" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always">
          <div ref="teacherPie" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always">
          <div ref="titlePie" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { getIndexData } from '@/api/index'; // 引入刚才定义的接口

// 1. 初始化统计数据 (初始为0)
const statistics = ref([
  { title: '全校教师总数', value: 0 },
  { title: '全校学生总数', value: 0 },
  { title: '全校课程总数', value: 0 },
  { title: '全校学院总数', value: 0 }
]);

const studentPie = ref(null);
const teacherPie = ref(null);
const titlePie = ref(null);

onMounted(() => {
  loadAllData();
});

async function loadAllData() {
  try {
    const res = await getIndexData();
    const data = res.data;

    // 更新卡片数字
    statistics.value[0].value = data.teacherCount;
    statistics.value[1].value = data.studentCount;
    statistics.value[2].value = data.courseCount;
    statistics.value[3].value = data.deptCount;

    // 渲染图表
    renderCharts(data);
  } catch (error) {
    console.error("加载首页数据失败", error);
  }
}

function renderCharts(data) {
  // 1. 学生分布饼图
  const sChart = echarts.init(studentPie.value);
  sChart.setOption({
    title: { text: '各学院学生人数', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: '50%', data: data.studentDeptData }]
  });

  // 2. 教师分布饼图
  const tChart = echarts.init(teacherPie.value);
  tChart.setOption({
    title: { text: '各学院教师人数', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: '50%', data: data.teacherDeptData }]
  });

  // 3. 职称分布饼图
  const titleChart = echarts.init(titlePie.value);
  titleChart.setOption({
    title: { text: '教师职称分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: ['40%', '70%'], data: data.teacherTitleData }]
  });
}
</script>

<style scoped>
.stat-card { text-align: center; background: #f8f9fa; }
.stat-title { font-size: 14px; color: #666; }
.stat-value { font-size: 28px; font-weight: bold; margin-top: 10px; color: #409EFF; }
</style>