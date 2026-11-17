<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card>
          <div class="stat-card">
            <div>
              <div class="stat-title">{{ card.title }}</div>
              <div class="stat-value">{{ card.value }}</div>
            </div>
            <el-icon :size="40" :color="card.color"><component :is="card.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:16px">
      <template #header><span>系统说明</span></template>
      <p>本后台基于 Vue 3 + Vite + Element Plus 构建，与后端 Spring Boot 框架对接。</p>
      <p>所有接口统一使用 POST，JWT 通过请求头传递；菜单与按钮权限由后端权限体系控制。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const cards = [
  { title: '用户总数', value: '1', icon: 'User', color: '#409eff' },
  { title: '角色数量', value: '2', icon: 'UserFilled', color: '#67c23a' },
  { title: '菜单权限', value: '6', icon: 'Menu', color: '#e6a23c' },
  { title: '部门数量', value: '3', icon: 'OfficeBuilding', color: '#f56c6c' }
]
</script>

<style scoped>
.stat-card { display: flex; align-items: center; justify-content: space-between; }
.stat-title { color: #909399; font-size: 13px; }
.stat-value { font-size: 28px; font-weight: bold; margin-top: 6px; }
</style>
