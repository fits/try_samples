
{-# LANGUAGE FlexibleContexts #-}
{-# LANGUAGE GADTs #-}
{-# LANGUAGE GeneralizedNewtypeDeriving #-}
{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE QuasiQuotes #-}
{-# LANGUAGE TemplateHaskell #-}
{-# LANGUAGE TypeFamilies #-}

import Database.Persist.Sqlite
import Database.Persist.TH
import Control.Monad.IO.Class

share [mkPersist sqlSettings, mkMigrate "migrateAll"] [persistLowerCase|
Product
	name String
	deriving Show
|]

main :: IO ()
main = runSqlite "sample.db" $ do
	runMigration migrateAll

	id1 <- insert $ Product "sample1"
	liftIO $ print id1

	d1 <- get id1
	liftIO $ print d1
