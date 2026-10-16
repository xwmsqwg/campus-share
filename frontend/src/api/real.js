import http from './http'
import { normalizeComment, normalizeFavoritePage, normalizeFavoriteStatus, normalizeLikeStatus, normalizeNote, normalizeUser, unwrapPage, unwrapResponse } from './normalize'
import { enrichNoteAuthors } from './noteAuthors'

const authorProfileCache = new Map()
const authorProfileRequests = new Map()

async function loadAuthorProfile(userId) {
  const id = Number(userId)
  if (!Number.isInteger(id) || id <= 0) return null
  if (authorProfileCache.has(id)) return authorProfileCache.get(id)
  if (!authorProfileRequests.has(id)) {
    const request = http.get(`/users/${id}`)
      .then((response) => normalizeUser(unwrapResponse(response)))
      .catch(() => null)
      .finally(() => authorProfileRequests.delete(id))
    authorProfileRequests.set(id, request)
  }
  return authorProfileRequests.get(id)
}

async function hydrateNoteAuthors(notes) {
  const normalizedNotes = notes.map(normalizeNote)
  return enrichNoteAuthors(normalizedNotes, loadAuthorProfile, authorProfileCache)
}

export async function login(payload) {
  const data = unwrapResponse(await http.post('/auth/login', payload))
  const user = normalizeUser(data.user)
  localStorage.setItem('campus_token', data.token)
  localStorage.setItem('campus_user', JSON.stringify(user))
  return user
}
export async function register(payload) {
  return unwrapResponse(await http.post('/auth/register', payload))
}
export async function getCurrentUser() { return normalizeUser(unwrapResponse(await http.get('/auth/me'))) }
export async function fetchUserProfile(userId) { return normalizeUser(unwrapResponse(await http.get(`/users/${userId}`))) }
export async function updateCurrentUser(payload) {
  return unwrapResponse(await http.put('/users/me', payload))
}
export async function uploadAvatar(file) {
  const form = new FormData()
  form.append('file', file)
  return unwrapResponse(await http.post('/users/me/avatar', form))
}
export async function logout() { localStorage.removeItem('campus_token'); localStorage.removeItem('campus_user'); return true }
export async function fetchNotes(keyword = '', page = 1, size = 9) {
  const data = unwrapPage(await http.get('/notes', { params: { keyword, page, size } }))
  return { records: await hydrateNoteAuthors(data.records), total: Number(data.total || 0), page: Number(data.page || page), size: Number(data.size || size) }
}
export async function fetchNote(id) {
  const records = await hydrateNoteAuthors([unwrapResponse(await http.get(`/notes/${id}`))])
  return records[0]
}
const notePayload = (payload) => ({ title: payload.title, summary: payload.summary, context: payload.content ?? payload.context })
export async function createNote(payload) {
  const records = await hydrateNoteAuthors([unwrapResponse(await http.post('/notes', notePayload(payload)))])
  return records[0]
}
export async function updateNote(id, payload) {
  const records = await hydrateNoteAuthors([unwrapResponse(await http.put(`/notes/${id}`, notePayload(payload)))])
  return records[0]
}
export async function deleteNote(id) {
  const ids = Array.isArray(id) ? id : [id]
  unwrapResponse(await http.delete('/notes', { data: { ids } }))
  return true
}
export async function fetchUserNotes(userId, page = 1, size = 10) {
  const pageData = unwrapPage(await http.get(`/users/${userId}/notes`, { params: { page, size } }))
  return { records: await hydrateNoteAuthors(pageData.records), total: Number(pageData.total || 0), page: Number(pageData.page || page), size: Number(pageData.size || size) }
}
export async function fetchComments(noteId) { return (unwrapResponse(await http.get(`/notes/${noteId}/comments`)) || []).map(normalizeComment) }
export async function createComment(noteId, content) { return normalizeComment(unwrapResponse(await http.post(`/notes/${noteId}/comments`, { content }))) }
export async function deleteComment(id) { unwrapResponse(await http.delete(`/comments/${id}`)); return true }
export async function likeNote(id) {
  const data = unwrapResponse(await http.post(`/notes/${id}/like`))
  return data == null ? getLikeStatus(id) : normalizeLikeStatus(data)
}
export async function unlikeNote(id) {
  const data = unwrapResponse(await http.delete(`/notes/${id}/like`))
  return data == null ? getLikeStatus(id) : normalizeLikeStatus(data)
}
export async function getLikeStatus(id, fallbackCount = 0) { return normalizeLikeStatus(unwrapResponse(await http.get(`/notes/${id}/like/status`)), fallbackCount) }
export async function favoriteNote(id) {
  const data = unwrapResponse(await http.post(`/notes/${id}/favorite`))
  return data == null ? getFavoriteStatus(id) : normalizeFavoriteStatus(data)
}
export async function unfavoriteNote(id) {
  const data = unwrapResponse(await http.delete(`/notes/${id}/favorite`))
  return data == null ? getFavoriteStatus(id) : normalizeFavoriteStatus(data)
}
export async function getFavoriteStatus(id) {
  return normalizeFavoriteStatus(unwrapResponse(await http.get(`/notes/${id}/favorite/status`)))
}
export async function fetchMyFavorites(page = 1, size = 9) {
  const pageData = normalizeFavoritePage(unwrapPage(await http.get('/users/me/favorites', { params: { page, size } })))
  return { ...pageData, records: await enrichNoteAuthors(pageData.records, loadAuthorProfile, authorProfileCache) }
}
export async function uploadNoteImage(id, file) {
  const form = new FormData()
  form.append('file', file)
  const response = await http.post(`/notes/${id}/images`, form)
  return unwrapResponse(response)
}

export async function fetchAdminStatistics() {
  return unwrapResponse(await http.get('/admin/statistics'))
}

export async function fetchAdminNotes(keyword = '', status = '', page = 1, size = 10) {
  const data = unwrapPage(await http.get('/admin/notes', {
    params: { keyword, status, page, size }
  }))
  return {
    records: data.records.map(normalizeNote),
    total: Number(data.total || 0),
    page: Number(data.page || page),
    size: Number(data.size || size)
  }
}

export async function deleteAdminNote(id) {
  unwrapResponse(await http.delete(`/admin/notes/${id}`))
  return true
}
