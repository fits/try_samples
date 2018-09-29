<template>
  <div>
    <h1>Item - {{ $route.params.id }}</h1>
    <div>id: {{ item.id }}, name: {{ item.name }}</div>
  </div>
</template>

<script>

export default {
  computed: {
    item () {
      const id = this.$route.params.id
      const res = this.$store.state.items.filter(it => it.id == id)

      return (res.length > 0) ? res[0] : {}
    }
  }
}

</script>
