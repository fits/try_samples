module Handler.Hello where

import Import

getHelloR :: String -> Handler Html
getHelloR msg = defaultLayout $(widgetFile "hello")
