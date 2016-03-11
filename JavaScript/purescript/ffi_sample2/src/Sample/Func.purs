module Sample.Func where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

type Data t = { name :: String | t }

foreign import procData1 :: forall e. { name :: String } -> Eff (console :: CONSOLE | e) Unit

foreign import procData2 :: forall e a. { name :: String | a } -> Eff (console :: CONSOLE | e) Unit

foreign import procData3 :: forall e a. Object ( name :: String | a) -> Eff (console :: CONSOLE | e) Unit

foreign import procData4 :: forall e a. Data a -> Eff (console :: CONSOLE | e) Unit

foreign import runFunc :: forall e a b. Function a b -> a -> Eff (console :: CONSOLE | e) Unit
