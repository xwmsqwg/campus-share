export function resolveRouteGuard(to, loggedIn, userRole = '') {
  if (to.meta?.requiresAuth && !loggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta?.admin && String(userRole).toLowerCase() !== 'admin') return '/'
  if (to.meta?.guest && loggedIn) return '/'
  return undefined
}
