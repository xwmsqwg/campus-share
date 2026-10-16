import test from 'node:test'
import assert from 'node:assert/strict'
import { normalizeFavoriteStatus, normalizeFavoritePage } from './normalize.js'

test('normalizes favorite status fields returned by the backend', () => {
  assert.deepEqual(normalizeFavoriteStatus({ favorited: 1 }), { favorited: true })
  assert.deepEqual(normalizeFavoriteStatus({ favorite: false }), { favorited: false })
})

test('normalizes favorite page records into frontend notes', () => {
  const page = normalizeFavoritePage({
    records: [{ id: 8, context: '收藏的正文', images: [{ imageUrl: '/favorite.jpg' }] }],
    total: 1,
    page: 1,
    size: 9
  })

  assert.equal(page.records[0].content, '收藏的正文')
  assert.equal(page.records[0].cover, '/favorite.jpg')
  assert.equal(page.total, 1)
})
