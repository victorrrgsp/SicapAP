import axios from "axios";
import store from '@/store'

export const api = axios.create({
  
    // baseURL: `${process.env.VUE_APP_HTTP_API}`,
    baseURL:'https://www.tceto.tc.br/backend',
    headers: {
        'Content-type': 'application/x-www-form-urlencoded', 
        'Accept': 'application/json'
    }
    
})

//intercepta as requests
api.interceptors.request.use(config=>{
  return config
}, error => Promise.reject(error))


// //intercepta os response api
api.interceptors.response.use(resp=>{

  if (resp.status === 401){//status nao autorizado obs: tratar os demais erros
    store.dispatch('auth/ActionSignOut')//remove o token do header
    window._Vue.$router.push({ name: 'login' })//redireciona para o login
  }

  if (resp.status === 400){//status nao autorizado obs: tratar os demais erros
    store.dispatch('auth/ActionSignOut')//remove o token do header
    window._Vue.$router.push({ name: 'login' })//redireciona para o login
  }
  return resp
}, error => Promise.reject(error))

//seta o token no header para as proximas requests
const setBearerToken = access_token => {

  api.defaults.headers.common['Authorization'] = `Bearer ${access_token}`

 }

 export{setBearerToken}