import Vue from 'vue'
import routes from './routes'
import Router from 'vue-router'
import beforeEach from './middleware/beforeEach'

Vue.use(Router)

const router = new Router({ routes, mode: 'history' })

router.beforeEach(beforeEach)

export default router

