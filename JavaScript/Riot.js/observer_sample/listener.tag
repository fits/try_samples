
<listener>
    <p>message: { message }</p>

    opts.observer.on('updated', ev => {
        this.message = JSON.stringify(ev);
        this.update();

        console.log(ev);
    });
</listener>
