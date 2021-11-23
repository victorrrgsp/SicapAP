import * as storage from '../resource/storage'
import * as types from './mutation-types'
//import jwt_decode from "jwt-decode";
//import { http } from '@/plugins/axios'

// export const ActionDoLogin = ({ dispatch }, payload) => {
  
//     return http.post('token',payload).then(res => {
//       var decodedPayload = jwt_decode(res.data.access_token, { payload: true })
//       dispatch('ActionSetUser', decodedPayload)
//       dispatch('ActionSetToken', res.data.access_token)
//   })

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

//export const ActionLoadSession = ({ dispatch }) => {

  // http.get('userinfo').then(res => {
  //         const data =res.data

  //         console.log(reject)

  //         // console.log(data)
  //       //  if(JSON.stringify(data) === '{}'){
  //       //   console.log('akii')
  //       //  }
  //         dispatch('ActionSetUser', data)
  //        // return Promise.resolve()
  //     }).catch((err) => {
  //         console.log(err)
  //      // dispatch('ActionSignOut')
  //    //   return Promise.reject(err)
  //     })

//  return http.get('userinfo').then((res => {

//     const data =res.data

//     // console.log(data)
//   //  if(JSON.stringify(data) === '{}'){
     
//   //  }
//     dispatch('ActionSetUser', data)
//    // return Promise.resolve()
// }),(rej) => {

//   console.log(rej)

// })
//}



// export const RefreshToken = ({ dispatch}) =>{

//   http.get('callback/refreshtoken').then(res => {
    
//     const data2 =res.data
//     dispatch('ActionSetUser', data2)
  
//   })


// }







export const ActionSignOut = ({ dispatch }) => {

      dispatch('finalizarSessao')
      storage.setHeaderToken('')
      storage.deleteLocalToken()
      dispatch('ActionSetUser', {})
      dispatch('ActionSetToken', '')

}