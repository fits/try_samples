<script>
	import { afterUpdate, beforeUpdate } from 'svelte'
	import Item from './Item.svelte'

	export let counter

	function countUp() {
		counter += 1
	}

	function countDown() {
		counter -= 1
	}

	$: {
		console.log(`*** current counter: ${counter}`)
	}

	$: status = counter >= 0 ? 'normal' : 'negative'

	beforeUpdate(() => console.log('*** before update'))
	afterUpdate(() => console.log('*** after update'))
</script>

<main>
	<h1>Counter</h1>
	<div>
		<button on:click={countDown}> - </button>
		<button on:click={countUp}> + </button>
		count: <span class={status}>{counter}</span>
	</div>
	<div>
		{#if counter >= 10}
			<p>>= 10</p>
		{:else if counter >= 5}
			<p>>= 5</p>
		{/if}
	</div>
	<div>
		<Item />
	</div>
</main>

<style>
	main {
		text-align: center;
		padding: 1em;
		max-width: 240px;
		margin: 0 auto;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}

	.normal {
		color: black
	}

	.negative {
		color: red
	}
</style>