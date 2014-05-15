@Grab('com.ibm.icu:icu4j:53.1')
import com.ibm.icu.text.Transliterator

Transliterator.availableIDs.each { println it }
