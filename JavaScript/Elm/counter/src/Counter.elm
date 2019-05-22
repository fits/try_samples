module Main exposing (main)

import Browser
import Html exposing (Html, button, div, text, h1, span)
import Html.Attributes exposing (style)
import Html.Events exposing (onClick)

type alias Model = Int
type Msg = Up Int | Down Int

init : Model
init = 0

update : Msg -> Model -> Model
update msg model =
    case msg of
        Up n -> model + n
        Down n -> model - n

view : Model -> Html Msg
view model =
    div []
        [  h1 [] [ text "Counter" ]
        , div []
            [ button [ onClick (Down 2) ] [ text "2down"]
            , button [ onClick (Down 1) ] [ text "down"]
            , span
                [ style "width" "60px" 
                , style "display" "inline-block"
                , style "text-align" "center"
                ]
                [ text (String.fromInt model) ]
            , button [ onClick (Up 1) ] [ text "up"]
            , button [ onClick (Up 2) ] [ text "2up"]
            ]
        ]

main : Program () Model Msg
main = 
    Browser.sandbox
        { init = init
        , update = update
        , view = view
        }
