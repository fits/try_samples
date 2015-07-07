package sample.model

import com.querydsl.core.types._
import com.querydsl.scala._

import com.querydsl.core.types.PathMetadataFactory._;

import com.querydsl.scala.sql.RelationalPathImpl

import com.querydsl.sql._

object QProductVariation extends QProductVariation("qProductVariation") {
  override def as(variable: String) = new QProductVariation(variable)
  
}

class QProductVariation(md: PathMetadata) extends RelationalPathImpl[QProductVariation](md, null, "product_variation") {
  def this(variable: String) = this(forVariable(variable))

  def this(parent: Path[_], property: String) = this(forProperty(parent, property))

  val color = createString("color")

  val id = createNumber[Long]("id")

  val productId = createNumber[Long]("productId")

  val size = createString("size")

  val primary: PrimaryKey[QProductVariation] = createPrimaryKey(id)

  addMetadata(color, ColumnMetadata.named("color").ofType(12).withSize(10).notNull())
  addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull())
  addMetadata(productId, ColumnMetadata.named("product_id").ofType(-5).withSize(19).notNull())
  addMetadata(size, ColumnMetadata.named("size").ofType(12).withSize(10).notNull())
}

