
let plus x = x + 3
let times x = x * 2

let f = plus >> times
let g = plus << times

// times(plus(4)) = 14
printfn "%i" (f 4)
// plus(times(4)) = 11
printfn "%i" (g 4)
