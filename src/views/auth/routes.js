export default [
  {
    path: '/login',
    name: 'login',
    meta: {
      showNavbar: false,
      showDashboard: false,
      icon: 'door-open', 
      title: 'Login',
    },
    component: () => import(/* webpackChunkName: "login" */ './pages/Login')
  },
  {
    path: '/logout',
    name: 'logout',
    meta: {
      showNavbar: false,
      showDashboard: false,
      icon: 'door-open', 
      title: 'Logout',
    },
    component: () => import(/* webpackChunkName: "login" */ './pages/Logout')
  }
  // {
  //   path: '/code',
  //   name: 'code',
  //   meta: {
  //     showNavbar: false,
  //     showDashboard: false,
  //     icon: 'door-open', 
  //     title: 'Code',
  //   },
  //   component: () => import(/* webpackChunkName: "login" */ './pages/Code')
  // }
]