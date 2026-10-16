import test from 'node:test'
import assert from 'node:assert/strict'
import { resolveRouteGuard } from './guard.js'

test('redirects guests from protected routes and preserves the destination', () => {
  assert.deepEqual(resolveRouteGuard({ path: '/notes/create', fullPath: '/notes/create', meta: { requiresAuth: true } }, false), {
    path: '/login',
    query: { redirect: '/notes/create' }
  })
})

test('redirects authenticated users away from guest routes', () => {
  assert.equal(resolveRouteGuard({ path: '/login', fullPath: '/login', meta: { guest: true } }, true), '/')
})

test('allows public routes for guests', () => {
  assert.equal(resolveRouteGuard({ path: '/notes/1', fullPath: '/notes/1', meta: {} }, false), undefined)
})
