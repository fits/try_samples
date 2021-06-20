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
import { defineComponent } from 'vue';

export default defineComponent({
  data() {
    return {
      count: 0
    }
  },
  methods: {
    countUp() {
      this.count++
    }
  }
})
</script>
