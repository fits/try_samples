<template>
  <div>
    <p>
      <button @click="countUp">count up</button>
    </p>
    <p>
      counter: {{ count }}
    </p>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'

export default {
  setup() {
    const count = ref(0)
    const countUp = () => count.value++

    return {
      count,
      countUp
    }
  }
}
</script>
