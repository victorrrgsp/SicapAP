//import * as storage from '../resource/storage'
import * as types from './mutation-types'
import {api} from '@/plugins/axios'


export const ActionFind = ({ commit }, payload) => (
        api.get('/externo/acompanhamentoRemessa/all/'+payload.exercicio+'/'+payload.remessa).then(resp => {
            commit(types.SET_DADOS, resp.data)
        })
)
export const ActionFindExercicio = ( {commit}) => (
    api.get('/exercicio').then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)
export const ActionFindByRemessa = ( {commit}, payload) => (
    api.get('/externo/acompanhamentoRemessa/getRemessa/'+payload.exercicio+'/'+payload.remessa).then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)

export const ActionFindByExercicio = ( {commit}, payload) => (
    api.get('/externo/acompanhamentoRemessa/getExercicio/'+payload.exercicio+'/'+payload.remessa).then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)



