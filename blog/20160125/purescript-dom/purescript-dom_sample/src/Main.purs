module Main where

import Prelude
import Control.Monad.Eff

import Data.Maybe
import Data.Nullable (toMaybe)

import DOM
import DOM.HTML.Types
import DOM.Node.Types
import DOM.HTML (window)
import DOM.HTML.Window (document)
import DOM.Node.NonElementParentNode (getElementById)
import DOM.Node.Node (setTextContent)

updateContent :: forall eff. String -> String -> Eff (dom :: DOM | eff) Unit
updateContent id content = do
    win <- window
    doc <- document win
    node <- getElementById (ElementId id) $ htmlDocumentToNonElementParentNode doc
    case (toMaybe node) of
        Just x -> setTextContent content (elementToNode x)
        _      -> return unit
