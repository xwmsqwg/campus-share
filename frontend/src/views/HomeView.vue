<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import NoteGrid from '../components/NoteGrid.vue'
import UnavailableState from '../components/UnavailableState.vue'
import { fetchNotes } from '../api'
import { toChineseErrorMessage } from '../api/status'

const notes = ref([])
const keyword = ref('')
const loading = ref(true)
const unavailable = ref(false)
const error = ref('')
const page = ref(1)
const size = 9
const total = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))

async function load(targetPage = 1) {
  loading.value = true
  unavailable.value = false
  error.value = ''
  try {
    const result = await fetchNotes(keyword.value, targetPage, size)
    notes.value = result.records
    total.value = result.total
    page.value = result.page
  } catch (reason) {
    unavailable.value = reason.kind === 'unavailable'
    error.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

onMounted(() => load())
</script>

<template>
  <section class="home-hero"><div class="hero-inner"><div class="hero-copy"><p class="eyebrow">CAMPUS NOTES / 01</p><h1>记录校园里<br /><i>值得被记住的光。</i></h1><p class="hero-lead">拾光是一个属于校园的图文分享空间。<br />写下今天，也看看别人正在经历的日常。</p><RouterLink to="/notes/create" class="dark-button">开始写一篇笔记 <span>↗</span></RouterLink></div><div class="hero-art"><div class="hero-photo"><img src="https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1200&q=85" alt="校园图书馆" /></div><div class="hero-note">“校园最好的时刻，常常发生在赶路之外。”<small>拾光编辑部</small></div></div></div></section>
  <section id="featured" class="content-section">
    <div class="section-heading"><div><p class="eyebrow">EXPLORE THE CAMPUS</p><h2>最近的校园笔记</h2></div><form class="search-box" @submit.prevent="load(1)"><span>⌕</span><input v-model="keyword" placeholder="搜索校园故事、地点或关键词" /><button type="submit">搜索</button></form></div>
    <div v-if="loading" class="state-box">正在整理最新的校园故事...</div>
    <UnavailableState v-else-if="unavailable" title="笔记接口尚未接入" />
    <div v-else-if="error" class="state-box"><strong>暂时无法加载笔记</strong><span>{{ error }}</span><button class="outline-button" @click="load(page)">重新加载</button></div>
    <div v-else-if="!notes.length" class="state-box"><strong>还没有找到相关笔记</strong><span>换个关键词试试，或者成为第一个分享的人。</span></div>
    <template v-else><NoteGrid :notes="notes" /><div v-if="totalPages > 1" class="profile-pagination"><button class="outline-button" :disabled="page === 1" @click="load(page - 1)">上一页</button><span>第 {{ page }} / {{ totalPages }} 页 · 共 {{ total }} 篇</span><button class="outline-button" :disabled="page === totalPages" @click="load(page + 1)">下一页</button></div></template>
  </section>
  <section id="about" class="manifesto"><p class="eyebrow">A SMALL PLACE FOR BIG MOMENTS</p><h2>每个人的校园生活，<br /><i>都值得被认真写下。</i></h2><p>从一顿晚饭到一次散步，从实验室的灯到操场的风。<br />在拾光，找到与你共享同一片校园的人。</p></section>
</template>
