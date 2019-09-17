module Main where

import Prelude

import Control.Monad.Indexed ((:*>), (:>>=))

import Data.Lazy (force)

import Effect (Effect)

import Hyper.Node.Server (defaultOptionsWithLogging, runServer)
import Hyper.Request (getRequestData)
import Hyper.Response (closeHeaders, respond, writeStatus, end)
import Hyper.Status (statusOK, statusNotFound)

main :: Effect Unit
main = 
  runServer defaultOptionsWithLogging {} app

  where
    paths = _.parsedUrl >>> force >>> _.path

    responseEnd st txt = writeStatus st :*> closeHeaders :*> respond txt

    ok = responseEnd statusOK
    notFound = writeStatus statusNotFound :*> closeHeaders :*> end

    app = paths <$> getRequestData :>>=
      case _ of
        ["users"] -> ok "users-all"
        ["users", id] -> ok $ "userid-" <> id
        _ -> notFound
