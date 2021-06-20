<template>
  <div>
    <ItemList />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import ItemList from './components/ItemList.vue';

export default defineComponent({
  name: 'App',
  components: {
    ItemList
  }
});
</script>
