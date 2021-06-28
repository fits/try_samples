<template>
  <v-app>
    <v-main>
      <ItemList />
    </v-main>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import ItemList from './components/ItemList.vue';

export default Vue.extend({
  name: 'App',

  components: {
    ItemList,
  },

  data: () => ({
    //
  }),
});
</script>
