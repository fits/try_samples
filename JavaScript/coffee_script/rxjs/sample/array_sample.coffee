rx = require 'rxjs'

rx.Observable.fromArray(["a", "b", "c"]).subscribe (x) -> console.log x

