export default [
    {
        path: '/sicapapexterno/AcompanhamentoRemessas2021',
        name: 'acompanhamentoRemessas2021',
        meta: {
            showNavbar: true,
            showDashboard: false,
            icon: 'layout-text-window-reverse',
            title: 'Acompanhamento de Remessas (2021)',
            label: 'Acompanhamento de Remessas (2021)'
        },
        beforeEnter() {
           location.href = 'https://app.tce.to.gov.br/sicap/ap/externo/app/index.php' 
        }
    }
]