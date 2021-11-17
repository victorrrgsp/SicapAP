import axios from "axios";
import store from '@/store'


// export const http = axios.create({
//   baseURL: `${process.env.VUE_APP_HTTP_AUTH}`,
//   headers: {'Content-type': 'application/x-www-form-urlencoded'}
// })

// export const api = axios.create({
//   baseURL: `${process.env.VUE_APP_HTTP_API}`,
//   headers: {'Content-type': 'application/json'}
// })


export const autenticar = axios.create({

    baseURL: `${process.env.VUE_APP_AUTENTICAR}`,
    headers: {
        'Content-type': 'application/x-www-form-urlencoded', 
        'Accept': 'application/json'
    }

})


autenticar.interceptors.request.use(config=>{
  return config
}, error => Promise.reject(error))


autenticar.interceptors.response.use(config=>{
  return config
}, error => Promise.reject(error))

// //intercepta as requests
// http.interceptors.request.use(config=>{
//   return config
// }, error => Promise.reject(error))


// //intercepta os response api
// http.interceptors.response.use(resp=>{

//   if (resp.status === 401) {//status nao autorizado obs: tratar os demais erros
//     store.dispatch('auth/ActionSignOut')//remove o token do header
//     window._Vue.$router.push({ name: 'login' })//redireciona para o login
//   }

//   return resp
  
// }, error => Promise.reject(error))



// //intercepta os response api
// api.interceptors.response.use(resp=>{

//   if (resp.status === 401) {//status nao autorizado obs: tratar os demais erros
//     store.dispatch('auth/ActionSignOut')//remove o token do header
//     window._Vue.$router.push({ name: 'login' })//redireciona para o login
//   }

//   return resp
// }, error => Promise.reject(error))


//intercepta os response autenticar
autenticar.interceptors.response.use(resp=>{

  if (resp.status === 401) {//status nao autorizado obs: tratar os demais erros
    store.dispatch('auth/ActionSignOut')//remove o token do header
    window._Vue.$router.push({ name: 'login' })//redireciona para o login
  }

  return resp
}, error => Promise.reject(error))



//seta o token no header para as proximas requests
const setBearerToken = access_token => {

  //http.defaults.headers.common['Authorization'] = `Bearer ${access_token}`
  //api.defaults.headers.common['Authorization'] = `Bearer ${access_token}`
  autenticar.defaults.headers.common['Authorization'] = `Bearer ${access_token}`

 }


 export{setBearerToken}