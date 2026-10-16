export function validateRegisterForm(form) {
  const errors = {}
  const username = form.username?.trim() || ''
  const password = form.password || ''
  const confirm = form.confirm || ''
  const nickname = form.nickname?.trim() || ''

  if (!username) errors.username = '请输入用户名'
  else if (username.length < 2) errors.username = '用户名至少需要 2 个字符'
  if (!password) errors.password = '请输入密码'
  else if (password.length < 8) errors.password = '密码至少需要 8 个字符'
  if (!confirm) errors.confirm = '请再次输入密码'
  else if (confirm !== password) errors.confirm = '两次输入的密码不一致'
  if (!nickname) errors.nickname = '请输入昵称'
  else if (nickname.length < 2) errors.nickname = '昵称至少需要 2 个字符'

  return errors
}
