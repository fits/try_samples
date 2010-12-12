
let plus x = x + 3
let times x = x * 2

let f = plus >> times
// times(plus 4)
printfn "%i" (f 4)

let g = plus << times
// plus(times 4)
printfn "%i" (g 4)
