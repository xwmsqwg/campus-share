import test from 'node:test'
import assert from 'node:assert/strict'
import { fetchNotes, updateCurrentUser } from './mock.js'
import { getUsers } from '../mock/data.js'

function createLocalStorage() {
  const values = new Map()
  return {
    getItem: (key) => values.get(key) ?? null,
    setItem: (key, value) => values.set(key, String(value)),
    removeItem: (key) => values.delete(key)
  }
}

test('fetchNotes returns records and pagination metadata in demo mode', async () => {
  globalThis.localStorage = createLocalStorage()
  const page = await fetchNotes('', 2, 1)
  assert.equal(page.records.length, 1)
  assert.equal(page.total, 3)
  assert.equal(page.page, 2)
  assert.equal(page.size, 1)
})

test('updateCurrentUser saves editable profile fields without changing protected fields', async () => {
  globalThis.localStorage = createLocalStorage()
  const user = getUsers()[0]
  localStorage.setItem('campus_user', JSON.stringify(user))

  await updateCurrentUser({ nickname: '新昵称', gender: 2, profile: '更新后的简介', image: 'https://example.com/avatar.jpg' })

  const saved = getUsers().find((item) => item.id === user.id)
  assert.equal(saved.nickname, '新昵称')
  assert.equal(saved.gender, 2)
  assert.equal(saved.profile, '更新后的简介')
  assert.equal(saved.image, 'https://example.com/avatar.jpg')
  assert.equal(saved.username, user.username)
  assert.equal(saved.role, user.role)
})
