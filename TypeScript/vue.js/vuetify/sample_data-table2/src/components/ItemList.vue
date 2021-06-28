<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="10">
        <v-data-table
          :headers="headers"
          :items="items"
          item-key="name"
          :loading="loading"
          show-expand
        >
          <template v-slot:top>
            <v-toolbar flat>
              <v-btn
                color="primary"
                class="mb-2"
                :disabled="loading"
                @click="loadItems()"
              >
                Reload
              </v-btn>
            </v-toolbar>
          </template>
          <template v-slot:expanded-item="{ headers, item }">
            <td :colspan="headers.length">
              <v-simple-table class="details">
                <thead>
                  <tr>
                    <th class="primary--text">
                      Size
                    </th>
                    <th class="primary--text">
                      Color
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="v, i in item.variations"
                    :key="item.name + '/' + i"
                  >
                    <td>{{ v.size }}</td>
                    <td>{{ v.color }}</td>
                  </tr>
                </tbody>
              </v-simple-table>
            </td>
          </template>
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
        loading: false,
        items: [],
        headers: [
          { text: 'Name', value: 'name' },
          { text: 'Value', value: 'value' },
          { text: 'Variations', value: 'data-table-expand' },
        ]
      }
    },
    methods: {
      async loadItems() {
        this.loading = true
        this.items = []

        try {
          const res = await fetch('/items.json')
          this.items = await res.json()
        } catch(e) {
          console.error(e)
        } finally {
          this.loading = false
        }
      }
    },
    created() {
      this.loadItems()
    },
  })
</script>

<style>
  .details {
    margin-top: 4px;
    margin-bottom: 4px;
  }
</style>
