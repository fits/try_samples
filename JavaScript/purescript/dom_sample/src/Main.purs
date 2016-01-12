module Main where

import Prelude
import Control.Monad.Eff

import Data.Nullable (toMaybe)
import Data.Maybe

import DOM
import DOM.HTML.Types
import DOM.Node.Types

import DOM.HTML (window)
import DOM.HTML.Window (document)
import DOM.Node.NonElementParentNode (getElementById)
import DOM.Node.Node (setTextContent)

render :: forall eff. String -> Eff (dom :: DOM | eff) Unit
render id = do
	win <- window
	doc <- document win
	nod <- getElementById (ElementId id) $ htmlDocumentToNonElementParentNode doc
	case (toMaybe nod) of
		Just x -> setTextContent "sample" (elementToNode x)
		_      -> return unit
