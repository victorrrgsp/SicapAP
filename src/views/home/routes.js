  export default [
    {
      path: '/sicapapexterno/',
      name: 'home',
      component: () => import(/* webpackChunkName: "home" */ './Home'),
      meta: {
        showDashboard: false,
        showNavbar: true,
        icon: 'door-open', 
        title: 'Home',
        label: 'Home'
      },
   }
  ]