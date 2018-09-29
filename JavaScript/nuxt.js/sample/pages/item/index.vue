<template>
  <div>
    <h1>Items</h1>

    <ul v-if="items.length > 0">
      <li v-for="it in items">
        <router-link :to="{ path: `/item/${it.id}`}">
          {{it.id}} - {{it.name}}
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  computed: {
    items () {
      return this.$store.state.items
    }
  },
  created: function() {
    axios
      .get('http://localhost:8080/items')
      .then(res => this.$store.commit('update', res.data))
      .catch(err => console.error(err))
  }
}
</script>
