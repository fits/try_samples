module Counter (component) where

import Prelude

import Data.Maybe (Maybe(..))

import Halogen.HTML (ClassName(..))
import Halogen as H
import Halogen.HTML as HH
import Halogen.HTML.Events as HE
import Halogen.HTML.Properties as HP

type State = { count :: Int }

data Action = Down | Up

component :: forall q i o m. H.Component HH.HTML q i o m
component =
  H.mkComponent
    { initialState: const initialState
    , render
    , eval: H.mkEval $ H.defaultEval { handleAction = handleAction }
    }

initialState :: State
initialState = { count: 0 }

render :: forall m. State -> H.ComponentHTML Action () m
render state =
  HH.div_
    [ HH.button
        [ HE.onClick \_ -> Just Down ]
        [ HH.text "down" ]
    , HH.span
        [ HP.class_ $ ClassName "result" ]
         [ HH.text $ show state.count ]
    , HH.button
        [ HE.onClick \_ -> Just Up ]
        [ HH.text "up" ]
    ]

handleAction :: forall o m. Action -> H.HalogenM State Action () o m Unit
handleAction = case _ of
  Down ->
    H.modify_ \st -> st { count = st.count - 1 }
  Up ->
    H.modify_ \st -> st { count = st.count + 1 }
