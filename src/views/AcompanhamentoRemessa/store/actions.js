//import * as storage from '../resource/storage'
import * as types from './mutation-types'
import {api} from '@/plugins/axios'


export const ActionFind = ({ commit }) => (
        api.get('/externo/acompanhamentoRemessa/all/').then(resp => {
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
    api.get('/externo/acompanhamentoRemessa/getExercicio/'+ payload.exercicio +'/'+ payload.remessa ).then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)

export const ActionFindByExercicioVigente = ( {commit} ) => (
    api.get('/exercicio/exercicioVigente').then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)

export const ActionFindByRemessaVigente = ( {commit}, payloud) => (
    api.get('/remessa/remessaVigente/'+payloud.exercicio).then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)

export const ActionSituacaoGFIP = ( {commit}, payloud) => (
    api.get('cadastrarGfip/situacao/gfip/' + payloud.exercicio + '/' + payloud.remessa + '/' + payloud.cnpj).then(resp => {
        commit(types.SET_DADOS, resp.data)
    })
)
