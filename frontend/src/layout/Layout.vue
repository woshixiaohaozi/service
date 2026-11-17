<template>
  <el-container class="layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <span v-if="!isCollapse">Base Framework</span>
        <span v-else>BF</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item v-for="item in menuRoutes" :key="item.path" :index="'/' + item.path">
          <el-icon><component :is="iconMap[item.icon || 'House']" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <el-icon class="fold-btn" @click="isCollapse = !isCollapse">
          <Expand v-if="isCollapse" />
          <Fold v-else />
        </el-icon>
        <div class="header-right">
          <span class="user-name">{{ auth.userInfo?.realName || auth.userInfo?.username || '管理员' }}</span>
          <el-dropdown @command="onCommand">
            <el-avatar :size="32" style="background:#409eff">管</el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { House, User, UserFilled, Menu, OfficeBuilding, Expand, Fold } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const iconMap: Record<string, unknown> = { House, User, UserFilled, Menu, OfficeBuilding }

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const isCollapse = ref(false)

type MenuItem = { path: string; title?: string; icon?: string; hidden?: boolean }

// 侧边菜单：从子路由的 meta 中提取 title/icon（之前直接读路由根属性导致空标签）
const menuRoutes = computed<MenuItem[]>(() => {
  const layout = router.options.routes.find(r => r.name === 'Layout')
  return ((layout?.children || []) as any[])
    .filter(r => !r.meta?.hidden)
    .map(r => ({
      path: r.path,
      title: r.meta?.title as string | undefined,
      icon: r.meta?.icon as string | undefined,
      hidden: r.meta?.hidden as boolean | undefined
    }))
})

function onCommand(cmd: string) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' }).then(() => {
      auth.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #001529; transition: width 0.3s; overflow: hidden; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; border-bottom: 1px solid #1f2d3d; }
.el-menu { border-right: none; }
.header { background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.fold-btn { font-size: 20px; cursor: pointer; }
.header-right { display: flex; align-items: center; gap: 12px; }
.user-name { color: #606266; }
.main { padding: 0; overflow: auto; }
.fade-enter-active, .fade-leave-active { transition: opacity .2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
