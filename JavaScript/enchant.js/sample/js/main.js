enchant();

window.onload = function(){
    var game = new Core(320, 320);

    game.fps = 24;

    game.preload("img/chara01.png");

    game.onload = function(){
        player = new Sprite(32, 48);

        player.image = game.assets["img/chara01.png"];

        player.x = 0;
        player.y = 0;

        player.direction = 0;
        player.distance = 6;
        player.patternNumber = 3;
        player.isMoving = false;
        player.walk = 0;
        player.vx = 0;
        player.vy = 0;

        game.rootScene.addChild(player);

        player.addEventListener("enterframe", function(){
            this.frame = (this.direction * 3) + this.walk;

            if (this.isMoving) {
                this.moveBy(this.vx, this.vy);

                this.isMoving = false;
                this.walk++;

                if (this.walk >= this.patternNumber) {
                    this.walk = 0;
                }
            }
            else {
                this.vx = 0;
                this.vy = 0;

                if (game.input.left) {
                    this.direction = 1;
                    this.vx = -1 * this.distance;
                }
                else if (game.input.right) {
                    this.direction = 2;
                    this.vx = 1 * this.distance;
                }
                else if (game.input.up) {
                    this.direction = 3;
                    this.vy = -1 * this.distance;
                }
                else if (game.input.down) {
                    this.direction = 0;
                    this.vy = 1 * this.distance;
                }

                if (this.vx || this.vy) {
                    this.isMoving = true;
                }
            }
        });
    };

    game.start();
};
