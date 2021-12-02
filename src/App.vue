<template>
  <div id="app">
        <layout-navbar />
        <div class="menu"> <menus v-if="showNavbar" /> </div>
        <div class="body" > 
          <base-spinner />
          <router-view/>
        </div>  
  </div>
</template>

<script>

import Menus from './components/layout/Menus'
import LayoutNavbar from './components/layout/LayoutNavbar'
import BaseSpinner from './components/global/BaseSpinner'

    export default {
          name: 'App',
          components: {
            LayoutNavbar,
            BaseSpinner,
            Menus
          },
          computed: {
            showNavbar () {
             return this.$route.name !== 'home'
            }
          },
          mounted(){
                  setTimeout(() =>{
                    this.$root.$emit('Spinner::hide')
                  }, 300)
          }
    }
</script>

<style lang="scss">
#app {
  
  color: var(--dark-medium);
  background-color: var(--light);

  .navigation-sidebar {
      background-color: var(--light);
  }

  .logo{
      background-color: var(--azul-tce);
      .app-title {
      color: var(--cinza-claro-tce);
      font-size: 20pt;
      margin-top: 10px;
      text-align: center;
    }
  }

  .body{
      position: absolute;
      margin-top: 80px;
      left: 20px;
      right: 20px;
    }

  .menu{
      position: absolute;
      margin-top: 2px;
      left: 20px;
      right: 20px;
    }

}
</style>
