<template>
  <div>
    <Counter />
  </div>
  <div>
    <Counter2 />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Counter from './components/Counter.vue';
import Counter2 from './components/Counter2.vue';

export default defineComponent({
  name: 'App',
  components: {
    Counter,
    Counter2
  }
});
</script>
