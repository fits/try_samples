<template>
  <div class="box">
    <div>
      <button v-on:click="dec">down</button>
    </div>
    <div class="value" v-on:click="count++">
      counter: {{ count }}
    </div>
    <div>
      <button v-on:click="inc">up</button>
    </div>
  </div>
</template>

<script lang="ts">
  import { ref, defineComponent } from 'vue'

  export default defineComponent({
    name: 'Counter',
    setup: () => {
      const count = ref(0)

      const inc = () => count.value++
      const dec = () => count.value--

      return {
        count,
        inc,
        dec
      }
    }
  })
</script>

<style scoped>
  .box {
    display: flex;
  }

  .value {
    width: 20%;
  }
</style>
