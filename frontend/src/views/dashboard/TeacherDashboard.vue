<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24" v-for="(item, index) in statList" :key="index" style="margin-bottom: 20px;">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span style="font-weight: bold; font-size: 16px;">
                <el-icon><Monitor /></el-icon> 教学班级：{{ item.classId }} 成绩分布统计
              </span>
            </div>
          </template>
          
          <div :id="'chart-' + index" style="height: 400px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-empty v-if="statList.length === 0" description="暂无任课班级数据" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getTeacherClassStats } from '@/api/teacher/teacher'; // 路径根据你的实际情况修改

const statList = ref([]);

onMounted(() => {
  fetchData();
});

// 获取数据
async function fetchData() {
  const response = await getTeacherClassStats();
  statList.value = response.data;
  
  // 等待 DOM 渲染完成后初始化图表
  await nextTick();
  initCharts();
}

// 初始化所有图表
function initCharts() {
  statList.value.forEach((data, index) => {
    const chartDom = document.getElementById('chart-' + index);
    if (!chartDom) return;
    
    const myChart = echarts.init(chartDom);
    
    const option = {
      title: {
        text: '成绩与GPA区间分布',
        left: 'center',
        textStyle: { fontSize: 14 }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['学生人数'],
        bottom: 10
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: [
          '平时<60', '平时60-85', '平时85+', 
          '期末<60', '期末60-70', '期末70-85', '期末85+',
          'GPA 0.0', 'GPA 1.0-2.3', 'GPA 2.7-3.3', 'GPA 3.7-4.0'
        ],
        axisLabel: {
          interval: 0,
          rotate: 30
        }
      },
      yAxis: {
        type: 'value',
        name: '人数',
        minInterval: 1 // 保证坐标轴显示整数
      },
      series: [
        {
          name: '学生人数',
          type: 'bar',
          barWidth: '60%',
          data: [
            data.normalLow, data.normalMid, data.normalHigh,
            data.testFail, data.testPass, data.testGood, data.testExcellent,
            data.gpa0, data.gpa12, data.gpa23, data.gpa34
          ],
          itemStyle: {
            // 根据不同的维度给出不同的颜色
            color: (params) => {
              if (params.dataIndex < 3) return '#5470c6'; // 平时分-蓝色
              if (params.dataIndex < 7) return '#91cc75'; // 期末分-绿色
              return '#fac858'; // GPA-黄色
            }
          },
          label: {
            show: true,
            position: 'top'
          }
        }
      ]
    };
    
    myChart.setOption(option);
    
    // 自适应窗口大小
    window.addEventListener('resize', () => myChart.resize());
  });
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>