<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import NoteGrid from '../components/NoteGrid.vue'
import UnavailableState from '../components/UnavailableState.vue'
import { fetchUserNotes, fetchUserProfile, getCurrentUser, isUnavailableError, updateCurrentUser, uploadAvatar } from '../api'
import { toChineseErrorMessage } from '../api/status'

const route = useRoute()
const current = ref(null)
const notes = ref([])
const loading = ref(true)
const error = ref('')
const unavailable = ref(false)
const page = ref(1)
const size = 10
const total = ref(0)
const editing = ref(false)
const saving = ref(false)
const saveError = ref('')
const form = ref({ nickname: '', gender: 0, profile: '' })
const avatarFile = ref(null)
const avatarPreview = ref('')
const avatarError = ref('')
const isCurrentProfile = computed(() => !route.params.id)
const user = computed(() => current.value || { nickname: '校园同学', avatar: '', bio: '正在校园里收集值得记住的瞬间。' })
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))

function fillForm(value) {
  form.value = { nickname: value.nickname || '', gender: value.gender ?? 0, profile: value.bio || '' }
}

function chooseAvatar(event) {
  avatarError.value = ''
  const file = event.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { avatarError.value = '请选择图片文件'; event.target.value = ''; return }
  if (file.size > 5 * 1024 * 1024) { avatarError.value = '头像不能超过 5MB'; event.target.value = ''; return }
  if (avatarPreview.value.startsWith('blob:')) URL.revokeObjectURL(avatarPreview.value)
  avatarFile.value = file
  avatarPreview.value = URL.createObjectURL(file)
}

async function loadNotes(userId, targetPage = page.value) {
  const pageData = await fetchUserNotes(userId, targetPage, size)
  notes.value = pageData.records
  total.value = pageData.total
  page.value = pageData.page
}

async function changePage(targetPage) {
  if (!current.value || targetPage < 1 || targetPage > totalPages.value) return
  loading.value = true
  error.value = ''
  try {
    await loadNotes(current.value.id, targetPage)
  } catch (reason) {
    error.value = toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

function startEdit() {
  fillForm(current.value)
  saveError.value = ''
  editing.value = true
}

async function saveProfile() {
  saveError.value = ''
  if (!form.value.nickname.trim()) {
    saveError.value = '昵称不能为空'
    return
  }

  saving.value = true
  try {
    await updateCurrentUser({
      nickname: form.value.nickname.trim(),
      gender: form.value.gender,
      profile: form.value.profile
    })
    if (avatarFile.value) await uploadAvatar(avatarFile.value)
    current.value = await getCurrentUser()
    fillForm(current.value)
    window.dispatchEvent(new CustomEvent('campus:user-updated', { detail: current.value }))
    editing.value = false
    avatarFile.value = null
    if (avatarPreview.value.startsWith('blob:')) URL.revokeObjectURL(avatarPreview.value)
    avatarPreview.value = ''
  } catch (reason) {
    saveError.value = toChineseErrorMessage(reason)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const profileId = route.params.id
  try {
    current.value = profileId ? await fetchUserProfile(profileId) : await getCurrentUser()
    fillForm(current.value)
    await loadNotes(profileId || current.value.id)
  } catch (reason) {
    unavailable.value = isUnavailableError(reason)
    error.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
})

onBeforeUnmount(() => {
  if (avatarPreview.value.startsWith('blob:')) URL.revokeObjectURL(avatarPreview.value)
})
</script>

<template>
  <section class="profile-page">
    <div class="profile-head">
      <img v-if="user.avatar" :src="user.avatar" alt="头像" />
      <span v-else class="profile-avatar" aria-label="默认头像">{{ (user.nickname || '校').slice(0, 1) }}</span>
      <div>
        <p class="eyebrow">MY CAMPUS JOURNAL</p>
        <h1>{{ user.nickname }}</h1>
        <p>{{ user.bio }}</p>
        <div class="profile-stats">
          <span><strong>{{ total }}</strong> 篇笔记</span>
          <span><strong>68</strong> 位读者</span>
        </div>
      </div>
      <button v-if="isCurrentProfile && !editing" class="outline-button" type="button" @click="startEdit">编辑资料</button>
    </div>

    <form v-if="isCurrentProfile && editing" class="profile-editor" @submit.prevent="saveProfile">
      <div class="form-label-row">
        <span class="form-label">资料设置</span>
        <button class="text-button" type="button" @click="editing = false">取消</button>
      </div>
      <label>昵称<input v-model="form.nickname" maxlength="50" /></label>
      <div class="avatar-upload-field"><span class="form-label">头像</span><div class="avatar-upload-preview"><img v-if="avatarPreview || user.avatar" :src="avatarPreview || user.avatar" alt="头像预览" /><span v-else class="profile-avatar">{{ (user.nickname || '校').slice(0, 1) }}</span></div><label class="upload-box"><span>选择头像图片</span><small>支持 JPG、PNG，最大 5MB</small><input type="file" accept="image/*" @change="chooseAvatar" /></label><p v-if="avatarError" class="form-error">{{ avatarError }}</p></div>
      <div class="gender-options">
        <label v-for="option in [{ value: 0, label: '未设置' }, { value: 1, label: '男' }, { value: 2, label: '女' }]" :key="option.value" class="gender-option" :class="{ active: form.gender === option.value }">
          <input v-model.number="form.gender" type="radio" :value="option.value" name="profile-gender" />
          <span>{{ option.label }}</span>
        </label>
      </div>
      <label>个人简介<textarea v-model="form.profile" maxlength="500" rows="4" /></label>
      <p v-if="saveError" class="form-error">{{ saveError }}</p>
      <button class="dark-button" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存资料' }}</button>
    </form>

    <div class="profile-notes">
      <div class="section-heading">
        <div><p class="eyebrow">YOUR NOTES</p><h2>校园笔记</h2></div>
        <div v-if="isCurrentProfile" class="profile-actions">
          <RouterLink to="/favorites" class="profile-favorites-link">我的收藏</RouterLink>
          <RouterLink to="/notes/create" class="dark-button small">+ 写一篇</RouterLink>
        </div>
      </div>
      <UnavailableState v-if="unavailable" title="个人主页接口尚未接入" />
      <div v-else-if="error" class="state-box"><strong>暂时无法加载个人主页</strong><span>{{ error }}</span></div>
      <div v-else-if="loading" class="state-box">正在加载个人主页...</div>
      <template v-else-if="notes.length">
        <NoteGrid :notes="notes" />
        <div v-if="totalPages > 1" class="profile-pagination">
          <button class="outline-button" :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
          <span>第 {{ page }} / {{ totalPages }} 页</span>
          <button class="outline-button" :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
        </div>
      </template>
      <div v-else class="state-box">还没有发布笔记，写下你的第一段校园故事吧。</div>
    </div>
  </section>
</template>
