import System
import Text.XML.HaXml
import Text.XML.HaXml.Posn
import Text.XML.HaXml.Util

main = do
	args <- getArgs
	contents <- readFile $ head args

	let
		Document _ _ root _ = xmlParse "" contents
		cont = CElem root noPos

	mapM_ (print . tagTextContent) $ (deep $ tag "jpfr-t-cte:OperatingIncome" `with` attrval ("contextRef", AttValue [Left "CurrentYearConsolidatedDuration"])) cont

