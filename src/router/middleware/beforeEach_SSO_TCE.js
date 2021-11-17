import store from '@/store'

export default async (to, from, next) => {


  document.title = `${to.name} - ${process.env.VUE_APP_SISTEMA}`

  if (to.name !== 'login' && !store.getters['auth/hasToken']) {
    
    try {
    
      await store.dispatch('auth/ActionCheckToken')
      // next({ path: to.path })
      next({ name: 'login' })
    
    } catch (err) {

      //tratar a menssagem de erro i
      next({ name: 'login' })
     // reject(err)

    }

  } else {

        var token = to.query.access_token

        if(token){// se existir token entra para setar na store
          
          var user = {name:'Teste da Silva', cpf:'00141340126'}
            store.dispatch('auth/ActionSetToken', token)
            store.dispatch('auth/ActionSetUser', user)

        }

  

        if (to.name === 'login' && store.getters['auth/hasToken']) {

          next({ name: 'home' })

            } 

        else {
      
              next()
    
              }
              

  }
}