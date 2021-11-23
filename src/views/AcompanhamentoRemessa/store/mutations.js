import * as types from './mutation-types'

export default {
  [types.SET_DADOS] (state, payload) {
    state.tableData = payload
  }
}