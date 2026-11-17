<template>
  <div class="page-container">
    <el-card>
      <div class="page-toolbar">
        <el-input v-model="query.roleName" placeholder="角色名称" clearable style="width:180px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增角色</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleKey" label="角色标识" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="openAssignMenus(row)">分配权限</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="page-pagination">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
          :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next"
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="角色标识"><el-input v-model="form.roleKey" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单权限弹窗：树形勾选 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="450px">
      <el-tree ref="treeRef" :data="menuTree" show-checkbox node-key="id"
        :props="{ label: 'menuName', children: 'children' }" default-expand-all />
      <template #footer>
        <el-button @click="menuDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitMenus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type ElTree } from 'element-plus'
import { roleApi } from '@/api/role'
import { menuApi } from '@/api/menu'
import type { SysRole, SysMenu } from '@/types/api'

const loading = ref(false)
const list = ref<SysRole[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, roleName: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const form = reactive<SysRole>({ roleName: '', roleKey: '', status: 1, sort: 0 })

async function loadData() {
  loading.value = true
  try { const res = await roleApi.page({ ...query }); list.value = res.records; total.value = res.total }
  finally { loading.value = false }
}
function openAdd() { Object.assign(form, { id: undefined, roleName: '', roleKey: '', sort: 0, status: 1, remark: '' }); dialogTitle.value = '新增角色'; dialogVisible.value = true }
function openEdit(row: SysRole) { Object.assign(form, row); dialogTitle.value = '编辑角色'; dialogVisible.value = true }
async function submitForm() { await roleApi.save({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
async function handleDelete(row: SysRole) {
  await ElMessageBox.confirm('确认删除角色 ' + row.roleName + '？', '提示', { type: 'warning' })
  await roleApi.remove(row.id!); ElMessage.success('删除成功'); loadData()
}

const menuDialogVisible = ref(false)
const menuTree = ref<SysMenu[]>([])
const treeRef = ref<InstanceType<typeof ElTree>>()
const currentRoleId = ref(0)
async function openAssignMenus(row: SysRole) {
  currentRoleId.value = row.id!
  menuTree.value = await menuApi.tree()
  menuDialogVisible.value = true
  const ids = await roleApi.menuIds(row.id!)
  await nextTick()
  treeRef.value?.setCheckedKeys(ids, false)
}
async function submitMenus() {
  const ids = treeRef.value?.getCheckedKeys(false) as number[] || []
  const halfIds = treeRef.value?.getHalfCheckedKeys() as number[] || []
  await roleApi.assignMenus(currentRoleId.value, [...ids, ...halfIds])
  ElMessage.success('权限分配成功'); menuDialogVisible.value = false
}

onMounted(loadData)
</script>
