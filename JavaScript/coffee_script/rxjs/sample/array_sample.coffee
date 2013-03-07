rx = require 'rxjs'

rx.Observable.fromArray(["a", "b", "c"]).subscribe (x) -> console.log x

# 0, 1, 2 を skip し 3, 4 を take して出力
rx.Observable.range(0, 10).skip(3).take(2).subscribe (x) -> console.log x

