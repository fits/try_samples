import System
import Text.XML.HaXml
import Text.XML.HaXml.Posn
import Text.XML.HaXml.Util
import Text.XML.HaXml.Xtract.Parse

-- XPath ‚ÅŒŸõ
findNodeList :: String -> Content Posn -> [Content Posn]
findNodeList pattern ct = xtract id pattern ct

-- Element ‚ð Content ‚Ö
toContent :: Element Posn -> Content Posn
toContent el = CElem el noPos

-- XML‚Ìƒp[ƒX
parseXmlString :: String -> Element Posn
parseXmlString ct = 
	let Document _ _ root _ = xmlParse "" ct
	in root

main = do
	args <- getArgs
	contents <- readFile $ head args

	let nodeList = findNodeList "//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']" (toContent $ parseXmlString contents)

	mapM_ (print . tagTextContent) nodeList
