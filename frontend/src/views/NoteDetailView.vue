<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import UnavailableState from '../components/UnavailableState.vue'
import { createComment, deleteComment, deleteNote, favoriteNote, fetchComments, fetchNote, getCurrentUser, getFavoriteStatus, getLikeStatus, isUnavailableError, likeNote, unfavoriteNote, unlikeNote } from '../api'
import { toChineseErrorMessage } from '../api/status'

const route = useRoute()
const router = useRouter()
const note = ref(null)
const loading = ref(true)
const unavailable = ref(false)
const loadError = ref('')
const user = ref(null)
const comments = ref([])
const commentsLoading = ref(false)
const commentsUnavailable = ref(false)
const commentText = ref('')
const commentError = ref('')
const imageUnavailable = ref(false)
const actionError = ref('')
const likeState = ref({ liked: false, likeCount: 0 })
const likeLoading = ref(false)
const likeUnavailable = ref(false)
const likeError = ref('')
const favoriteState = ref({ favorited: false })
const favoriteLoading = ref(false)
const favoriteUnavailable = ref(false)
const favoriteError = ref('')
const canEdit = computed(() => note.value && user.value && note.value.userId === user.value.id)
const canDeleteComment = (comment) => user.value && (comment.userId === user.value.id || String(user.value.role).toLowerCase() === 'admin')

function markImageUnavailable() {
  imageUnavailable.value = true
}

function isCommentUnavailable(reason) {
  return isUnavailableError(reason)
}

function commentErrorMessage(reason) {
  const message = reason?.message || '评论请求失败，请稍后重试。'
  if (/没有登[录陆]|未登录|登录已失效/.test(message)) return '请先登录后再进行评论操作。'
  return toChineseErrorMessage(message)
}

function likeErrorMessage(reason) {
  const message = reason?.message || '点赞请求失败，请稍后重试。'
  if (/没有登[录陆]|未登录|登录已失效/.test(message)) return '请先登录后再点赞。'
  return toChineseErrorMessage(message)
}

function favoriteErrorMessage(reason) {
  const message = reason?.message || '收藏请求失败，请稍后重试。'
  if (/没有登[录陆]|未登录|登录已失效/.test(message)) return '请先登录后再收藏。'
  return toChineseErrorMessage(message)
}

async function loadLikeStatus() {
  if (!localStorage.getItem('campus_token')) {
    likeState.value = { liked: false, likeCount: Number(note.value?.likes || 0) }
    return
  }
  try {
    likeState.value = await getLikeStatus(note.value.id, note.value.likes)
  } catch (reason) {
    likeUnavailable.value = isUnavailableError(reason)
    likeError.value = likeUnavailable.value ? '' : likeErrorMessage(reason)
    likeState.value = { liked: false, likeCount: Number(note.value?.likes || 0) }
  }
}

async function toggleLike() {
  if (!localStorage.getItem('campus_token')) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (likeLoading.value) return
  likeLoading.value = true
  likeError.value = ''
  likeUnavailable.value = false
  try {
    likeState.value = likeState.value.liked
      ? await unlikeNote(note.value.id)
      : await likeNote(note.value.id)
  } catch (reason) {
    likeUnavailable.value = isUnavailableError(reason)
    likeError.value = likeUnavailable.value ? '' : likeErrorMessage(reason)
  } finally {
    likeLoading.value = false
  }
}

async function loadFavoriteStatus() {
  if (!localStorage.getItem('campus_token')) {
    favoriteState.value = { favorited: false }
    return
  }
  try {
    favoriteState.value = await getFavoriteStatus(note.value.id)
  } catch (reason) {
    favoriteUnavailable.value = isUnavailableError(reason)
    favoriteError.value = favoriteUnavailable.value ? '' : favoriteErrorMessage(reason)
    favoriteState.value = { favorited: false }
  }
}

async function toggleFavorite() {
  if (!localStorage.getItem('campus_token')) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (favoriteLoading.value) return
  favoriteLoading.value = true
  favoriteError.value = ''
  favoriteUnavailable.value = false
  try {
    favoriteState.value = favoriteState.value.favorited
      ? await unfavoriteNote(note.value.id)
      : await favoriteNote(note.value.id)
  } catch (reason) {
    favoriteUnavailable.value = isUnavailableError(reason)
    favoriteError.value = favoriteUnavailable.value ? '' : favoriteErrorMessage(reason)
  } finally {
    favoriteLoading.value = false
  }
}

onMounted(async () => {
  try {
    note.value = await fetchNote(route.params.id)
    await loadLikeStatus()
    await loadFavoriteStatus()
    if (localStorage.getItem('campus_token')) {
      commentsLoading.value = true
      try {
        comments.value = await fetchComments(route.params.id)
      } catch (reason) {
        commentsUnavailable.value = isCommentUnavailable(reason)
        commentError.value = commentsUnavailable.value ? '' : commentErrorMessage(reason)
      } finally {
        commentsLoading.value = false
      }
    } else {
      commentError.value = ''
    }
    if (localStorage.getItem('campus_token')) {
      try { user.value = await getCurrentUser() } catch {}
    }
  } catch (reason) {
    unavailable.value = isUnavailableError(reason)
    loadError.value = unavailable.value ? '' : toChineseErrorMessage(reason)
  } finally {
    loading.value = false
  }
})

async function remove() {
  actionError.value = ''
  if (confirm('确定要删除这篇笔记吗？')) {
    try {
      await deleteNote(note.value.id)
      router.push('/')
    } catch (reason) {
      actionError.value = toChineseErrorMessage(reason)
    }
  }
}

async function submitComment() {
  commentError.value = ''
  if (!commentText.value.trim()) {
    commentError.value = '请输入评论内容'
    return
  }
  try {
    const created = await createComment(note.value.id, commentText.value.trim())
    comments.value = [{
      ...created,
      nickname: created.nickname || user.value?.nickname || '当前用户',
      image: created.image || user.value?.avatar || ''
    }, ...comments.value]
    commentText.value = ''
  } catch (reason) {
    commentsUnavailable.value = isCommentUnavailable(reason)
    commentError.value = commentsUnavailable.value ? '' : commentErrorMessage(reason)
  }
}

async function removeComment(commentId) {
  try {
    await deleteComment(commentId)
    comments.value = comments.value.filter((comment) => comment.id !== commentId)
  } catch (reason) {
    commentsUnavailable.value = isCommentUnavailable(reason)
    commentError.value = commentsUnavailable.value ? '' : commentErrorMessage(reason)
  }
}
</script>

<template>
  <div v-if="loading" class="state-box">正在打开这篇笔记...</div>
  <UnavailableState v-else-if="unavailable" title="笔记详情接口尚未接入" />
  <div v-else-if="loadError" class="state-box"><strong>暂时无法打开笔记</strong><span>{{ loadError }}</span><RouterLink to="/" class="outline-button">返回首页</RouterLink></div>
  <div v-else-if="note" class="detail-page">
    <div class="detail-top">
      <RouterLink to="/" class="back-link">← 返回笔记流</RouterLink>
      <div v-if="canEdit" class="detail-actions">
        <RouterLink :to="`/notes/${note.id}/edit`" class="outline-button">编辑</RouterLink>
        <button class="text-danger" @click="remove">删除</button>
      </div>
    </div>
    <p v-if="actionError" class="form-error detail-error">{{ actionError }}</p>
    <article class="detail-layout">
      <div class="detail-main">
        <h1>{{ note.title }}</h1>
        <div class="detail-author"><img v-if="note.avatar" :src="note.avatar" alt="" /><span v-else class="detail-author-avatar" aria-hidden="true">{{ (note.author || '校').slice(0, 1) }}</span><div><strong>{{ note.author || `用户 ${note.userId}` }}</strong><span>{{ note.createdAt }} · {{ note.views }} 次阅读</span></div></div>
        <div v-if="note.images?.length" class="detail-gallery"><figure v-for="image in note.images" :key="image.id || image.imageUrl"><img :src="image.imageUrl" :alt="note.title" @error="markImageUnavailable" /><figcaption v-if="imageUnavailable">图片无法加载，请检查 OSS 对象的公共读权限。</figcaption></figure></div>
        <figure v-else-if="note.cover" class="detail-cover-wrap"><img class="detail-cover" :src="note.cover" :alt="note.title" @error="markImageUnavailable" /><figcaption v-if="imageUnavailable">图片无法加载，请检查 OSS 对象的公共读权限。</figcaption></figure>
        <div class="detail-content"><p v-for="paragraph in note.content.split('\n\n')" :key="paragraph">{{ paragraph }}</p></div>
        <section class="detail-reactions" aria-label="笔记互动">
          <button type="button" class="like-button" :class="{ liked: likeState.liked }" :disabled="likeLoading || likeUnavailable" @click="toggleLike">
            <span aria-hidden="true">{{ likeState.liked ? '♥' : '♡' }}</span>
            {{ likeLoading ? '处理中...' : likeState.liked ? '已点赞' : '点赞' }}
            <strong>{{ likeState.likeCount }}</strong>
          </button>
          <button type="button" class="favorite-button" :class="{ favorited: favoriteState.favorited }" :disabled="favoriteLoading || favoriteUnavailable" @click="toggleFavorite">
            <span aria-hidden="true">{{ favoriteState.favorited ? '★' : '☆' }}</span>
            {{ favoriteLoading ? '处理中...' : favoriteState.favorited ? '已收藏' : '收藏' }}
          </button>
          <span v-if="likeUnavailable" class="reaction-hint">点赞接口暂不可用</span>
          <span v-else-if="likeError" class="form-error reaction-hint">{{ likeError }}</span>
          <span v-else-if="!user" class="reaction-hint">登录后可以点赞</span>
          <span v-if="favoriteUnavailable" class="reaction-hint">收藏接口暂不可用</span>
          <span v-else-if="favoriteError" class="form-error reaction-hint">{{ favoriteError }}</span>
          <span v-else-if="!user" class="reaction-hint">登录后可以收藏</span>
        </section>
        <section class="comments-section">
          <div class="comments-heading"><div><p class="eyebrow">COMMENTS</p><h2>评论</h2></div><span>{{ comments.length }} 条评论</span></div>
          <UnavailableState v-if="commentsUnavailable" title="评论接口暂不可用" message="评论服务暂时无法访问，请稍后刷新重试。" />
          <template v-else>
            <div v-if="commentsLoading" class="comment-empty">正在加载评论...</div>
            <form v-else-if="user" class="comment-form" @submit.prevent="submitComment">
              <textarea v-model="commentText" rows="3" maxlength="1000" placeholder="说说你对这篇笔记的看法..." />
              <div class="comment-form-footer"><span v-if="commentError" class="form-error">{{ commentError }}</span><span v-else>评论只能针对这篇笔记</span><button class="dark-button small" type="submit">发表评论</button></div>
            </form>
            <div v-else class="comment-login-hint"><span>登录后查看评论并参与讨论。</span><RouterLink to="/login" class="outline-button">去登录</RouterLink></div>
            <p v-if="commentError" class="form-error">{{ commentError }}</p>
            <div v-if="comments.length" class="comment-list"><article v-for="comment in comments" :key="comment.id" class="comment-item"><img v-if="comment.image" :src="comment.image" alt="" /><span v-else class="comment-avatar-placeholder" aria-hidden="true">{{ (comment.nickname || '校').slice(0, 1) }}</span><div><div class="comment-meta"><strong>{{ comment.nickname || `用户 ${comment.userId}` }}</strong><span>{{ comment.createTime }}</span><button v-if="canDeleteComment(comment)" type="button" class="comment-delete" @click="removeComment(comment.id)">删除</button></div><p>{{ comment.content }}</p></div></article></div>
            <div v-else-if="!commentsLoading" class="comment-empty">还没有评论，来留下第一句话吧。</div>
          </template>
        </section>
      </div>
      <aside class="detail-aside"><p class="eyebrow">NOTE / {{ String(note.id).padStart(2, '0') }}</p><p>“把普通的一天写下来，过一阵子回头看，它就有了光。”</p></aside>
    </article>
  </div>
  <div v-else class="state-box">笔记不存在或已被删除。</div>
</template>
