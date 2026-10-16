import test from 'node:test'
import assert from 'node:assert/strict'
import { isUnavailableError, resolveApiMode } from './mode.js'

test('uses demo mode unless real mode is explicitly selected', () => {
  assert.equal(resolveApiMode(), 'demo')
  assert.equal(resolveApiMode('demo'), 'demo')
  assert.equal(resolveApiMode('REAL'), 'real')
})

test('recognizes unavailable feature errors', () => {
  assert.equal(isUnavailableError({ kind: 'unavailable' }), true)
  assert.equal(isUnavailableError({ code: 'FEATURE_UNAVAILABLE' }), true)
  assert.equal(isUnavailableError(new Error('failed')), false)
})
