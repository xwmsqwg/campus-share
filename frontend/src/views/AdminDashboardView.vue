<script setup>
import { onMounted, ref } from 'vue'
import { fetchAdminStatistics, isUnavailableError } from '../api'
import { toChineseErrorMessage } from '../api/status'

const loading = ref(true)
const error = ref('')
const unavailable = ref(false)
const statistics = ref({
  userCount: 0,
  noteCount: 0,
  publishedNoteCount: 0,
  commentCount: 0,
  likeCount: 0,
  favoriteCount: 0
})

const statItems = [
  { key: 'userCount', label: '用户总数', hint: '已注册用户' },
  { key: 'noteCount', label: '笔记总数', hint: '包含全部状态' },
  { key: 'publishedNoteCount', label: '已发布笔记', hint: '公开可见内容' },
  { key: 'commentCount', label: '评论总数', hint: '社区互动' },
  { key: 'likeCount', label: '点赞总数', hint: '内容反馈' },
  { key: 'favoriteCount', label: '收藏总数', hint: '用户收藏' }
]

async function loadStatistics() {
  loading.value = true
  error.value = ''
  unavailable.value = false
  try {
    statistics.value = { ...statistics.value, ...(await fetchAdminStatistics()) }
  } catch (reason) {
    unavailable.value = isUnavailableError(reason)
    error.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

onMounted(loadStatistics)
</script>

<template>
  <section class="admin-page">
    <div class="admin-page-heading">
      <div>
        <p class="admin-kicker">OVERVIEW</p>
        <h2>今天的社区概况</h2>
        <p>快速了解拾光社区的内容和互动情况。</p>
      </div>
      <button class="admin-outline-button" :disabled="loading" @click="loadStatistics">↻ 刷新数据</button>
    </div>

    <div v-if="loading" class="admin-state">正在加载统计数据...</div>
    <div v-else-if="unavailable" class="admin-state"><strong>管理员接口尚未连接</strong><span>请启动后端并刷新页面。</span></div>
    <div v-else-if="error" class="admin-state admin-state-error"><strong>统计数据加载失败</strong><span>{{ error }}</span><button class="admin-outline-button" @click="loadStatistics">重新加载</button></div>
    <div v-else class="admin-stat-grid">
      <article v-for="item in statItems" :key="item.key" class="admin-stat-item">
        <span class="admin-stat-label">{{ item.label }}</span>
        <strong>{{ Number(statistics[item.key] || 0).toLocaleString() }}</strong>
        <small>{{ item.hint }}</small>
      </article>
    </div>

  </section>
</template>
