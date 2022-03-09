//import store from '@/store'
//import jwt_decode from "jwt-decode";

export default async (to, from, next) => {
  document.title = `${to.name} - Sicap Atos de Pessoal Público`

  if (to.name !== 'login') {
    
    try {
      // await store.dispatch('auth/ActionCheckToken')
        //next({ name: 'home' })
        
        next()
        
    } catch (err) {
      next({ name: 'login' })
    }

  } else {
          if (to.name === 'login') {
            next({ name: 'login' })
          } else {
            next()
          }
  }
}