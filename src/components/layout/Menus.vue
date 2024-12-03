<template >
  <div class="padding">
    <nav class="mb-1">
      <b-nav horizontal>
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
    font-size: 14.50px;
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
