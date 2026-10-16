import test from 'node:test'
import assert from 'node:assert/strict'
import { describeApiError } from './status.js'

test('describes standard API error status codes', () => {
  assert.deepEqual(describeApiError({ response: { status: 400, data: { message: '参数错误' } } }), {
    kind: 'error',
    message: '参数错误'
  })
  assert.deepEqual(describeApiError({ response: { status: 401, data: { message: '未登录' } } }), {
    kind: 'auth',
    message: '登录状态已失效，请重新登录。'
  })
  assert.deepEqual(describeApiError({ response: { status: 403, data: { message: '无权限' } } }), {
    kind: 'forbidden',
    message: '没有权限执行此操作。'
  })
  assert.deepEqual(describeApiError({ response: { status: 404, data: { message: '资源不存在' } } }), {
    kind: 'not-found',
    message: '请求的资源不存在。'
  })
  assert.deepEqual(describeApiError({ response: { status: 409, data: { message: '用户名已存在' } } }), {
    kind: 'conflict',
    message: '用户名已存在'
  })
  assert.deepEqual(describeApiError({ response: { status: 500, data: { message: '服务器错误' } } }), {
    kind: 'error',
    message: '服务器错误'
  })
})
