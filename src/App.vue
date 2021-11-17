<template>
  <div id="app">
        <layout-navbar />
        <layout-body v-if="showNavbar" />
        <div class="body" > 
          <base-spinner />
          <router-view/>
        </div>  
  </div>
</template>

<script>
import LayoutBody from './components/layout/LayoutBody'
import LayoutNavbar from './components/layout/LayoutNavbar'
import BaseSpinner from './components/global/BaseSpinner'

    export default {
          name: 'App',
          components: {
            LayoutNavbar,
            LayoutBody,
            BaseSpinner
          },
          computed: {
            showNavbar () {
             return this.$route.name !== 'login'
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
      margin-top: 6px;
      left: 320px;
      right: 100px;
    }

}
</style>
