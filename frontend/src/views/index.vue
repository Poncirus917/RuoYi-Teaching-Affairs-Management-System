<template>
  <div class="app-container home">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-card shadow="never" class="welcome-card">
          <div class="welcome-content">
            <span class="welcome-text">你好，{{ userNickname }}！ 欢迎回来。</span>
            <el-tag type="success" effect="plain">{{ roleName }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <AdminDashboard v-if="roles.includes('administer') || roles.includes('admin')" />

    <div v-else-if="roles.includes('teacher')">
      <TeacherDashboard />
    </div>

    <div v-else-if="roles.includes('student')">
      <StudentDashboard />
    </div>

    <div v-else>
      <el-card>
        <p>欢迎登录系统，请联系管理员分配角色。</p>
      </el-card>
    </div>
  </div>
</template>

<script setup name="Index">
import { computed } from 'vue';
import AdminDashboard from './dashboard/AdminDashboard'; 
import TeacherDashboard from './dashboard/TeacherDashboard';
import StudentDashboard from './dashboard/StudentDashboard'; // 1. 导入学生端组件
import useUserStore from '@/store/modules/user';

const userStore = useUserStore();

// 获取角色列表
const roles = computed(() => userStore.roles);

// 获取用户昵称
const userNickname = computed(() => userStore.nickname || userStore.name);

// 处理显示的身份名称
const roleName = computed(() => {
  if (roles.value.includes('administer') || roles.value.includes('admin')) return '系统管理员';
  if (roles.value.includes('teacher')) return '任课教师';
  if (roles.value.includes('student')) return '在校学生';
  return '系统用户';
});
</script>

<style scoped>
.home {
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
  padding: 20px;
}
.welcome-card {
  border: none;
  background: linear-gradient(to right, #ffffff, #f9fbff);
}
.welcome-content {
  display: flex;
  align-items: center;
  gap: 15px;
}
.welcome-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
</style>