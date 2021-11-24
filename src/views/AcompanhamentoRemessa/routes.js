export default [
  {
    path: '/sicapapexterno/AcompanhamentoRemessa',
    name: 'acompanhamentoremessa',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'layout-text-window-reverse', 
      title: 'AcompanhamentoRemessa',
      label: 'Remessas'
    },
    component: () => import(/* webpackChunkName: "AcompanhamentoRemessa" */ './pages/AcompanhamentoRemessa'),
  }
]

