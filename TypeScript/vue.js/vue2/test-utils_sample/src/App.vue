<template>
  <div>
    <Counter />
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Counter from './components/Counter.vue';

export default Vue.extend({
  name: 'App',
  components: {
    Counter
  }
});
</script>
