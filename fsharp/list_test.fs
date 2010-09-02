
let list1 = [1; 2; 3;]
printfn "list1 = %A" list1

let list2 = [1 .. 10]
printfn "list2 = %A" list2

let list3 = [for i in 1 .. 5 -> i * i]
printfn "list3 = %A" list3

printfn "empty = %A" []

//æ“ª‚É100’Ç‰Á
let list4 = 100 :: list1
printfn "list4 = %A" list4

//ƒŠƒXƒg‚Ì˜AŒ‹
let list5 = list1 @ list2
printfn "list5 = %A" list5

