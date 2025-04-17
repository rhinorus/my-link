import './assets/styles/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';

const vuetify = createVuetify({
  components, // Регистрирует все компоненты глобально
});

const app = createApp(App);

app.use(router);
app.use(vuetify);

app.mount('#app')