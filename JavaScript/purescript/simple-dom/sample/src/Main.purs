module Main where

import Prelude
import Control.Monad.Eff

import Data.Maybe

import DOM

import Data.DOM.Simple.Window
import Data.DOM.Simple.Element (getElementById, setTextContent)

render :: forall eff. String -> Eff (dom :: DOM | eff) Unit
render id = do
    doc <- document globalWindow
    nod <- getElementById id doc

    case nod of
        Just x -> setTextContent "sample" x
        _      -> return unit
