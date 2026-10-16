<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdminNote, fetchAdminNotes, isUnavailableError } from '../api'
import { toChineseErrorMessage } from '../api/status'

const keyword = ref('')
const status = ref('')
const page = ref(1)
const size = 10
const total = ref(0)
const records = ref([])
const loading = ref(false)
const error = ref('')
const unavailable = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))
const statusText = { '草稿': '草稿', '待审核': '待审核', '已发布': '已发布', '已拒绝': '已拒绝' }

function displayStatus(value) {
  const key = String(value || '')
  return statusText[key] || value || '未知'
}

function statusClass(value) {
  const classes = { 草稿: 'draft', 待审核: 'pending', 已发布: 'published', 已拒绝: 'rejected' }
  return `status-${classes[String(value || '')] || 'unknown'}`
}

async function load(targetPage = 1) {
  loading.value = true
  error.value = ''
  unavailable.value = false
  try {
    const result = await fetchAdminNotes(keyword.value.trim(), status.value, targetPage, size)
    records.value = result.records
    total.value = result.total
    page.value = result.page
  } catch (reason) {
    unavailable.value = isUnavailableError(reason)
    error.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

async function removeNote(note) {
  try {
    await ElMessageBox.confirm(`确定删除笔记“${note.title || '未命名笔记'}”吗？删除后无法恢复。`, '删除笔记', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAdminNote(note.id)
    ElMessage.success('笔记删除成功')
    await load(records.value.length === 1 && page.value > 1 ? page.value - 1 : page.value)
  } catch (reason) {
    if (reason !== 'cancel' && reason !== 'close') ElMessage.error(toChineseErrorMessage(reason))
  }
}

onMounted(() => load())
</script>

<template>
  <section class="admin-page">
    <div class="admin-page-heading">
      <div>
        <p class="admin-kicker">NOTE MANAGEMENT</p>
        <h2>笔记管理</h2>
        <p>查询、查看并删除社区中的笔记内容。</p>
      </div>
      <span class="admin-total">共 {{ total }} 篇</span>
    </div>

    <form class="admin-filter-bar" @submit.prevent="load(1)">
      <label><span>关键词</span><input v-model="keyword" placeholder="搜索标题或内容" /></label>
      <label><span>状态</span><select v-model="status"><option value="">全部状态</option><option value="草稿">草稿</option><option value="待审核">待审核</option><option value="已发布">已发布</option><option value="已拒绝">已拒绝</option></select></label>
      <button class="admin-dark-button" type="submit">查询</button>
      <button class="admin-outline-button" type="button" @click="keyword = ''; status = ''; load(1)">重置</button>
    </form>

    <div v-if="loading" class="admin-state">正在加载笔记...</div>
    <div v-else-if="unavailable" class="admin-state"><strong>管理员接口尚未连接</strong><span>请启动后端并刷新页面。</span></div>
    <div v-else-if="error" class="admin-state admin-state-error"><strong>笔记加载失败</strong><span>{{ error }}</span><button class="admin-outline-button" @click="load(page)">重新加载</button></div>
    <div v-else-if="!records.length" class="admin-state"><strong>没有找到笔记</strong><span>换一个关键词或状态试试。</span></div>
    <div v-else class="admin-table-wrap">
      <table class="admin-table">
        <thead><tr><th>ID</th><th>标题</th><th>作者 ID</th><th>状态</th><th>浏览量</th><th>更新时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="note in records" :key="note.id">
            <td class="admin-id">#{{ note.id }}</td>
            <td><strong class="admin-note-title">{{ note.title || '未命名笔记' }}</strong><small class="admin-note-summary">{{ note.summary || note.content || '暂无摘要' }}</small></td>
            <td>{{ note.userId ?? '-' }}</td>
            <td><span class="admin-status" :class="statusClass(note.status)">{{ displayStatus(note.status) }}</span></td>
            <td>{{ Number(note.views || 0).toLocaleString() }}</td>
            <td>{{ note.updatedAt || note.createdAt || '-' }}</td>
            <td><button class="admin-delete-button" @click="removeNote(note)">删除</button></td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="totalPages > 1" class="admin-pagination"><button class="admin-outline-button" :disabled="page === 1" @click="load(page - 1)">上一页</button><span>第 {{ page }} / {{ totalPages }} 页</span><button class="admin-outline-button" :disabled="page === totalPages" @click="load(page + 1)">下一页</button></div>
  </section>
</template>
