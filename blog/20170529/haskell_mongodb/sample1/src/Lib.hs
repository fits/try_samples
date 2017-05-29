{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE ExtendedDefaultRules #-}

module Lib
    ( someFunc
    ) where

import System.Environment
import Control.Monad.Trans (lift)
import Database.MongoDB 

someFunc :: IO ()
someFunc = do
    mongo <- mongoHost
    pipe <- connect (host mongo)

    access pipe master "sample" proc

    close pipe

mongoHost :: IO String
mongoHost = getEnv "MONGO_HOST"

proc :: Action IO ()
proc = do
    insertMany_ "items" [
        ["name" =: "item1", "value" =: 1],
        ["name" =: "item2", "value" =: 2] ]

    allItems >>= printDocs

allItems :: Action IO [Document]
allItems = rest =<< find ( select [] "items" )

printDocs :: [Document] -> Action IO ()
printDocs docs = lift $ mapM_ print docs
