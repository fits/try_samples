{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE ExtendedDefaultRules #-}

module Lib
    ( accessMongo
    ) where

import System.Environment
import Control.Monad.Trans (lift)
import Database.MongoDB 

accessMongo :: IO ()
accessMongo = do
    mongo <- mongoHost
    pipe <- connect (host mongo)

    res <- access pipe master "sample" proc

    close pipe

mongoHost :: IO String
mongoHost = getEnv "MONGO_HOST"

proc :: Action IO ()
proc = do
    insert "items" ["name" =: "item1", "value" =: 1]

    insertMany "items" [
        ["name" =: "item2", "value" =: 2],
        ["name" =: "item3", "value" =: 3] ]

    allItems >>= printDocs

allItems :: Action IO [Document]
allItems = rest =<< find ( select [] "items" )

printDocs :: [Document] -> Action IO ()
printDocs docs = lift $ mapM_ print docs
