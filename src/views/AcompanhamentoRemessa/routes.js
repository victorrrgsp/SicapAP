export default [
  {
    path: '/sicapapexterno/AcompanhamentoRemessa',
    name: 'acompanhamento remessa',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'layout-text-window-reverse', 
      title: 'AcompanhamentoRemessa',
      label: 'acompanhamento de remessas'
    },
    component: () => import(/* webpackChunkName: "AcompanhamentoRemessa" */ './pages/AcompanhamentoRemessa'),
  }
]

