@Grab('com.ibm.icu:icu4j:53.1')
import com.ibm.icu.text.Transliterator

def t = Transliterator.getInstance('Fullwidth-Halfwidth')

def toHalf = { String str -> t.transliterate(str) }
def toNumericOnly = { String str -> str.replaceAll('[^0-9]', '') }

def toHalfWidthNumeric = toHalf >> toNumericOnly

println toHalfWidthNumeric('12345678')
println toHalfWidthNumeric('123-45-678')
println toHalfWidthNumeric('123Å\45Å\678')
println toHalfWidthNumeric('ÇPÇQÇRÅ\ÇSÇTÅ\ÇUÇVÇW')
println toHalfWidthNumeric('ÇPÇQÇRÅ|ÇSÇTÅ|ÇUÇVÇW')
println toHalfWidthNumeric('ÇPÇQÇRÅ]ÇSÇTÅ]ÇUÇVÇW')
println toHalfWidthNumeric('ÇPÇQÇR-ÇSÇT-ÇUÇVÇW')
println '-----'
println toHalfWidthNumeric('')
println toHalfWidthNumeric('ÉTÉìÉvÉã')
println toHalfWidthNumeric('ª›ÃﬂŸ')
println toHalfWidthNumeric('Ç≥ÇÒÇ’ÇÈ')
println '-----'
