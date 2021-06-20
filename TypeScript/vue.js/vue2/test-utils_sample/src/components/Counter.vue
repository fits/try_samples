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
import Vue from 'vue'

export default Vue.extend({
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
