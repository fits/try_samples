<template>
  <v-app>
    <v-main>
      <Sample />
    </v-main>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue';
import Sample from './components/Sample.vue';

export default Vue.extend({
  name: 'App',

  components: {
    Sample,
  },

  data: () => ({
    //
  }),
});
</script>
