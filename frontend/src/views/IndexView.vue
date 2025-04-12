<script setup lang="ts">
import TgButton from '@/components/TgButton.vue';
import ShortLink from '@/components/ShortLink.vue';
import type { ILink } from '@/components/ShortLink.vue';
import { computed, ref, watch } from 'vue';
import axios from 'axios';
import { toast } from 'vue3-toastify';
import showToast, { ToastType } from '@/mixins/toastMixin';
import moment from 'moment';

const link = ref({
  url: '',
  shortUrl: '',
  isAlreadyUsed: false
});

const links = ref<ILink[]>([])

// Проверка на существующую короткую ссылку
watch(
  () => link.value.shortUrl,
  (shortUrl) => {
    axios.get(`/api/links/by-short-url/${shortUrl}`).then(
      () => link.value.isAlreadyUsed = true,
      () => link.value.isAlreadyUsed = false
    );
})

async function refresh() {
  axios.get('/api/links').then(
    (response) => {
      links.value = response.data;
      links.value.sort((a,b) => moment(b.lastModified).diff(moment(a.lastModified)));
    }
  )
}

async function pasteUrl() {
  link.value.url = await navigator.clipboard.readText();
}

function clearForm() {
  link.value.shortUrl = '';
  link.value.url = '';
}

function createShortUrl() {
  if (isFormComplete.value) {
    axios.put('/api/links', link.value).then(
      () => {
        refresh();
        showToast("Ссылка создана!", ToastType.Success);
        clearForm();
      }
    );
  }

}

const isFormComplete = computed(() => {
  if (link.value.url == '')
    return false;

  if (link.value.shortUrl == '')
    return false;

  if (link.value.isAlreadyUsed)
    return false;

  return true;
})

const creationButtonText = computed(() => {
  if (link.value.isAlreadyUsed)
    return 'Короткое название уже занято';

  return 'Создать ссылку';
})

refresh();

</script>

<template>

  <div class="block column space-between gap-1">

    <!-- О том, как использовать сервис -->
    <div class="scenarios card">
      <!-- Заголовок карточки -->
      <div class="w-100 row flex-start gap-05">
        <div class="center">
          <img src="../assets/images/lightbulb.svg" alt="lightbulb">
        </div>

        <div class="column gap-025">
          <span>Сценарии использования</span>
          <span class="hint">Как приложение помогает в повседневности</span>
        </div>
      </div>
    </div>

    <!-- Логотип и создание ссылки -->
    <div class="column">

      <!-- Логотип -->
      <div class="center column gap-05">
        <!-- <img src="../assets/images/logo.svg" alt="logo"> -->
        <h4>моя-ссылка.рф</h4>
        <span class="hint">Легко запомнить. Легко поделиться.</span>
      </div>

      <!-- Блок создания ссылки -->
      <div class="block center column gap-1">
        <div class="card create-link">

          <div class="flex gap-1">
            <input v-model="link.url" class="w-100" id="url-input" placeholder="URL-адрес для сокращения" type="text">
            <tg-button @click="pasteUrl" :name="'Вставить'"></tg-button>
          </div>

          <div class="short-uri">
            <span class="hint" style="white-space: nowrap;">моя-ссылка.рф/</span>
            <input v-model="link.shortUrl" id="short-url-input" class="w-100" placeholder="название-ссылки" type="text">
          </div>

        </div>

        <tg-button @click="createShortUrl" class="w-100" 
          :alert="link.isAlreadyUsed" 
          :disabled="!isFormComplete" 
          :name="creationButtonText">
        </tg-button>
      </div>

    </div>


    <!-- Карточка со ссылками -->
    <div class="flex column gap-1" style="flex-direction: column;" v-if="links.length > 0">
      <!-- Заголовок карточки -->
      <div class="w-100 flex-start">
        <span class="hint">Мои ссылки</span>
      </div>

      <div class="flex column gap-1">

        <!-- Ссылка -->
        <short-link v-for="link in links" :url="link.url" :short-url="link.shortUrl" :number-of-clicks="100"
          :last-modified="link.lastModified" @update="refresh">
        </short-link>

      </div>
    </div>

    <div v-else class="center">
      <h4 class="hint">Вы пока не создали ни одной ссылки</h4>
    </div>

  </div>



</template>

<style scoped>
h4 {
  font-size: 1.2rem;
}

.scenarios {
  transition: 0.15s ease-in-out;
}

.scenarios:hover {
  cursor: pointer;

  /* Смешение сцвета фона с прозрачным */
  --transparent-bg-color: color-mix(in srgb, var(--bg-color), #0000 40%);
  background-color: var(--transparent-bg-color);

  box-shadow: 0 0 .25rem .15rem rgba(34, 60, 80, .05);
}

.create-link {
  width: 100%;

  flex-direction: column;
  gap: 1rem;
}

.short-uri {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: .25rem;
}
</style>
