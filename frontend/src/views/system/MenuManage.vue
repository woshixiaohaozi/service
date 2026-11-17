<template>
  <div class="page-container">
    <el-card>
      <div class="page-toolbar">
        <el-button type="success" @click="openAdd(0)">新增顶级菜单</el-button>
        <el-button @click="expandAll">展开全部</el-button>
        <el-button @click="collapseAll">收起全部</el-button>
      </div>
      <el-table :data="tree" v-loading="loading" row-key="id" border
        :tree-props="{ children: 'children' }" default-expand-all ref="tableRef">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.menuType === 'M' ? '' : row.menuType === 'C' ? 'success' : 'warning'">
              {{ row.menuType === 'M' ? '目录' : row.menuType === 'C' ? '菜单' : '权限' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="path" label="路由地址" />
        <el-table-column prop="permission" label="权限标识" />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.menuType !== 'F'" link type="primary" @click="openAdd(row.id)">新增</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="菜单名称"><el-input v-model="form.menuName" /></el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.menuType">
            <el-radio value="M">目录</el-radio><el-radio value="C">菜单</el-radio><el-radio value="F">权限</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由地址"><el-input v-model="form.path" /></el-form-item>
        <el-form-item label="组件路径"><el-input v-model="form.component" /></el-form-item>
        <el-form-item label="权限标识"><el-input v-model="form.permission" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi } from '@/api/menu'
import type { SysMenu } from '@/types/api'

const loading = ref(false)
const tree = ref<SysMenu[]>([])
const tableRef = ref()
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const form = reactive<SysMenu>({ menuName: '', parentId: 0, menuType: 'C', status: 1, sort: 0 })

async function loadData() { loading.value = true; try { tree.value = await menuApi.tree() } finally { loading.value = false } }
function openAdd(parentId: number) { Object.assign(form, { id: undefined, parentId, menuName: '', menuType: 'C', path: '', component: '', permission: '', icon: '', sort: 0, status: 1 }); dialogTitle.value = '新增菜单'; dialogVisible.value = true }
function openEdit(row: SysMenu) { Object.assign(form, row); dialogTitle.value = '编辑菜单'; dialogVisible.value = true }
async function submitForm() { await menuApi.save({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
async function handleDelete(row: SysMenu) { await ElMessageBox.confirm('确认删除菜单 ' + row.menuName + '？', '提示', { type: 'warning' }); await menuApi.remove(row.id!); ElMessage.success('删除成功'); loadData() }
function expandAll() { tree.value.forEach(row => tableRef.value?.toggleRowExpansion(row, true)) }
function collapseAll() { tree.value.forEach(row => tableRef.value?.toggleRowExpansion(row, false)) }
onMounted(loadData)
</script>
