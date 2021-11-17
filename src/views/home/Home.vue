<template>
  <div id="home">
    <div class="user-info"></div>
    <div class="home-links">
      <router-link
        :key="i"
        class="home-link"
        :to="{ name: route.name }"
        v-for="(route, i) in routes"
      >
        <b-icon class="h1 mb-2" :icon="route.meta.icon" ></b-icon>
        <span class="h3 mb-2">{{ route.meta.label }}</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'Home',
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
@mixin flex-center($columns: false) {
  display: flex;
  align-items: center;
  justify-content: center;
  @if $columns {
    flex-direction: column;
  }
}
#home {
  width: 100%;
  height: 70vh;
  @include flex-center(true);
  justify-content: space-around;
  .user-info {
    text-align: center;
  }
  .home-links {
    width: 100%;
    @include flex-center();
    .home-link {
      flex: 0 0 25%;
      margin: 0 15px;
      padding: 40px 0;
      transition: .4s;
      text-decoration: none;
      color: var(--bs-white);
      @include flex-center(true);
      &:hover {
        transform: scale(1.1);
        text-decoration: none;
        box-shadow: 0 10px 20px rgba(0,0,0,0.5);
      }
    }
    .home-link:first-child { background-color: var(--bs-blue) }
    //.home-link:nth-child(2) { background-color: var(--bs-orange) }
    //.home-link:last-child { background-color: var(--bs-red) }
  }
}
</style>