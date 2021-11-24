import Vue from 'vue'
import App from './App'
import store from './store'
import router from './router'
import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import './assets/scss/app.scss'
import VueTheMask from 'vue-the-mask'
import './plugins/axios'


Vue.use(VueTheMask)
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
Vue.config.productionTip = false

Vue.config.errorHandler = function(err, vm, info) {

  // alert('erro componente')

  console.log(`Error: ${err.toString()}\nInfo: ${info}`);
}

Vue.config.warnHandler = function(msg, vm, trace) {
  alert('warning componente')
  console.log(`Warn: ${msg}\nTrace: ${trace}`);

}


window._Vue = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

