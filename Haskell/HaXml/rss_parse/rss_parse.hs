import System.Environment
import Text.XML.HaXml
import Text.XML.HaXml.Posn
import Text.XML.HaXml.Util
import Text.XML.HaXml.Xtract.Parse

type XmlString = String

data ChannelItem = ChannelItem {
	title :: String,
	description :: String,
	link :: String
} deriving (Eq, Show, Read)

toDocument :: XmlString -> Document Posn
toDocument = xmlParse ""

toContent :: Document Posn -> Content Posn
toContent (Document _ _ el _) = CElem el noPos

toItem :: Content Posn -> ChannelItem
toItem ct = ChannelItem (get "title") (get "description") (get "link")
	where
		get name = concatMap tagTextContent $ keep /> tag name $ ct

findItems :: CFilter Posn
findItems = tag "rss" /> tag "channel" /> tag "item"

items :: XmlString -> [ChannelItem]
items xml = map toItem $ findItems . toContent . toDocument $ xml

main = do
	args <- getArgs
	xml <- readFile $ head args

	mapM_ (putStrLn . title) $ items xml
