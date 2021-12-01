<template>
  <div> 
    <!-- <b-sidebar id="sidebar-menu" v-model="isSidebarOpen" aria-labelledby="sidebar-menu-title" no-header shadow> -->
      <b-sidebar  
      v-model="active"
      id="sidebar-menu"  
      aria-labelledby="sidebar-menu-title" 
      no-header 
      shadow
      >
      <template >
        
        <div class="sidebar-tce" >
           <div class="row" >
                    <div class="col-3" style="margin-top: 5px; margin-bottom: 5px;">
                        <img src="@/assets/imgs/logo_TCE.jpg" style="margin-left: 45px" width="40" height="50"  >         
                    </div>
                    <div class="col-9">
                        <div class="row fonte_titulo_logo">Tribunal de Contas </div>
                        <div class="row fonte_subtitulo_logo"> do Estado do Tocantins</div>
                    </div>   
           </div>
        </div>

        <div class="p-3">
          <div class="d-flex aling-items-center flex-column"></div>
             <nav class="mb-3">
                <b-nav vertical>
                        <router-link
                            exact
                            :key="i"
                            class="nav-link"
                            exact-active-class="active"
                            :to="{ name: router.name }"
                            v-for="(router, i) in routes">
                            <b-icon :icon="router.meta.icon"></b-icon>
                            {{ router.meta.title }} 
                        </router-link>
                </b-nav>
           </nav>
           <br/>
           <br/>
           <!--  -->
          <b-button variant="primary" block @click="closeSidebar" >Fechar</b-button>
        </div>


      </template>
    </b-sidebar>
   
  </div>
</template>
<script>

import { mapActions, mapState } from 'vuex'

export default {

  data(){

      return {

        active:true
     
      }
  },

  computed: {
    
    ...mapState('auth', ['user']),

    routes () {
      return this.$router.options.routes.filter(route => (
        route.meta && route.meta.showNavbar
      ))
    }
  },

beforeMount(){
             
},

  mounted() {
          setTimeout(() =>{
                        this.active = true
                      }, 2.0*1000)
     },

  methods: {
    
    // openSidebar() {
    //     this.isSidebarOpen = false
    //   },
       closeSidebar() {
        this.active = false
      },

    ...mapActions('auth', ['ActionSignOut']),

    async submit () {

      try {

          await this.ActionSignOut(this.form)
          this.$router.push({ name: 'login' })

          } catch (err) {

            alert('Não foi possível fazer logof')

          }

    }
  }
}
</script>
<style scoped lang="scss">
.nav {
    font-family: 'Montserrat', sans-serif;
    font-weight: 800;
    font-size: 1.25rem/17.50px;
    margin-left: -15px;
    width: calc(100% + 30px);
  .nav-link {
    color: var(--cinza-escuro-tce);
    transition: .4s;
    &.active {
      color: var(--azul-tce);
      background-color: transparent;
    }
    &:hover {
      color: var(--dark);
      background-color: var(--light-medium);
    }
  }
}

.sidebar-tce {
    background-color: var(--azul-escuro-tce);
    box-shadow: 0px 5px 2px var(--vermelho-escuro-tce);
    padding: 0 0 0 0 !important;
    width: calc(100% + 2px);
}

.fonte_titulo_logo {
    font-size: 1.953rem/27.34px !important;
    font-weight: bold;
    color: var(--cinza-ligt-tce) !important;
    word-spacing: 5px; 
    position: relative;
    top: 15px; 
    margin-left: 6px;
}

.fonte_subtitulo_logo {
    font-size: 13px !important;
    color: var(--cinza-ligt-tce) !important; 
    font-weight: 300;
    position: relative;
    top: 10px; 
    margin-left: 6px;
}

.row{
    display: flex !important;
    padding: 0 0 0 0 !important;
    width: calc(100% + 10px);
 }


</style>
