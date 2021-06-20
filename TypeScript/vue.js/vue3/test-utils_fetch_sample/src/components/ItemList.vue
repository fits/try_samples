<template>
  <div>
    <button @click="loadItems">load items</button>
    <ul>
      <li v-for="it in items">
        {{ it.name }}, {{ it.value }}
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'

export default {
  setup() {
    const items = ref([])

    const loadItems = async () => {
      const res = await fetch('/items.json')
      items.value = await res.json()
    }

    return {
      items,
      loadItems
    }
  }
}
</script>