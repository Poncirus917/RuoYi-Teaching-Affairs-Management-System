<template>
  <div class="student-dashboard"> <el-row :gutter="20" class="mb20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">总已选学分</div>
          <div class="stat-value">{{ totalStats.totalCredits || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card earned-card">
          <div class="stat-title">总已获得学分</div>
          <div class="stat-value">{{ totalStats.earnedCredits || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card gpa-card">
          <div class="stat-title">总平均绩点 (GPA)</div>
          <div class="stat-value">{{ totalStats.avgGpa || '0.00' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>各学期学分与绩点趋势</span>
        </div>
      </template>
      <div id="semesterChart" style="height: 400px; width: 100%;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getMyGpaStats } from '@/api/student/student';

const totalStats = ref({});
const semesterData = ref([]);

onMounted(() => {
  fetchData();
});

async function fetchData() {
  const res = await getMyGpaStats();
  const data = res.data;
  
  // 1. 拆分数据：最后一条是“个人总计”，其余是学期数据
  totalStats.value = data.find(item => item.sortOrder === 2) || {};
  semesterData.value = data.filter(item => item.sortOrder === 1);
  
  await nextTick();
  initChart();
}

function initChart() {
  const chartDom = document.getElementById('semesterChart');
  const myChart = echarts.init(chartDom);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['已选学分', '已获得学分', '平均绩点'] // 确保这里有三个项
    },
    xAxis: {
      type: 'category',
      data: semesterData.value.map(item => item.semesterName)
    },
    yAxis: [
      {
        type: 'value',
        name: '学分',
        position: 'left'
      },
      {
        type: 'value',
        name: '绩点',
        min: 0,
        max: 4,
        position: 'right'
      }
    ],
    series: [
      {
        name: '已选学分',
        type: 'bar',
        barWidth: '25%', // 调窄一点点
        data: semesterData.value.map(item => item.totalCredits),
        itemStyle: { color: '#909399' } // 灰色表示总修读
      },
      {
        name: '已获得学分',
        type: 'bar',
        barWidth: '25%',
        barGap: '10%', // 两个柱子之间的间距
        data: semesterData.value.map(item => item.earnedCredits),
        itemStyle: { color: '#409EFF' } // 蓝色表示已获得
      },
      {
        name: '平均绩点',
        type: 'line',
        yAxisIndex: 1,
        data: semesterData.value.map(item => item.avgGpa),
        itemStyle: { color: '#67C23A' },
        label: { show: true, position: 'top' }
      }
    ]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
}
</script>

<style scoped>
.mb20 { margin-bottom: 20px; }
.stat-card { text-align: center; padding: 10px 0; }
.stat-title { font-size: 14px; color: #909399; margin-bottom: 10px; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.earned-card .stat-value { color: #409EFF; } /* 新增：已得学分显示蓝色 */
.gpa-card .stat-value { color: #67C23A; }
</style>