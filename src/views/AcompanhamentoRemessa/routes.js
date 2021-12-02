export default [
  {
    path: '/sicapapexterno/AcompanhamentoRemessa',
    name: 'Acompanhamento de Remessa',
    meta: {
      showNavbar: true,
      showDashboard: true,
      icon: 'layout-text-window-reverse', 
      title: 'Acompanhamento de Remessas',
      label: 'Acompanhamento de Remessas'
    },
    component: () => import(/* webpackChunkName: "AcompanhamentoRemessa" */ './pages/AcompanhamentoRemessa'),
  }
]

