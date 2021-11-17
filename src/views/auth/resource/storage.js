import { setBearerToken } from  '@/plugins/axios'

export const setHeaderToken = access_token => setBearerToken(access_token)
export const getLocalToken = () => localStorage.getItem('access_token')
export const deleteLocalToken = () => localStorage.removeItem('access_token')
export const setLocalToken = access_token => localStorage.setItem('access_token', access_token)