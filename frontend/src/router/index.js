import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import NoteDetailView from '../views/NoteDetailView.vue'
import NoteEditorView from '../views/NoteEditorView.vue'
import ProfileView from '../views/ProfileView.vue'
import FavoritesView from '../views/FavoritesView.vue'
import AdminLayout from '../views/AdminLayout.vue'
import AdminDashboardView from '../views/AdminDashboardView.vue'
import AdminNotesView from '../views/AdminNotesView.vue'
import { resolveRouteGuard } from './guard'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', component: HomeView },
    { path: '/login', component: LoginView, meta: { guest: true } },
    { path: '/register', component: RegisterView, meta: { guest: true } },
    { path: '/notes/create', component: NoteEditorView, meta: { requiresAuth: true } },
    { path: '/notes/:id/edit', component: NoteEditorView, meta: { requiresAuth: true } },
    { path: '/notes/:id', component: NoteDetailView },
    { path: '/users/:id', component: ProfileView },
    { path: '/profile', component: ProfileView, meta: { requiresAuth: true } },
    { path: '/favorites', component: FavoritesView, meta: { requiresAuth: true } },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, admin: true },
      children: [
        { path: '', component: AdminDashboardView },
        { path: 'notes', component: AdminNotesView }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const loggedIn = Boolean(localStorage.getItem('campus_token'))
  const user = readStoredUser()
  return resolveRouteGuard(to, loggedIn, user?.role)
})

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('campus_user') || 'null')
  } catch {
    return null
  }
}

export default router
