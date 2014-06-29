@Grab('org.apache.spark:spark-core_2.10:1.0.0')
import org.apache.spark.api.java.JavaSparkContext

def spark = new JavaSparkContext('local', 'MoneyCount')

def res = spark.textFile(args[0]).countByValue()

println res
