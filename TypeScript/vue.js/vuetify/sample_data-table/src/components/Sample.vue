<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="10">
        <v-data-table :headers="headers" :items="items">
        </v-data-table>
      </v-col>
    </v-row>
    <v-row class="text-center">
      <v-col cols="8">
        <v-data-table :headers="headers" :items="items" group-by="category">
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
  import Vue from 'vue'

  export default Vue.extend({
    name: 'Sample',

    data: () => ({
      items: [
        { name: 'item-1', value: 1, category: 'A' },
        { name: 'item-2', value: 120, category: 'B' },
        { name: 'item-3', value: 30, category: 'C' },
        { name: 'item-4', value: 10, category: 'C' },
        { name: 'item-5', value: 340, category: 'D' },
        { name: 'item-6', value: 660, category: 'E' },
        { name: 'item-7', value: 7, category: 'E' },
      ]
    }),
    computed: {
      headers: () => [
        { text: 'Name', value: 'name' },
        { text: 'Value', value: 'value' },
        { text: 'Category', value: 'category' },
      ]
    },
  })
</script>
