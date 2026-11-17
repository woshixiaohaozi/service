<template>
  <div class="page-container">
    <el-card>
      <div class="page-toolbar">
        <el-input v-model="query.username" placeholder="用户名" clearable style="width:180px" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openAdd">新增用户</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="mobile" label="手机号" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="openAssignRoles(row)">分配角色</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="page-pagination">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
          :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.mobile" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-checkbox-group v-model="checkedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.id" :value="role.id" style="display:block">
          {{ role.roleName }} ({{ role.roleKey }})
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { userApi } from '@/api/user'
import { roleApi } from '@/api/role'
import { permissionApi } from '@/api/permission'
import type { SysUser, SysRole } from '@/types/api'

const loading = ref(false)
const list = ref<SysUser[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, username: '', status: undefined as number | undefined })

const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref<FormInstance>()
const form = reactive<SysUser>({ username: '', password: '', status: 1 })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await userApi.page({ ...query })
    list.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}
function resetQuery() { query.username = ''; query.status = undefined; query.pageNum = 1; loadData() }
function openAdd() { Object.assign(form, { id: undefined, username: '', password: '', realName: '', mobile: '', status: 1 }); dialogTitle.value = '新增用户'; dialogVisible.value = true }
function openEdit(row: SysUser) { Object.assign(form, row); dialogTitle.value = '编辑用户'; dialogVisible.value = true }
async function submitForm() {
  await formRef.value?.validate()
  await userApi.save({ ...form })
  ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
}
async function handleDelete(row: SysUser) {
  await ElMessageBox.confirm('确认删除用户 ' + row.username + '？', '提示', { type: 'warning' })
  await userApi.remove(row.id!)
  ElMessage.success('删除成功'); loadData()
}

// 启用 / 禁用切换
async function handleToggleStatus(row: SysUser) {
  const next = row.status === 1 ? 0 : 1
  const action = next === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm('确认' + action + '用户 ' + row.username + '？', '提示', { type: 'warning' })
  await userApi.changeStatus(row.id!, next)
  ElMessage.success(action + '成功'); loadData()
}

// 角色分配
const roleDialogVisible = ref(false)
const allRoles = ref<SysRole[]>([])
const checkedRoleIds = ref<number[]>([])
const currentUserId = ref(0)
async function openAssignRoles(row: SysUser) {
  currentUserId.value = row.id!
  allRoles.value = await roleApi.list()
  checkedRoleIds.value = await permissionApi.userRoles(row.id!)
  roleDialogVisible.value = true
}
async function submitRoles() {
  await permissionApi.assignRoles(currentUserId.value, checkedRoleIds.value)
  ElMessage.success('角色分配成功'); roleDialogVisible.value = false
}

onMounted(loadData)
</script>
