import test from 'node:test'
import assert from 'node:assert/strict'
import { normalizeUser, normalizeNote, normalizeLikeStatus, unwrapResponse, unwrapPage, withNoteAuthor } from './normalize.js'
import { formatTokenHeader } from './auth.js'
import { toChineseErrorMessage } from './status.js'
import { validateRegisterForm } from './registerValidation.js'

test('normalizes backend user fields for the existing frontend', () => {
  const user = normalizeUser({ id: 7, nickname: '小光', image: '/avatar.png', profile: '简介' })
  assert.equal(user.id, 7)
  assert.equal(user.avatar, '/avatar.png')
  assert.equal(user.bio, '简介')
})

test('normalizes backend note fields for the existing frontend', () => {
  const note = normalizeNote({ id: 3, context: '正文', view_count: 9, created_at: '2026-10-04 10:00:00' })
  assert.equal(note.id, 3)
  assert.equal(note.content, '正文')
  assert.equal(note.views, 9)
  assert.equal(note.createdAt, '2026-10-04 10:00:00')
})

test('normalizes note images as a cover image source', () => {
  const note = normalizeNote({
    id: 4,
    images: [{ id: 8, noteId: 4, imageUrl: 'https://oss.example/note.jpg', sortOrder: 1 }]
  })
  assert.equal(note.cover, 'https://oss.example/note.jpg')
  assert.equal(note.images[0].imageUrl, 'https://oss.example/note.jpg')
})

test('fills missing note author data from a user profile and preserves existing fields', () => {
  const note = normalizeNote({ id: 5, userId: 23 })
  const result = withNoteAuthor(note, { nickname: '校园同学', avatar: 'https://oss.example/avatar.jpg' })
  assert.equal(result.author, '校园同学')
  assert.equal(result.avatar, 'https://oss.example/avatar.jpg')

  const existing = withNoteAuthor({ ...note, author: '已有昵称', avatar: 'existing.jpg' }, { nickname: '新昵称', avatar: 'new.jpg' })
  assert.equal(existing.author, '已有昵称')
  assert.equal(existing.avatar, 'existing.jpg')
})

test('normalizes like status fields returned by the backend', () => {
  assert.deepEqual(normalizeLikeStatus({ liked: 1, like_count: '6' }), {
    liked: true,
    likeCount: 6
  })
})

test('handles an empty like response without exposing a JavaScript error', () => {
  assert.deepEqual(normalizeLikeStatus(null, 3), {
    liked: false,
    likeCount: 3
  })
})

test('translates technical frontend errors into Chinese user messages', () => {
  assert.equal(toChineseErrorMessage("Cannot read properties of null (reading 'liked')"), '点赞结果为空，请检查后端返回数据。')
  assert.equal(toChineseErrorMessage('Network Error'), '后端服务未连接，请启动后端后重试。')
})

test('returns readable register field validation messages', () => {
  assert.deepEqual(validateRegisterForm({ username: 'a', password: '123', confirm: '456', nickname: '' }), {
    username: '用户名至少需要 2 个字符',
    password: '密码至少需要 8 个字符',
    confirm: '两次输入的密码不一致',
    nickname: '请输入昵称'
  })
})

test('formats the JWT authorization header for the backend', () => {
  assert.equal(formatTokenHeader('jwt-token'), 'Bearer jwt-token')
  assert.equal(formatTokenHeader(''), undefined)
})

test('unwraps the documented response envelope and rejects business errors', () => {
  assert.deepEqual(unwrapResponse({ code: 200, message: 'success', data: { ok: true } }), { ok: true })
  assert.throws(() => unwrapResponse({ code: 400, message: '失败', data: null }), /失败/)
})

test('unwraps paginated records nested inside the response data', () => {
  assert.deepEqual(unwrapPage({ code: 200, message: 'success', data: { records: [{ id: 1 }], total: 1 } }), {
    records: [{ id: 1 }],
    total: 1
  })
})
