@Grab('org.apache.spark:spark-core_2.10:1.0.0')
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function

def spark = new JavaSparkContext('local', 'MoneyCount')

def list = ['A', 'B', 'B', 'C', 'B', 'A']

def res = 1 - spark.parallelize(list).countByValue()*.value.sum { (it / list.size()) ** 2 }

println res
