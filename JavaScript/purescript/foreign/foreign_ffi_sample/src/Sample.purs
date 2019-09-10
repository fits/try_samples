module Sample
  ( createData
  , dataList
  ) where

import Foreign

foreign import createData :: String -> Foreign
foreign import dataList :: Foreign
