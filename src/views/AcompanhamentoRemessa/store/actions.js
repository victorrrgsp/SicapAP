//import * as storage from '../resource/storage'
import * as types from './mutation-types'
import { api } from '@/plugins/axios'


export const ActionFind = ({ commit }) => (

        api.get('externo/acompanhamentoRemessa/all').then(resp => {
        // console.log(resp)
            commit(types.SET_DADOS, resp.data)
        })

)

export const ActionFindByRemessa = ( {commit}, payload) => (

    //console.log(payload.exercicio)

    api.get('externo/acompanhamentoRemessa/getRemessa/'+payload.exercicio+'/'+payload.remessa).then(resp => {
    // console.log(resp)
        commit(types.SET_DADOS, resp.data)
    })

)
export const FindAll = ({ commit }) => (

    api.get('filaProcessamento/processos').then(resp => {
    // console.log(resp)
        commit(types.SET_DADOS, resp.data)
    })

)

export const FindFila = ({ commit }) => (

    api.get('filaProcessamento/fila').then(resp => {
    // console.log(resp)
        commit(types.SET_DADOS, resp.data)
    })

)


