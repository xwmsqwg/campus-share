export function normalizeUser(user = {}) {
  return {
    ...user,
    id: user.id,
    username: user.username,
    nickname: user.nickname || user.username,
    avatar: user.avatar ?? user.image ?? '',
    bio: user.bio ?? user.profile ?? '',
    role: user.role || 'user'
  }
}

export function normalizeNote(note = {}) {
  const images = Array.isArray(note.images)
    ? note.images.map((image) => ({
      ...image,
      imageUrl: image.imageUrl ?? image.image_url ?? image.url ?? ''
    })).filter((image) => image.imageUrl)
    : []
  return {
    ...note,
    id: note.id,
    userId: note.userId ?? note.user_id,
    title: note.title || '',
    summary: note.summary || '',
    content: note.content ?? note.context ?? '',
    images,
    cover: note.cover ?? note.coverUrl ?? note.cover_url ?? images[0]?.imageUrl ?? '',
    author: note.author ?? note.nickname ?? note.user?.nickname ?? '',
    avatar: note.avatar ?? note.image ?? note.user?.image ?? '',
    views: note.views ?? note.viewCount ?? note.view_count ?? 0,
    likes: note.likes ?? note.likeCount ?? 0,
    createdAt: note.createdAt ?? note.created_at ?? note.createTime ?? '',
    tags: note.tags || [],
    status: note.status || 'published'
  }
}

export function withNoteAuthor(note = {}, profile = {}) {
  return {
    ...note,
    author: note.author || profile.nickname || '',
    avatar: note.avatar || profile.avatar || ''
  }
}

export function normalizeComment(comment = {}) {
  return {
    ...comment,
    noteId: comment.noteId ?? comment.note_id,
    userId: comment.userId ?? comment.user_id,
    nickname: comment.nickname ?? comment.user?.nickname ?? '',
    image: comment.image ?? comment.avatar ?? comment.user?.image ?? '',
    createTime: comment.createTime ?? comment.create_time ?? '',
    content: comment.content || ''
  }
}

export function normalizeLikeStatus(status = {}, fallbackCount = 0) {
  status = status || {}
  const rawLiked = status.liked ?? status.isLiked ?? status.like
  const rawCount = status.likeCount ?? status.like_count ?? status.likes ?? fallbackCount
  return {
    liked: rawLiked === true || rawLiked === 1 || rawLiked === '1' || rawLiked === 'true',
    likeCount: Number.isFinite(Number(rawCount)) ? Number(rawCount) : Number(fallbackCount) || 0
  }
}

export function normalizeFavoriteStatus(status = {}) {
  status = status || {}
  const rawFavorited = status.favorited ?? status.favorite ?? status.isFavorite ?? status.is_favorite
  return {
    favorited: rawFavorited === true || rawFavorited === 1 || rawFavorited === '1' || rawFavorited === 'true'
  }
}

export function normalizeFavoritePage(page = {}) {
  const data = page || {}
  return {
    records: (data.records || []).map(normalizeNote),
    total: Number(data.total || 0),
    page: Number(data.page || 1),
    size: Number(data.size || 9)
  }
}

export function unwrapResponse(response) {
  const body = response?.code !== undefined ? response : (response?.data ?? response)
  if (body?.code !== 200) throw new Error(body?.message || '请求失败')
  return body.data
}

export function unwrapPage(response) {
  const page = unwrapResponse(response) || {}
  return {
    ...page,
    records: Array.isArray(page) ? page : (page.records || [])
  }
}
