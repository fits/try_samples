
<counter>
    <p>count: { count }</p>
    <button onclick= { countUp }>countUp</button>

    this.count = 0;

    this.countUp = () => {
        this.count += 1;

        opts.observer.trigger('updated', { newCount: this.count });
    }
</counter>
