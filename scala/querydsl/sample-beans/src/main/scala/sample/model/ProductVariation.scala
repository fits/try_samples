package sample.model


object ProductVariation extends QProductVariation("productVariation") {
  override def as(variable: String) = new QProductVariation(variable)
  
}

/**
 * ProductVariation is a Querydsl bean type
 */
class ProductVariation {

  var color: String = _

  var id: java.lang.Long = _

  var productId: java.lang.Long = _

  var size: String = _

}

