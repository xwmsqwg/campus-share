import { withNoteAuthor } from './normalize.js'

// 给笔记补充作者昵称和头像；同一份缓存可在多个列表请求之间复用。
export async function enrichNoteAuthors(notes = [], loadProfile, cache = new Map()) {
  const records = Array.isArray(notes) ? notes : []
  const userIds = [...new Set(records.map((note) => Number(note.userId)).filter((id) => Number.isInteger(id) && id > 0))]
  const profiles = new Map()

  await Promise.all(userIds.map(async (userId) => {
    if (cache.has(userId)) {
      profiles.set(userId, cache.get(userId))
      return
    }
    try {
      const profile = await loadProfile(userId)
      if (profile) {
        cache.set(userId, profile)
        profiles.set(userId, profile)
      }
    } catch {
      // 单个用户资料失败时保留原笔记，不影响整个列表。
    }
  }))

  return records.map((note) => {
    const profile = profiles.get(Number(note.userId))
    return profile ? withNoteAuthor(note, profile) : note
  })
}
