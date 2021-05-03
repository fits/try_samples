<script lang="ts">
  let count: number = 0

  const inc = () => count += 1
  const dec = () => count -= 1
</script>

<div class="box">
  <div>
    <button on:click={ dec }>down</button>
  </div>
  <div class="value">
    counter: { count }
  </div>
  <div>
    <button on:click={ inc }>up</button>
  </div>
</div>

<style>
  .box {
    display: flex;
  }

  .value {
    width: 20%;
  }
</style>