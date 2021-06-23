<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="10">
        <v-data-table :headers="headers" :items="items">
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
  import Vue from 'vue'

  export default Vue.extend({
    data() {
      return {
        items: [],
      }
    },
    computed: {
      headers() {
        return [
          { text: 'Name', value: 'name' },
          { text: 'Value', value: 'value' },
          { text: 'Category', value: 'category' },
        ]
      }
    },
    methods: {
      async loadItems() {
        const res = await fetch('/items.json')
        this.items = await res.json()
      }
    },
    created() {
      this.loadItems()
    },
  })
</script>
