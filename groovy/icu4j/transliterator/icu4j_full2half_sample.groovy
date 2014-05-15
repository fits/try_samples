@Grab('com.ibm.icu:icu4j:53.1')
import com.ibm.icu.text.Transliterator

def t = Transliterator.getInstance('Fullwidth-Halfwidth')

println t.transliterate('123-45-678')
println t.transliterate('123\45\678')
println t.transliterate('‚P‚Q‚R\‚S‚T\‚U‚V‚W')
println t.transliterate('‚P‚Q‚R|‚S‚T|‚U‚V‚W')
println t.transliterate('‚P‚Q‚R]‚S‚T]‚U‚V‚W')
println t.transliterate('‚P‚Q‚R-‚S‚T-‚U‚V‚W')
println t.transliterate('‚P‚Q‚R ‚S‚T ‚U‚V‚W')
println t.transliterate('‚P‚Q‚R@‚S‚T@‚U‚V‚W')
println t.transliterate('ƒTƒ“ƒvƒ‹')
println t.transliterate('»İÌßÙ')
println t.transliterate('‚³‚ñ‚Õ‚é')
