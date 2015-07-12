package sample.model


object Product extends QProduct("product") {
  override def as(variable: String) = new QProduct(variable)
  
}

/**
 * Product is a Querydsl bean type
 */
class Product {

  var id: java.lang.Long = _

  var name: String = _

  var price: java.lang.Long = _

  var releaseDate: java.sql.Timestamp = _

}

