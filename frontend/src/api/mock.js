import {
  getComments,
  getCurrentUser as getStoredUser,
  getFavorites,
  getLikes,
  getNotes,
  getUsers,
  saveComments,
  saveFavorites,
  saveLikes,
  saveNotes,
  saveUsers
} from '../mock/data.js'

const wait = (value, delay = 160) => new Promise((resolve) => setTimeout(() => resolve(value), delay))
const defaultCover = 'https://images.unsplash.com/photo-1519452575417-564c1401ecc0?auto=format&fit=crop&w=1200&q=85'

function error(message, kind = 'error') {
  const result = new Error(message)
  result.kind = kind
  return result
}

function currentUser() {
  const user = getStoredUser()
  if (!user) throw error('请先登录后继续', 'auth')
  return user
}

function publicUser(user) {
  if (!user) return null
  const { password, ...safeUser } = user
  return { ...safeUser, avatar: safeUser.avatar ?? safeUser.image ?? '', bio: safeUser.bio ?? safeUser.profile ?? '' }
}

function findNote(id) { return getNotes().find((note) => note.id === Number(id)) }

function fileToDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = () => reject(error('图片读取失败'))
    reader.readAsDataURL(file)
  })
}

export async function login({ username, password }) {
  if (!username || !password) throw error('请输入用户名和密码', 'validation')
  const user = getUsers().find((item) => item.username === username && item.password === password)
  if (!user) throw error('用户名或密码错误', 'auth')
  const safeUser = publicUser(user)
  localStorage.setItem('campus_token', `demo-jwt-${user.id}-${Date.now()}`)
  localStorage.setItem('campus_user', JSON.stringify(safeUser))
  return wait(safeUser)
}

export async function register(payload) {
  const username = payload.username?.trim()
  const nickname = payload.nickname?.trim()
  if (!username || !payload.password || !nickname) throw error('请填写完整注册信息', 'validation')
  if (getUsers().some((user) => user.username === username)) throw error('用户名已存在', 'validation')
  const user = { id: Date.now(), username, password: payload.password, nickname, gender: payload.gender ?? 0, image: payload.image || '', profile: payload.profile?.trim() || '', role: 'user' }
  saveUsers([...getUsers(), user])
  return wait({ message: '注册成功，请登录' })
}

export async function getCurrentUser() { return wait(publicUser(getStoredUser())) }
export async function updateCurrentUser(payload) {
  const user = getStoredUser()
  if (!user) throw error('请先登录', 'auth')
  const users = getUsers()
  const index = users.findIndex((item) => Number(item.id) === Number(user.id))
  if (index < 0) throw error('用户不存在', 'not-found')
  users[index] = { ...users[index], ...payload }
  saveUsers(users)
  localStorage.setItem('campus_user', JSON.stringify(publicUser(users[index])))
  return wait(true)
}
export async function uploadAvatar(file) {
  const user = currentUser()
  if (!file || !file.type.startsWith('image/')) throw error('请选择图片文件', 'validation')
  if (file.size > 5 * 1024 * 1024) throw error('头像不能超过 5MB', 'validation')
  const image = await fileToDataUrl(file)
  const users = getUsers()
  const index = users.findIndex((item) => Number(item.id) === Number(user.id))
  if (index < 0) throw error('用户不存在', 'not-found')
  users[index] = { ...users[index], image }
  saveUsers(users)
  localStorage.setItem('campus_user', JSON.stringify(publicUser(users[index])))
  return wait({ image })
}
export async function fetchUserProfile(userId) {
  const user = getUsers().find((item) => item.id === Number(userId))
  if (!user) throw error('用户不存在', 'not-found')
  return wait(publicUser(user))
}
export async function logout() {
  localStorage.removeItem('campus_token')
  localStorage.removeItem('campus_user')
  return wait(true)
}

export async function fetchNotes(keyword = '', page = 1, size = 9) {
  const query = keyword.trim().toLowerCase()
  const matched = getNotes().filter((note) => note.status === 'published' && (!query || `${note.title}${note.summary}${note.content}${(note.tags || []).join('')}`.toLowerCase().includes(query)))
  const start = (page - 1) * size
  return wait({ records: matched.slice(start, start + size), total: matched.length, page, size })
}

export async function fetchNote(id) {
  const notes = getNotes()
  const note = notes.find((item) => item.id === Number(id))
  if (!note) throw error('笔记不存在', 'not-found')
  note.views = (note.views || 0) + 1
  saveNotes(notes)
  return wait(note)
}

export async function createNote(payload) {
  const user = currentUser()
  const note = { id: Date.now(), userId: user.id, title: payload.title.trim(), summary: payload.summary.trim(), content: payload.content.trim(), cover: payload.cover || defaultCover, author: user.nickname, avatar: user.avatar, createdAt: '刚刚', likes: 0, views: 0, tags: payload.tags || [], status: 'published' }
  saveNotes([note, ...getNotes()])
  return wait(note)
}

export async function updateNote(id, payload) {
  const user = currentUser()
  const notes = getNotes()
  const note = notes.find((item) => item.id === Number(id))
  if (!note) throw error('笔记不存在', 'not-found')
  if (note.userId !== user.id) throw error('只能编辑自己的笔记', 'forbidden')
  Object.assign(note, { title: payload.title.trim(), summary: payload.summary.trim(), content: payload.content.trim(), cover: payload.cover || note.cover })
  saveNotes(notes)
  return wait(note)
}

export async function deleteNote(id) {
  const ids = Array.isArray(id) ? id.map(Number) : [Number(id)]
  const user = currentUser()
  const notes = getNotes()
  const selectedNotes = notes.filter((item) => ids.includes(Number(item.id)))
  if (selectedNotes.length === 0) throw error('笔记不存在', 'not-found')
  if (selectedNotes.some((item) => item.userId !== user.id)) throw error('只能删除自己的笔记', 'forbidden')
  saveNotes(notes.filter((item) => !ids.includes(Number(item.id))))
  return wait(true)
}

export async function fetchUserNotes(userId, page = 1, size = 10) {
  const records = getNotes().filter((note) => note.userId === Number(userId) && note.status === 'published')
  const start = (page - 1) * size
  return wait({ records: records.slice(start, start + size), total: records.length, page, size })
}

export async function fetchComments(noteId) { return wait(getComments().filter((comment) => comment.noteId === Number(noteId) && comment.status !== 'deleted')) }

export async function createComment(noteId, content) {
  const user = currentUser()
  if (!content?.trim()) throw error('请输入评论内容', 'validation')
  const comment = { id: Date.now(), noteId: Number(noteId), userId: user.id, nickname: user.nickname, image: user.avatar, content: content.trim(), status: 'normal', createTime: '刚刚' }
  saveComments([comment, ...getComments()])
  return wait(comment)
}

export async function deleteComment(id) {
  const user = currentUser()
  const comment = getComments().find((item) => item.id === Number(id))
  if (!comment) throw error('评论不存在', 'not-found')
  if (comment.userId !== user.id) throw error('只能删除自己的评论', 'forbidden')
  saveComments(getComments().filter((item) => item.id !== Number(id)))
  return wait(true)
}

export async function getLikeStatus(noteId) {
  const user = currentUser()
  const likes = getLikes()
  const note = findNote(noteId)
  return wait({ liked: likes.some((item) => item.userId === user.id && item.noteId === Number(noteId)), likeCount: (note?.likes || 0) + likes.filter((item) => item.noteId === Number(noteId)).length })
}

export async function likeNote(noteId) {
  const user = currentUser()
  const likes = getLikes()
  if (!likes.some((item) => item.userId === user.id && item.noteId === Number(noteId))) likes.push({ id: Date.now(), userId: user.id, noteId: Number(noteId) })
  saveLikes(likes)
  return getLikeStatus(noteId)
}

export async function unlikeNote(noteId) {
  const user = currentUser()
  saveLikes(getLikes().filter((item) => !(item.userId === user.id && item.noteId === Number(noteId))))
  return getLikeStatus(noteId)
}

export async function getFavoriteStatus(noteId) {
  const user = currentUser()
  return wait({ favorited: getFavorites().some((item) => item.userId === user.id && item.noteId === Number(noteId)) })
}

export async function favoriteNote(noteId) {
  const user = currentUser()
  const favorites = getFavorites()
  if (!favorites.some((item) => item.userId === user.id && item.noteId === Number(noteId))) favorites.push({ id: Date.now(), userId: user.id, noteId: Number(noteId) })
  saveFavorites(favorites)
  return getFavoriteStatus(noteId)
}

export async function unfavoriteNote(noteId) {
  const user = currentUser()
  saveFavorites(getFavorites().filter((item) => !(item.userId === user.id && item.noteId === Number(noteId))))
  return getFavoriteStatus(noteId)
}

export async function fetchMyFavorites(page = 1, size = 9) {
  const user = currentUser()
  const favoriteIds = new Set(getFavorites().filter((item) => item.userId === user.id).map((item) => Number(item.noteId)))
  const records = getNotes().filter((note) => favoriteIds.has(Number(note.id)) && note.status === 'published')
  const start = (page - 1) * size
  return wait({ records: records.slice(start, start + size), total: records.length, page, size })
}

export async function uploadNoteImage(noteId, file) {
  const user = currentUser()
  if (!file || !file.type.startsWith('image/')) throw error('请选择图片文件', 'validation')
  if (file.size > 5 * 1024 * 1024) throw error('图片不能超过 5MB', 'validation')
  const note = findNote(noteId)
  if (!note || note.userId !== user.id) throw error('只能为自己的笔记上传图片', 'forbidden')
  const image = await fileToDataUrl(file)
  const imageRecord = { id: `${Date.now()}-${Math.random()}`, imageUrl: image }
  if (!note.images?.length) {
    note.images = [imageRecord]
    note.cover = image
  } else {
    note.images.push(imageRecord)
  }
  saveNotes(getNotes())
  return wait({ imageUrl: image })
}

export async function fetchAdminStatistics() {
  throw error('管理员后台需要连接真实后端', 'unavailable')
}

export async function fetchAdminNotes() {
  throw error('管理员后台需要连接真实后端', 'unavailable')
}

export async function deleteAdminNote() {
  throw error('管理员后台需要连接真实后端', 'unavailable')
}
