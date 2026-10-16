<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import NoteGrid from '../components/NoteGrid.vue'
import UnavailableState from '../components/UnavailableState.vue'
import { fetchMyFavorites, isUnavailableError } from '../api'
import { toChineseErrorMessage } from '../api/status'

const notes = ref([])
const loading = ref(true)
const error = ref('')
const unavailable = ref(false)
const page = ref(1)
const size = 9
const total = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))

async function load(targetPage = 1) {
  loading.value = true
  error.value = ''
  unavailable.value = false
  try {
    const result = await fetchMyFavorites(targetPage, size)
    notes.value = result.records
    total.value = result.total
    page.value = result.page
  } catch (reason) {
    unavailable.value = isUnavailableError(reason)
    error.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

function changePage(targetPage) {
  if (targetPage < 1 || targetPage > totalPages.value || loading.value) return
  load(targetPage)
}

onMounted(() => load())
</script>

<template>
  <section class="favorites-page">
    <div class="favorites-header">
      <div>
        <p class="eyebrow">SAVED NOTES</p>
        <h1>我的收藏</h1>
        <p>把想再次读到的校园故事，留在这里。</p>
      </div>
      <span class="favorites-count">共 {{ total }} 篇</span>
    </div>

    <UnavailableState v-if="unavailable" title="收藏列表接口暂不可用" message="收藏服务暂时无法访问，请稍后刷新重试。" />
    <div v-else-if="error" class="state-box"><strong>暂时无法加载收藏</strong><span>{{ error }}</span><button class="outline-button" type="button" @click="load(page)">重新加载</button></div>
    <div v-else-if="loading" class="state-box">正在加载收藏...</div>
    <template v-else-if="notes.length">
      <NoteGrid :notes="notes" />
      <div v-if="totalPages > 1" class="profile-pagination">
        <button class="outline-button" :disabled="page === 1" type="button" @click="changePage(page - 1)">上一页</button>
        <span>第 {{ page }} / {{ totalPages }} 页</span>
        <button class="outline-button" :disabled="page === totalPages" type="button" @click="changePage(page + 1)">下一页</button>
      </div>
    </template>
    <div v-else class="state-box"><strong>还没有收藏笔记</strong><span>在笔记详情页点击“收藏”，以后可以从这里继续阅读。</span><RouterLink to="/" class="outline-button">去发现笔记</RouterLink></div>
  </section>
</template>
