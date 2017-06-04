module Api

open Freya.Core
open Freya.Machines.Http
open Freya.Types.Http
open Freya.Routers.Uri.Template

let handler = 
    freya {
        return Represent.text "sample" }

let machine =
    freyaMachine {
        handleOk handler }

let root =
    freyaRouter {
        resource "/sample" machine }
