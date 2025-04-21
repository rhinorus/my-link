<script setup lang="ts">

import moment from 'moment';
import axios from 'axios';
import showToast, { ToastType } from '@/mixins/toastMixin';
import { computed } from 'vue';

export interface ILink {
    url: String,
    shortUrl: String,
    isFavorite: boolean,
    count: Number,
    lastModified: Date
}

const props = defineProps<ILink>();

const emit = defineEmits(['update', 'toggleIsFavorite']);

async function removeUrl() {
    const confirmed = await window.confirm("Вы уверены? Ссылка будет удалена.");
    if (!confirmed) return;

    axios.delete(`/api/links/${props.shortUrl}`).then(
        () => {
            emit('update');
            showToast(`Ссылка <b>${props.shortUrl}</b> удалена`);
        },
        () => showToast("Произошла ошибка. Попробуйте позже.", ToastType.Error)
    );
}

function copy() {
    navigator.clipboard?.writeText(`моя-ссылка.рф/${props.shortUrl}`).then(
        () => showToast("Скопировано!"),
        () => showToast("Не удалось скопировать ссылку.", ToastType.Warn)
    )
}

const formattedDate = computed(() => {
    console.log(props.lastModified);
    return moment(props.lastModified).format('DD.MM.YYYY в HH:mm')
})

const formattedTargetUrl = computed(() => {
    return props.url.replace('https://', '').replace('http://', '');
})

</script>

<template>

    <!-- Ссылка -->
    <div class="link">

        <!-- Дата и число переходов -->
        <div class="space-between">
            <span class="hint"><b>{{ formattedDate }}</b></span>
            <span></span>
            <span class="hint">Переходов: <b>{{ count }}</b></span>
        </div>

        <!-- Строка с адресом -->
        <div class="uri-wrapper">
            <a :href="`/${shortUrl}`" class="uri" target="_blank">
                <span style="color: #777;">моя-ссылка.рф/</span>
                <span>{{ shortUrl }}</span>
            </a>

            <!-- Пустой элемент для корректной работы ховера -->
            <div></div>
        </div>

        <!-- Строка с целевым адресом -->
        <span class="hint target-url">{{ formattedTargetUrl }}</span>

        <hr>

        <!-- Блок действий -->
        <div class="space-between">
            <img class="icon-btn" src="../assets/images/delete.svg" @click="removeUrl" alt="delete-icon">

            <div class="flex-end gap-1">
                <img v-if="isFavorite" class="icon-btn" src="../assets/images/heart-fill.svg" @click="$emit('toggleIsFavorite', shortUrl)" alt="favorite-icon">
                <img v-else class="icon-btn" src="../assets/images/heart.svg" @click="$emit('toggleIsFavorite', shortUrl)" alt="empty-favorite-icon">
                
                <img class="icon-btn" src="../assets/images/copy.svg" @click="copy" alt="copy-icon">
            </div>

        </div>

    </div>

</template>

<style scoped>
.link {
    display: flex;
    flex-direction: column;

    border-radius: .25rem;

    background-color: var(--bg-color);

    padding: 1rem;
    gap: .5rem;
}

.uri-wrapper {
    display: flex;
    justify-content: space-between;
}

.uri {
    text-decoration: none;
}

.uri span {
    color: var(--text-color);
}

.target-url {
    text-overflow: ellipsis;
    overflow: hidden;
    max-width: 80%;
    white-space: nowrap;
}

.uri:hover span {
    cursor: pointer;
    text-decoration: underline;
    transition: .15s ease-in-out;
}

.icon-btn:hover{
    cursor: pointer;
    filter: brightness(.8);
}

</style>
