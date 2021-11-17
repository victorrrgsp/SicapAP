<template>
<div id="login">
  <form @submit.prevent="submit()" class="form-login" >
 
    <div class="card">
        <div class="card-header text-center">
          <h3 class="mb-0"><b>Painel de Acesso </b></h3>
        </div>
          <div class="card-body">
            <div >
             <div class="col"> 
                <div class="row text_identificacao">
                 <div class="row">
                    <div class="col-2"><b-icon icon="person-lines-fill"   width="25" height="25" ></b-icon></div>
                    <div class="col-10">Identificação</div>
                  </div>
                </div>
                <div class="row text_aviso">
                  Caso não consiga acesso ente em contato no ramal 0000.
                </div>
             </div>
          </div>
          <div class="botao_entrar">
            <b-button @click="autenticarUsuario" class="btn btn-primary " :disabled='loading'  >
                  <template v-if="loading">Entrando
                    <i class="fa fa-spinner fa-spin"></i>
                  </template>
                  <template v-else>Entrar
                    <i class="fa fa-sign-in-alt"></i>
                </template>
            </b-button>
          </div>
        <hr>
      </div>
    </div>
  </form>

        <b-modal v-model="modalShow" hide-footer>
            <b-embed
              type="iframe"
              aspect="16by9"
              src="https://www.youtube.com/embed/zpOULjyy-n8?rel=0"
              allowfullscreen
            ></b-embed>
        </b-modal>

</div>
</template>

<script>
import { mapActions } from 'vuex'
import { mapState } from 'vuex'
export default {
  name:'Login',
  data: () => ({
      loading: false,
      modalShow:false
  }),

  methods: {

    ...mapActions('auth', ['ActionDoLogin']),

    createItem() {
                  this.modalShow = true;
                  },

   autenticarUsuario(){
this.$router.push({ name: 'home' })


     //  window.location.href="http://172.28.3.31:8181/authentication"

      //window.location.href='https://api.tce.to.gov.br/autenticar/api/oauth2/authorize?response_type=code&client_id=e022c33585e1934882a7257b8a1e79813e0f7ca9&redirect_uri=http://localhost:8080/code&scope=openid&state=af0ifjsldkj&prompt=login&flow=general'
      // this.ActionDoLogin().then(res => {

       //  window.location.href=res.data;

   //   })
         
    }

   },
  computed: {
    ...mapState('auth', ['user']),
    routes () {
      return this.$router.options.routes.filter(route => (
        route.meta && route.meta.showDashboard
      ))
    }
  }
}
</script>

<style lang="scss" scoped>

      .form-login {
           height: 70vh;
           display: flex;
           align-items: center;
           justify-content: center;
        
         h1{
            font-size:18pt;
          }

        .card {
          width:30%;
          color: var(--azul-tce);
          box-shadow: 0 2px 4px #00000033;
        }
    
      .text_aviso{
    
        color:var(--cinza-escuro-tce);
        display: block;
        margin-block-start: 1em;
        margin-block-end: 1em;
        margin-inline-start: 0px;
        font-size: 0.8rem;

      }

      .text_identificacao{
         margin-block-start: 1em;
        margin-block-end: 1em;
        margin-inline-start: 0px;
        color:var(--dark-medium);
        flex: 1 1 100%;
        font-size: 1.2em;
        font-weight: 500;

      }

      .botao_entrar{
         margin-top: 3em;
    
         align-items: center;
         display: flex;
         align-items: center;
         justify-content: center;
      }
  }
  

</style>