{-# LANGUAGE DataKinds       #-}
{-# LANGUAGE TemplateHaskell #-}
{-# LANGUAGE TypeOperators   #-}
module Lib
  ( startApp
  , app
  ) where

import Data.Aeson
import Data.Aeson.TH
import Network.Wai
import Network.Wai.Handler.Warp
import Servant

data Counter = Counter
  { counterId :: String
  , count     :: Int
  } deriving (Eq, Show)

$(deriveJSON defaultOptions ''Counter)

type API = "counters" :> Get '[JSON] [Counter]
      :<|> "counters" :> Capture "counterid" String :> Get '[JSON] Counter
      :<|> "counters" :> ReqBody '[JSON] Counter :> Post '[JSON] Counter

startApp :: IO ()
startApp = run 8080 app

app :: Application
app = serve api server

api :: Proxy API
api = Proxy

server :: Server API
server = getCounters :<|> getCounter :<|> createCounter

    where getCounters :: Handler [Counter]
          getCounters = return
            [ Counter {counterId = "a1", count = 1}
            , Counter {counterId = "b2", count = 2}
            , Counter {counterId = "c3", count = 3}
            ]

          getCounter :: String -> Handler Counter
          getCounter id = return Counter {counterId = id, count = 1}

          createCounter :: Counter -> Handler Counter
          createCounter (Counter id ct) = return Counter {counterId = id, count = ct * 2}
