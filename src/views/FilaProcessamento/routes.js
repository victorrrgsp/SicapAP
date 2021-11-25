export default [
  {
    path: '/sicapapexterno/FilaProcessamento',
    name: 'filaprocessamento',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'list-nested', 
      title: 'FilaProcessamento',
      label: 'Fila Processamento'
    },
    component: () => import(/* webpackChunkName: "FilaProcessamento" */ './pages/FilaProcessamento'),
  }
]