import test from 'node:test'
import assert from 'node:assert/strict'
import { enrichNoteAuthors } from './noteAuthors.js'

test('loads each author once and reuses the profile cache across note lists', async () => {
  const cache = new Map()
  const loadedUsers = []
  const loadProfile = async (id) => {
    loadedUsers.push(id)
    return { nickname: `同学${id}`, avatar: `avatar-${id}.jpg` }
  }

  const first = await enrichNoteAuthors([
    { id: 1, userId: 23 },
    { id: 2, userId: 23 },
    { id: 3, userId: 24 }
  ], loadProfile, cache)
  const second = await enrichNoteAuthors([{ id: 4, userId: 23 }], loadProfile, cache)

  assert.deepEqual(loadedUsers, [23, 24])
  assert.equal(first[0].author, '同学23')
  assert.equal(first[1].avatar, 'avatar-23.jpg')
  assert.equal(second[0].author, '同学23')
})

test('keeps the original note when loading its author profile fails', async () => {
  const note = { id: 5, userId: 25, title: '仍然显示' }
  const result = await enrichNoteAuthors([note], async () => { throw new Error('资料接口失败') })

  assert.deepEqual(result, [note])
})
