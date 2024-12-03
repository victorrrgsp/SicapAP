// import { routes as home } from '../views/home'
import { routes as auth } from '../views/auth'
import { routes as fila } from '../views/FilaProcessamento'
import { routes as remessas } from '../views/AcompanhamentoRemessa'
import { routes as remessas2021 } from '../views/AcompanhamentoRemessa2021'

export default [
  ...auth,
  // ...home,
  ...remessas,
  ...fila,
  ...remessas2021
 
]