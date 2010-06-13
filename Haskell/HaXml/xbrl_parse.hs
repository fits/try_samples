import System
import Text.XML.HaXml
import Text.XML.HaXml.Posn
import Text.XML.HaXml.Util
import Text.XML.HaXml.Xtract.Parse

main = do
	args <- getArgs
	contents <- readFile $ head args

	let
		Document _ _ root _ = xmlParse "" contents
		cont = CElem root noPos

	mapM_ (print . tagTextContent) $ xtract id "//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']" cont

-- ˆÈ‰º‚Å‚à‰Â
--	let Elem n _ cont = root
--	mapM_ (print . tagTextContent) $ concatMap (xtract id "//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']") cont

