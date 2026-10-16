export function resolveApiMode(value = '') {
  return String(value).toLowerCase() === 'real' ? 'real' : 'demo'
}

export function isUnavailableError(error) {
  return error?.kind === 'unavailable' || error?.code === 'FEATURE_UNAVAILABLE'
}
