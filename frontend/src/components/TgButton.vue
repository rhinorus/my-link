<script setup lang="ts">
import { computed } from 'vue';


const props = defineProps({
  name: String,
  disabled: {
    default: false,
    required: false,
    type: Boolean
  }
});

const btnClass = computed(() => {
  if (props.disabled){
    return 'disabled';
  }
  return '';
});

</script>

<template>
  <div class="button center" :class="btnClass">
    <span class="btn-name">{{ name }}</span>
  </div>
  
</template>

<style scoped>

.button {
  cursor: pointer;


  background-color: var(--accent-color);
  color: var(--secondary-text-color);

  padding: .5rem 1rem;

  font-size: .75rem;
  border-radius: .25rem;
  transition: background-color 0.25s ease-in-out;
}

.btn-name {
  font-family: 'San Francisco SemiBold';
}


.button:hover {
  background-color: var(--accent-color-hover);
}

.button.disabled {
  background-color: #aaa !important;
  cursor: default;
}

</style>
