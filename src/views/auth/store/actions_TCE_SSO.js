import * as storage from '../resource/storage'
import * as types from './mutation-types'
import {  autenticar } from '@/plugins/axios'

export const ActionDoLogin = () => {

   //var state= geraStringAleatoria(10);
   //return autenticar.get('authentication/createurl')
   return autenticar.get('/teste2')
}

// export const geraStringAleatoria = (tamanho) => {
//   var stringAleatoria = '';
//   var caracteres = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
//   for (var i = 0; i < tamanho; i++) {
//       stringAleatoria += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
//   }
//   return stringAleatoria;
// }


export const ActionSetUser = ({ commit }, payload) => {
    commit(types.SET_USER, payload)
}

export const ActionSetToken = ({ commit }, payload) => {

    storage.setLocalToken(payload)
    storage.setHeaderToken(payload)
    commit(types.SET_TOKEN, payload)
}

export const ActionCheckToken = ({ dispatch, state }) => {

  console.log(state.access_token)

  if (state.access_token) {
    return Promise.resolve(state.access_token)
  }

  const access_token = storage.getLocalToken()

  if (!access_token) {
    return Promise.reject(new Error('Token Inválido'))
  }

  dispatch('ActionSetToken', access_token)
  return dispatch('ActionLoadSession')
}

export const ActionLoadSession = ({ dispatch }) => {

  autenticar.get('userinfo').then(res => {
      const data =res.data
      dispatch('ActionSetUser', data)
      return Promise.resolve()

      }).catch((err) => {
        dispatch('ActionSignOut')
        return Promise.reject(err)
      })

}

export const ActionSignOut = ({ dispatch }) => {
  storage.setHeaderToken('')
  storage.deleteLocalToken()
  dispatch('ActionSetUser', {})
  dispatch('ActionSetToken', '')
}