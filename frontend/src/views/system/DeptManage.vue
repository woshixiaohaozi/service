<template>
  <div class="page-container">
    <el-card>
      <div class="page-toolbar">
        <el-button type="success" @click="openAdd(0)">新增顶级部门</el-button>
      </div>
      <el-table :data="tree" v-loading="loading" row-key="id" border
        :tree-props="{ children: 'children' }" default-expand-all>
        <el-table-column prop="deptName" label="部门名称" width="220" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="leaderId" label="负责人ID" width="100" />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openAdd(row.id)">新增下级</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="部门名称"><el-input v-model="form.deptName" /></el-form-item>
        <el-form-item label="负责人ID"><el-input-number v-model="form.leaderId" :min="0" /></el-form-item>
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
import { deptApi } from '@/api/dept'
import type { SysDept } from '@/types/api'

const loading = ref(false)
const tree = ref<SysDept[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const form = reactive<SysDept>({ deptName: '', parentId: 0, status: 1, leaderId: 0, sort: 0 })

async function loadData() { loading.value = true; try { tree.value = await deptApi.tree() } finally { loading.value = false } }
function openAdd(parentId: number) { Object.assign(form, { id: undefined, parentId, deptName: '', leaderId: 0, sort: 0, status: 1 }); dialogTitle.value = '新增部门'; dialogVisible.value = true }
function openEdit(row: SysDept) { Object.assign(form, row); dialogTitle.value = '编辑部门'; dialogVisible.value = true }
async function submitForm() { await deptApi.save({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
async function handleDelete(row: SysDept) { await ElMessageBox.confirm('确认删除部门 ' + row.deptName + '？', '提示', { type: 'warning' }); await deptApi.remove(row.id!); ElMessage.success('删除成功'); loadData() }
onMounted(loadData)
</script>
