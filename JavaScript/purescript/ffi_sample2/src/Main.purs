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

    runFunc id "e5"
    runFunc id { name : "f6" }
