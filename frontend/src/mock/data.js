const seedUsers = [
  { id: 1, username: 'demo', password: '123456', nickname: '林栖', gender: 0, image: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80', profile: '在校园里记录一些值得记住的光。', role: 'user' },
  { id: 2, username: 'weekend', password: '123456', nickname: '周末研究员', gender: 0, image: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&q=80', profile: '慢慢认识这所学校。', role: 'user' }
]

const seedNotes = [
  { id: 1, userId: 1, title: '秋天的图书馆，适合把下午交给自己', summary: '从南门走到旧图书馆，光线落在长桌上。把一整个下午留给阅读，也留给校园里慢下来的风。', content: '从南门走到旧图书馆，光线落在长桌上。把一整个下午留给阅读，也留给校园里慢下来的风。\n\n这里的三楼靠窗位置总是很安静，适合整理课堂笔记，也适合什么都不做。', cover: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1200&q=85', author: '林栖', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80', createdAt: '今天 14:20', likes: 68, views: 312, tags: ['校园日常', '阅读'], status: 'published' },
  { id: 2, userId: 2, title: '一份给新生的校园散步路线', summary: '不赶课的时候，沿着湖边、实验楼和小操场慢慢走一圈，刚好认识这所学校。', content: '不赶课的时候，沿着湖边、实验楼和小操场慢慢走一圈，刚好认识这所学校。\n\n建议傍晚出发，最后在生活区买一杯热饮。', cover: 'https://images.unsplash.com/photo-1564981797816-1043664bf78d?auto=format&fit=crop&w=1200&q=85', author: '周末研究员', avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&q=80', createdAt: '昨天 18:05', likes: 42, views: 188, tags: ['新生指南', '散步'], status: 'published' },
  { id: 3, userId: 1, title: '实验室下班后的十五分钟', summary: '把电脑合上以后，去操场看一会儿天色，很多问题会在路上变得简单。', content: '把电脑合上以后，去操场看一会儿天色，很多问题会在路上变得简单。', cover: 'https://images.unsplash.com/photo-1498243691581-b145c3f54a5a?auto=format&fit=crop&w=1200&q=85', author: '林栖', avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&q=80', createdAt: '9月28日', likes: 35, views: 147, tags: ['实验室', '生活'], status: 'published' }
]

export function getNotes() {
  const saved = localStorage.getItem('campus_notes')
  return saved ? JSON.parse(saved) : seedNotes
}
export function saveNotes(notes) { localStorage.setItem('campus_notes', JSON.stringify(notes)) }
export function getUsers() {
  const saved = localStorage.getItem('campus_users')
  return saved ? JSON.parse(saved) : seedUsers
}
export function saveUsers(users) { localStorage.setItem('campus_users', JSON.stringify(users)) }
export function getCurrentUser() {
  return JSON.parse(localStorage.getItem('campus_user') || 'null')
}
export function getComments() { return JSON.parse(localStorage.getItem('campus_comments') || '[]') }
export function saveComments(comments) { localStorage.setItem('campus_comments', JSON.stringify(comments)) }
export function getLikes() { return JSON.parse(localStorage.getItem('campus_likes') || '[]') }
export function saveLikes(likes) { localStorage.setItem('campus_likes', JSON.stringify(likes)) }
export function getFavorites() { return JSON.parse(localStorage.getItem('campus_favorites') || '[]') }
export function saveFavorites(favorites) { localStorage.setItem('campus_favorites', JSON.stringify(favorites)) }
export function seedStorage() {
  if (!localStorage.getItem('campus_notes')) saveNotes(seedNotes)
  if (!localStorage.getItem('campus_users')) saveUsers(seedUsers)
  if (!localStorage.getItem('campus_comments')) saveComments([])
  if (!localStorage.getItem('campus_likes')) saveLikes([])
  if (!localStorage.getItem('campus_favorites')) saveFavorites([])
}
