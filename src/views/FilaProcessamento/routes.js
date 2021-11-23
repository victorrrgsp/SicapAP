export default [
  {
    path: '/sicapapexterno/FilaProcessamento',
    name: 'filaprocessamento',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'layout-text-window-reverse', 
      title: 'FilaProcessamento',
      label: 'Fila Processamento'
    },
    component: () => import(/* webpackChunkName: "FilaProcessamento" */ './pages/FilaProcessamento'),
  }
]