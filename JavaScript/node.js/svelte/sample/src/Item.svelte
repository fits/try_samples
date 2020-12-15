
<script>
    const items = [
        {name: 'item-1', value: 11},
        {name: 'item-2', value: 22},
        {name: 'item-3', value: 33},
    ]
</script>

<ul>
    {#each items as it, i}
        <li>{i}: {it.name}, {it.value}</li>
    {/each}
</ul>

<style>
    ul {
        text-align: left;
        list-style-type: none;
    }
</style>
