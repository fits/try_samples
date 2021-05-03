<template>
  <Counter />
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import Counter from './components/Counter.vue'

  export default defineComponent({
    name: 'App',
    components: {
      Counter
    }
  })
</script>

<style>
  #app {
    text-align: center;
    padding: 1em;
  }
</style>
