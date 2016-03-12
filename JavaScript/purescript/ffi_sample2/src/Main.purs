module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

import Sample.Func

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
    procData1 { name : "a1" }
    -- procData1 { name : "a1", v : 1 } -- NG

    procData2 { name : "b2", v : 2 }
    procData3 { name : "c3", v : 3, k : 1.5 }
    procData4 { name : "d4", v : 4, k : 1.5 }

    procData5 { name : "e5", v : 5, k : 1.5 }
    procData5 "e5-2"

    procData6 { name : "f6", v : 6 }
    procData7 { name : "g7" }


    runFunc id "123"
    runFunc id { name : "456" }

    runFunc2 id { name : "789" }
