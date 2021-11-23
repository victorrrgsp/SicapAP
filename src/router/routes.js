import { routes as home } from '../views/home'
import { routes as auth } from '../views/auth'
import { routes as fila } from '../views/FilaProcessamento'
import { routes as remessas } from '../views/AcompanhamentoRemessa'

export default [
  ...auth,
  ...home,
  ...remessas,
  ...fila
 
]