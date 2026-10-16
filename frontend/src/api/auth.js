export function formatTokenHeader(token) {
  return token ? `Bearer ${token}` : undefined
}

export function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('campus_user') || 'null')
  } catch {
    return null
  }
}
