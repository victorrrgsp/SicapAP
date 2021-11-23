<template>
<div>
  <b-navbar class="navbar-tce"  type="dark" >
          <div class="container-fluid" >
            <div class="row">
                <div class="col-2">
                    <b-collapse class="header1" id="collapse-1"  is-nav>
                          <b-navbar-brand class="col-s-4">
                              <b-button class="botao-menu" size="sm" v-b-toggle.sidebar-menu >
                                <b-icon icon="list"></b-icon>
                              </b-button>
                              <img src="@/assets/imgs/logo_TCE.jpg" class="img-logo">
                          </b-navbar-brand>
                          <b-navbar-nav class="col-s-8">
                                <div class="col-s-10">
                                  <div class="row fonte_titulo_logo"> Tribunal de Contas</div>
                                  <div class="row fonte_subtitulo_logo"> do Estado do Tocantins</div>
                                </div>
                          </b-navbar-nav>
                    </b-collapse>
              </div>
           <div class="col3-head">
              <b-collapse id="nav-collapse" is-nav>
                  <b-navbar-nav class="ms-left">
                  <div class="row" style="margin-left: 2rem;">
                    <b-nav >
                        <b-icon icon="arrows-fullscreen"  class="img-logo" style="color: #fff" ></b-icon>
                        <b-nav-text style="margin-top: 5px; margin-left: 5px;"><span class="text-white h4">Sicap Atos de Pessoal Público</span></b-nav-text>
                    </b-nav>
                  </div>
                </b-navbar-nav>
              </b-collapse>
          </div>

          <div class="col">
                <b-collapse id="nav-collapse"  is-nav >
                  <b-navbar-nav class="ms-auto ">
                      <b-nav class="teste" >
                        <b-nav-item href="#"></b-nav-item >
                        <b-nav-text style="margin-top: 7px"></b-nav-text>
                      </b-nav>
                    <b-nav-form @submit.prevent="submit()">
                       
                    </b-nav-form>
                  </b-navbar-nav>
                </b-collapse>
          </div>
       </div>
    </div>
  </b-navbar>
</div>
</template>

<script>

import { mapActions, mapState } from 'vuex'

export default {
  computed: {
     ...mapState('auth', ['user']),
    routes () {
      return this.$router.options.routes.filter(route => (
        route.meta && route.meta.showNavbar
      ))
    }
  },
  
  methods: {

    ...mapActions('auth', ['ActionSignOut']),

    async submit () {
      try {
          //  alert('rrwerw')
          await this.ActionSignOut(this.form)

          //if(this.routes[0].name!='home'){//redireciona somente se estiver em alguma pagina internar... caso contrario permanece no login
              this.$router.push({ name: 'login' })
       //   }

          } catch (err) {
            alert('Não foi possível fazer logof')
          }
    }
  }
}
</script>

<style scoped lang="scss">

    .header1{
      background-color: var(--azul-escuro-tce);
      width: 100%;

    }

    .botao-menu{

      margin-left: 5px;
      background-color: var(--azul-escuro-tce);

    }
    .botao-sair{
    
      background-color: var(--vermelho-escuro-tce); 
      margin-left: 1rem;  
      margin-top: 9px;

    }

    .img-logo{
      margin-left: 25px; 
      margin-right: 5px;
      width: 40px;
      height: 50px;

    }

    .navbar-tce {
        background-color: var(--azul-tce);
        box-shadow: 0px 5px 2px var(--vermelho-escuro-tce);
        padding: 0 0 0 0 !important;
        //  height: calc(100vh - 830px);
      
    }
    .fonte_titulo_logo {
      // font-family: "Montserrat" !important;
        font-size: 1.953rem/27.34px !important;
        font-weight: bold;
        color: var(--cinza-ligt-tce) !important;
      // word-spacing: 5px; 
        position: relative;
        top: 5px; 
      // margin-left: 1px;
    }

    .fonte_subtitulo_logo {
        font-size: 13px !important;
        color: var(--cinza-ligt-tce) !important; 
        font-weight: 300;
        position: relative;
    }

    .container-fluid{
        display: flex !important;
        padding: 0 0 0 0 !important;
        width: 100%;
        }

    .row{
        display: flex !important;
        padding: 0 0 0 0 !important;
       width: 100%;
       border-right: 0%;
        
        }

     .col-2{
        width: 342px;
 }
 html{
   border-right: 0%;
 }
/** TRATAR A RESPONSVIDADE */
/* Tablet em retrato a paisagem e desktop */
@media only screen and (min-width: 995px) and (max-width: 1199px) {
  

  .col1-head {width: 18%;}
  .col2-head {width: 57%;}
  .col3-head {width: 30%;}


  .col-s-1 {width: 8.33%;}
  .col-s-2 {width: 16.66%;}
  .col-s-3 {width: 20%;}
  .col-s-4 {width: 33.33%;}
  .col-s-5 {width: 41.66%;}
  .col-s-6 {width: 55%;}
  .col-s-7 {width: 58.33%;}
  .col-s-8 {width: 66.66%;}
  .col-s-9 {width: 75%;}
  .col-s-10 {width: 83.33%;}
  .col-s-11 {width: 91.66%;}
  .col-s-12 {width: 100%;}
  
}

/** desqktop grande */
@media only screen and (min-width: 1200px) {
  
  .col1-head {width: 18%;}
  .col2-head {width: 57%;}
  .col3-head {width: 25%;}
  .col5-head {width: 32%;}
  
  .col-s-1 {width: 8.33%;}
  .col-s-2 {width: 16.66%;}
  .col-s-3 {width: 18%;}
  .col-s-4 {width: 33.33%;}
  .col-s-5 {width: 41.66%;}
  .col-s-6 {width: 64%;}
  .col-s-7 {width: 58.33%;}
  .col-s-8 {width: 66.66%;}
  .col-s-9 {width: 75%;}
  .col-s-10 {width: 83.33%;}
  .col-s-11 {width: 91.66%;}
  .col-s-12 {width: 100%;}
  
}

@media (max-width: 480px) { 
 
 
  .col1-head {width: 18%;}
  .col2-head {width: 57%;}

  .col3-head {display: block;}

  .col{
       display: block;
   }
   .header1{
     display: block;
   }

 
  .col-s-1 {width: 8.33%;}
  .col-s-2 {width: 16.66%;}
  .col-s-3 {width: 20%;}
  .col-s-4 {width: 33.33%;}
  .col-s-5 {width: 41.66%;}
  .col-s-6 {width: 55%;}
  .col-s-7 {width: 58.33%;}
  .col-s-8 {width: 66.66%;}
  .col-s-9 {width: 75%;}
  .col-s-10 {width: 83.33%;}
  .col-s-11 {width: 91.66%;}
  .col-s-12 {width: 100%;}


  
 }

/* Telefones em paisagem a tablet em retrato */
@media  (max-width: 994px) {
  
  .col1-head {width: 18%;}
  .col2-head {width: 57%;}
  .col3-head {display: block;}
 
  



  .col-s-1 {width: 8.33%;}
  .col-s-2 {width: 16.66%;}
  .col-s-3 {width: 20%;}
  .col-s-4 {width: 33.33%;}
  .col-s-5 {width: 41.66%;}
  .col-s-6 {width: 55%;}
  .col-s-7 {width: 58.33%;}
  .col-s-8 {width: 66.66%;}
  .col-s-9 {width: 75%;}
  .col-s-10 {width: 83.33%;}
  .col-s-11 {width: 91.66%;}
  .col-s-12 {width: 100%;}
  
}


</style>