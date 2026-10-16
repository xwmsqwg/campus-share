import test from 'node:test'
import assert from 'node:assert/strict'
import { normalizeLikeStatus } from './like.js'

test('normalizes like status and keeps the note count fallback', () => {
  assert.deepEqual(normalizeLikeStatus({ liked: true, likeCount: 7 }, 3), {
    liked: true,
    likeCount: 7
  })
  assert.deepEqual(normalizeLikeStatus({ liked: false }, 3), {
    liked: false,
    likeCount: 3
  })
})
