<script setup lang="ts">
import TgButton from '@/components/TgButton.vue';
import ShortLink from '@/components/ShortLink.vue';
import type { ILink } from '@/components/ShortLink.vue';
import { computed, ref, watch, shallowRef } from 'vue';
import axios from 'axios';
import showToast, { ToastType } from '@/mixins/toastMixin';
import QRCodeStyling from 'qr-code-styling';
import moment from 'moment';
import type { IStatistics } from './interfaces/Statistics';

const link = ref({
  url: '',
  shortUrl: '',
  isAlreadyUsed: false
});

const user = ref({
  authorized: false,
  authUrl: ''
});

const applicationStats = ref<IStatistics>();

// Переменная для показа "Все ссылки / Только избранное"
const showFavoritesOnly = ref(false);

const links = ref<ILink[]>([]);
const showAuthDialog = shallowRef(false);
const showStatsDialog = shallowRef(false);

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

      user.value.authorized = isAuthorized();
      if (!user.value.authorized)
        loadAuthUrl();
    }
  )

  axios.get('/api/stats').then(
    (response) => applicationStats.value = response.data
  )
}

async function pasteUrl() {
  link.value.url = await navigator.clipboard.readText();
}

function toggleIsFavorite(shortUrl : string){
  axios.get(`/api/links/by-short-url/${shortUrl}`).then(
    (response) => {
      var modifiableLink = response.data;
      modifiableLink.isFavorite = !modifiableLink.isFavorite;

      axios.put('/api/links', modifiableLink).then(
        () => refresh()
      )
    }
  )
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

function isAuthorized() {
  const match = document.cookie.match(/USER_TOKEN=([^;]+)/);
  return match !== null;
}

function loadAuthUrl() {
  axios.get('/api/centralized-auth/url').then(
    (response) => {
      user.value.authUrl = response.data;
      checkAuth();
    }
  )
}

function checkAuth() {
  if (user.value.authorized)
    return;

  setTimeout(() => {
    axios.get("/api/centralized-auth/auth").then(
      (response) => {
        if (response.status == 200){
          user.value.authorized = true;
          showAuthDialog.value = false;
          refresh();
        }
      }
    )
    checkAuth();
  }, 3000);
}

function openAuthModal() {  
  showAuthDialog.value = true;
}

function openAuthLink() {
  window.open(user.value.authUrl, '_blank');
}

function appendQRCode () {
  const qrCode = new QRCodeStyling({
      width: 250,
      height: 250,
      margin: 0,
      type: "svg",
      data: user.value.authUrl,
      image: "/src/assets/images/telegram.webp",
      dotsOptions: {
          color: "#24A1DE",
          type: "rounded"
      },
      imageOptions: {
          crossOrigin: "anonymous",
          margin: 10,
          imageSize: .4
      }
  });

  var qrCodeElement = document.getElementById("qrcode");
  if (qrCodeElement !== null)
    qrCode.append(qrCodeElement);

}

function showFavorites() {
  showFavoritesOnly.value = true;
}

function showAllLinks() {
  showFavoritesOnly.value = false;
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

const filteredLinks = computed(() => {
  if (showFavoritesOnly.value)
    return links.value.filter(link => link.isFavorite);
  return links.value;
})

refresh();

</script>

<template>

  <div class="column h-100 space-between gap-1">

    <div class="block column gap-1">

      <!-- Карточка авторизации -->
      <div class="scenarios card" v-if="!user.authorized && user.authUrl" @click="openAuthModal">
        <!-- Заголовок карточки -->
        <div class="w-100 row flex-start gap-1">
          <div class="center">
            <img src="@/assets/images/telegram.webp" style="width: 28px" alt="telegram-icon">
          </div>

          <div class="column gap-025">
            <span>Войдите через телеграм</span>
            <span class="hint">Все созданные ссылки будут привязаны к аккаунту</span>
          </div>
        </div>
      </div>

      <!-- Карточка "Вы авторизованы" -->
      <div class="scenarios card" v-if="user.authorized">
        <!-- Заголовок карточки -->
        <div class="w-100 row flex-start gap-1">
          <div class="center">
            <img src="../assets/images/check.png" style="width: 28px" alt="check-icon">
          </div>

          <div class="column gap-025">
            <span>Вы авторизованы!</span>
            <span class="hint">Все созданные ссылки привязаны к Вашему аккаунту</span>
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

      <!-- Блок со ссылками -->
      <div class="flex column gap-1" style="flex-direction: column;" >

        <!-- Заголовки -->
        <div class="w-100 flex-start gap-1">
          <span class="hint choosable-header" :class="showFavoritesOnly ? '' : 'selected'" @click="showAllLinks">Мои ссылки</span>
          <span class="hint choosable-header" :class="showFavoritesOnly ? 'selected' : ''" @click="showFavorites">Избранное</span>
        </div>

        <div class="flex column gap-1" v-if="filteredLinks.length > 0">

          <!-- Ссылка -->
          <short-link v-for="link in filteredLinks" 
            :url="link.url" 
            :is-favorite="link.isFavorite"
            :short-url="link.shortUrl" 
            :count="link.count"
            :last-modified="link.lastModified" 
            @update="refresh"
            @toggle-is-favorite="toggleIsFavorite">
          </short-link>

        </div>

          <div v-else class="center">
            <h4 class="hint">Нет ни одной ссылки</h4>
          </div>
      </div>

    </div>

    <!-- Футер -->
    <div class="footer">
      <span @click="showStatsDialog = true" class="hint">Статистика</span>
    </div>
  </div>


  <!-- Модальное окно авторизации -->
  <v-dialog v-model="showAuthDialog" width="auto" @vue:updated="appendQRCode">
    <v-card
        title="Отсканируйте QR-код"
        style="padding: 20px;"
      >
      <div class="flex column center gap-1" style="margin-bottom: 20px;">
        <div id="qrcode"></div>

        <span>ИЛИ</span>

        <tg-button @click="openAuthLink" style="height: 30px; margin: 10px"
          :name="'Войдите с текущего устройства'">
        </tg-button>
      </div>
        
      </v-card>
  </v-dialog>

  <!-- Модальное окно со статистикой -->
  <v-dialog v-model="showStatsDialog" width="auto">
    <v-card
        title="Статистика приложения"
        style="padding: 20px;"
      >
        <div class="flex column center gap-1" style="margin-bottom: 20px;">

          <div class="flex row space-between w-100">
            <span>Всего переходов по ссылкам</span>
            <span>{{ applicationStats?.totalNumberOfClicks }}</span>
          </div>

          <div class="flex row space-between w-100">
            <span>Всего ссылок</span>
            <span>{{ applicationStats?.totalNumberOfLinks }}</span>
          </div>

          <div class="flex row space-between w-100">
            <span>Всего пользователей</span>
            <span>{{ applicationStats?.totalNumberOfUsers }}</span>
          </div>

        </div>
      </v-card>
  </v-dialog>


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

.choosable-header {
  cursor: pointer;
}

.choosable-header:hover {
  font-weight: bold;
}

.choosable-header.selected {
  color: var(--accent-color);
  font-weight: bold;
}

.footer {
  display: flex;
  justify-content: start;
  padding: 20px;

  border-top: 1px solid #ddd;
}

.footer span {
  cursor: pointer;
}

.footer span:hover {
  font-weight: bold;
}

</style>
