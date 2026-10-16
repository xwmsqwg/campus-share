<script setup>
import { onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({ initial: { type: Object, default: () => ({}) }, submitting: Boolean })
const emit = defineEmits(['submit'])
const form = ref({ title: '', summary: '', content: '' })
const imageFiles = ref([])
const imagePreviews = ref([])
const savedImages = ref([])
const imageError = ref('')

function clearPendingImages() {
  imagePreviews.value.forEach(({ url }) => {
    if (url.startsWith('blob:')) URL.revokeObjectURL(url)
  })
  imageFiles.value = []
  imagePreviews.value = []
}

function syncInitial(value) {
  form.value = { title: value.title || '', summary: value.summary || '', content: value.content || '' }
  savedImages.value = value.images?.length ? value.images : (value.cover ? [{ id: 'cover', imageUrl: value.cover }] : [])
  clearPendingImages()
  imageError.value = ''
}

watch(() => props.initial, syncInitial, { immediate: true })

function chooseImage(event) {
  imageError.value = ''
  const files = Array.from(event.target.files || [])
  if (!files.length) return
  files.forEach((file) => {
    if (!file.type.startsWith('image/')) {
      imageError.value = '已跳过非图片文件，请选择 JPG、PNG 等图片。'
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      imageError.value = '已跳过超过 5MB 的图片。'
      return
    }
    imageFiles.value.push(file)
    imagePreviews.value.push({ file, url: URL.createObjectURL(file) })
  })
  event.target.value = ''
}

function removeImage(index) {
  const preview = imagePreviews.value[index]
  if (preview?.url.startsWith('blob:')) URL.revokeObjectURL(preview.url)
  imagePreviews.value.splice(index, 1)
  imageFiles.value.splice(index, 1)
}

function submit() {
  emit('submit', {
    ...form.value,
    imageFiles: imageFiles.value,
    cover: imagePreviews.value[0]?.url || ''
  })
}

onBeforeUnmount(clearPendingImages)
</script>

<template>
  <form class="editor-form" @submit.prevent="submit">
    <label>标题<input v-model="form.title" required maxlength="50" placeholder="给这段校园时光一个标题" /></label>
    <label>一句话摘要<textarea v-model="form.summary" required maxlength="120" rows="2" placeholder="让别人先知道这篇笔记写了什么" /></label>
    <label>正文<textarea v-model="form.content" required rows="9" placeholder="记录你的校园故事、经验或发现" /></label>
    <div class="image-field">
      <div class="form-label-row"><span class="form-label">笔记图片 <small>可选，单张最大 5MB</small></span></div>
      <div v-if="savedImages.length" class="editor-saved-images"><img v-for="image in savedImages" :key="image.id || image.imageUrl" :src="image.imageUrl" alt="已上传图片" /></div>
      <div v-if="imagePreviews.length" class="image-preview-grid">
        <div v-for="(preview, index) in imagePreviews" :key="preview.url" class="image-preview-item">
          <img :src="preview.url" :alt="`待上传图片 ${index + 1}`" />
          <span v-if="index === 0" class="image-cover-label">封面</span>
          <button type="button" class="text-danger" @click="removeImage(index)">移除</button>
        </div>
      </div>
      <p v-if="imageError" class="form-error">{{ imageError }}</p>
      <label class="upload-box"><span>{{ savedImages.length || imagePreviews.length ? '继续添加图片' : '选择校园照片' }}</span><small>可一次选择多张；第一张将作为封面</small><input type="file" multiple accept="image/*" @change="chooseImage" /></label>
    </div>
    <button class="dark-button" :disabled="submitting" type="submit">{{ submitting ? '保存中...' : '保存笔记' }}</button>
  </form>
</template>
