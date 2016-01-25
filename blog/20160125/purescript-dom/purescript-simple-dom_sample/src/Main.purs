module Main where

import Prelude
import Control.Monad.Eff

import DOM

import Data.Maybe
import Data.DOM.Simple.Window (document, globalWindow)
import Data.DOM.Simple.Element (getElementById, setTextContent)

updateContent :: forall eff. String -> String -> Eff (dom :: DOM | eff) Unit
updateContent id content = do
    doc <- document globalWindow
    node <- getElementById id doc

    case node of
        Just x -> setTextContent content x
        _      -> return unit
