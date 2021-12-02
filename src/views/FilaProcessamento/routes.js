export default [
  {
    path: '/sicapapexterno/FilaProcessamento',
    name: 'filaprocessamento',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'list-nested', 
      title: 'Fila de Processamento',
      label: 'Fila de Processamento'
    },
    component: () => import(/* webpackChunkName: "FilaProcessamento" */ './pages/FilaProcessamento'),
  }
]