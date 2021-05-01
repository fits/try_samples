
<script>
    let first = ''
    let second = ''
</script>

<div>
    <p>first: <input type="text" bind:value={ first }></p>
    <p>second: <input type="text" bind:value={ second }></p>
    <hr />
    <p>{ [first, second].filter(i => i.trim() != '').join(', ') }</p>
</div>
