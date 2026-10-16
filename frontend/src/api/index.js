import * as real from './real'
import * as mock from './mock'
import { isUnavailableError, resolveApiMode } from './mode'

export const apiMode = resolveApiMode(import.meta.env.VITE_API_MODE)
export const useMock = apiMode === 'demo'
export { isUnavailableError }
const source = useMock ? mock : real

export const login = (...args) => source.login(...args)
export const register = (...args) => source.register(...args)
export const getCurrentUser = (...args) => source.getCurrentUser(...args)
export const updateCurrentUser = (...args) => source.updateCurrentUser(...args)
export const uploadAvatar = (...args) => source.uploadAvatar(...args)
export const fetchUserProfile = (...args) => source.fetchUserProfile(...args)
export const logout = (...args) => source.logout(...args)
export const fetchNotes = (...args) => source.fetchNotes(...args)
export const fetchNote = (...args) => source.fetchNote(...args)
export const createNote = (...args) => source.createNote(...args)
export const updateNote = (...args) => source.updateNote(...args)
export const deleteNote = (...args) => source.deleteNote(...args)
export const fetchUserNotes = (...args) => source.fetchUserNotes(...args)
export const fetchComments = (...args) => source.fetchComments(...args)
export const createComment = (...args) => source.createComment(...args)
export const deleteComment = (...args) => source.deleteComment(...args)
export const likeNote = (...args) => source.likeNote(...args)
export const unlikeNote = (...args) => source.unlikeNote(...args)
export const getLikeStatus = (...args) => source.getLikeStatus(...args)
export const favoriteNote = (...args) => source.favoriteNote(...args)
export const unfavoriteNote = (...args) => source.unfavoriteNote(...args)
export const getFavoriteStatus = (...args) => source.getFavoriteStatus(...args)
export const fetchMyFavorites = (...args) => source.fetchMyFavorites(...args)
export const uploadNoteImage = (...args) => source.uploadNoteImage(...args)
export const fetchAdminStatistics = (...args) => source.fetchAdminStatistics(...args)
export const fetchAdminNotes = (...args) => source.fetchAdminNotes(...args)
export const deleteAdminNote = (...args) => source.deleteAdminNote(...args)
