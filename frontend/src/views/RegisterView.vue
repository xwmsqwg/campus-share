<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { login, register, uploadAvatar } from '../api'
import { toChineseErrorMessage } from '../api/status'
import { validateRegisterForm } from '../api/registerValidation'

const router = useRouter()
const form = ref({ username: '', password: '', confirm: '', nickname: '', gender: 0, profile: '' })
const error = ref('')
const fieldErrors = ref({})
const avatarError = ref('')
const avatarFile = ref(null)
const avatarPreview = ref('')
const loading = ref(false)

function chooseAvatar(event) {
  avatarError.value = ''
  const file = event.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    avatarError.value = '请选择图片文件'
    event.target.value = ''
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    avatarError.value = '头像不能超过 5MB'
    event.target.value = ''
    return
  }
  if (avatarPreview.value.startsWith('blob:')) URL.revokeObjectURL(avatarPreview.value)
  avatarFile.value = file
  avatarPreview.value = URL.createObjectURL(file)
}

async function submit() {
  error.value = ''
  fieldErrors.value = validateRegisterForm(form.value)
  const firstError = Object.keys(fieldErrors.value)[0]
  if (firstError) {
    document.querySelector(`[name="${firstError}"]`)?.focus()
    return
  }

  loading.value = true
  try {
    const { confirm, ...payload } = form.value
    await register(payload)
    if (avatarFile.value) {
      await login({ username: form.value.username, password: form.value.password })
      await uploadAvatar(avatarFile.value)
      router.push('/profile')
    } else {
      router.push('/login?registered=1')
    }
  } catch (reason) {
    error.value = toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
}

onBeforeUnmount(() => {
  if (avatarPreview.value.startsWith('blob:')) URL.revokeObjectURL(avatarPreview.value)
})
</script>

<template>
  <div class="auth-page">
    <div class="auth-image register-image">
      <div>
        <p class="eyebrow">FIND YOUR CAMPUS</p>
        <h1>从今天开始，<br /><i>把校园写成故事。</i></h1>
      </div>
    </div>
    <div class="auth-panel">
      <RouterLink to="/" class="brand auth-brand">
        <span class="brand-mark">拾</span><span>拾光 <em>Campus</em></span>
      </RouterLink>
      <div class="auth-content">
        <p class="eyebrow">JOIN US</p>
        <h2>加入拾光</h2>
        <p class="auth-description">注册一个账号，开始记录属于你的校园生活。</p>
        <form novalidate @submit.prevent="submit">
          <label>
            用户名
            <input v-model="form.username" name="username" maxlength="20" placeholder="给自己取一个名字" @input="fieldErrors.username = ''" />
            <small v-if="fieldErrors.username" class="field-error">{{ fieldErrors.username }}</small>
          </label>
          <label>
            密码
            <input v-model="form.password" name="password" maxlength="72" type="password" placeholder="至少 8 位密码" @input="fieldErrors.password = ''" />
            <small v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</small>
          </label>
          <label>
            确认密码
            <input v-model="form.confirm" name="confirm" type="password" placeholder="再输入一次密码" @input="fieldErrors.confirm = ''" />
            <small v-if="fieldErrors.confirm" class="field-error">{{ fieldErrors.confirm }}</small>
          </label>
          <label>
            昵称
            <input v-model="form.nickname" name="nickname" maxlength="50" placeholder="展示给其他同学的昵称" @input="fieldErrors.nickname = ''" />
            <small v-if="fieldErrors.nickname" class="field-error">{{ fieldErrors.nickname }}</small>
          </label>

          <div class="form-field">
            <span class="form-label">头像 <small>可选，最大 5MB</small></span>
            <div v-if="avatarPreview" class="avatar-upload-preview"><img :src="avatarPreview" alt="头像预览" /></div>
            <label class="upload-box">
              <span>选择头像图片</span>
              <input type="file" accept="image/*" @change="chooseAvatar" />
            </label>
            <p v-if="avatarError" class="form-error">{{ avatarError }}</p>
          </div>

          <div class="form-field">
            <span class="form-label">性别</span>
            <div class="gender-options">
              <label v-for="option in [{ value: 0, label: '未设置' }, { value: 1, label: '男' }, { value: 2, label: '女' }]" :key="option.value" class="gender-option" :class="{ active: form.gender === option.value }">
                <input v-model.number="form.gender" type="radio" name="gender" :value="option.value" />
                <span>{{ option.label }}</span>
              </label>
            </div>
          </div>

          <div class="form-field">
            <div class="form-label-row">
              <span class="form-label">个人简介 <small>可选</small></span>
              <span class="field-count">{{ form.profile.length }}/500</span>
            </div>
            <textarea v-model="form.profile" class="fixed-profile" maxlength="500" rows="4" placeholder="介绍一下自己，让校园里的朋友更了解你" />
          </div>

          <p v-if="error" class="form-error">{{ error }}</p>
          <button class="dark-button full" :disabled="loading">{{ loading ? '创建中...' : '创建账号' }}</button>
        </form>
        <p class="auth-switch">已经有账号？<RouterLink to="/login">直接登录</RouterLink></p>
      </div>
    </div>
  </div>
</template>
