function RunnerManager(elem) {
    this.elem = elem;
    this.runners = {};
    this.started = false;

    this.seed = 123;

    $.ajax({
        url : "game/",
        type : "POST",
        success : function(json) {
            console.log(json);
            console.log("success");
            }
        },
    });
    this.socket = new Websocket('ws://localhost:8080');

    this.reset(this.seed);
}

(function() {
    function getTimeStamp() {
        return new Date().getTime();
    }

    RunnerManager.prototype = {
        start: function() {
            for(name in this.runners) {
                var runner = this.runners[name].start();
            }
            this.player.start();

            this.started = true;
        },
        reset: function(seed) {
            this.seed = seed || 123;

            for(name in this.runners) {
                this.runners[name].destroy();
            }
            this.runners = {}

            if(this.player) {
                this.player.destroy();
            }
            this.player = new Runner(this.elem, true, this.seed, this.onPlayerDied);
            
            this.started = false;
        },

        addRunner: function(name) {
            if(!this.runners[name] && !this.started) {
                this.runners[name] = new Runner(this.elem, false, this.seed);
            }

            return this.runners[name] || null;
        },

        onAction: function(player, action) {
            ;
        },
        onPlayerDied: function() {
            ;
        }
    }
})()