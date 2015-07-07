package sample.model

import com.querydsl.core.types._
import com.querydsl.scala._

import com.querydsl.core.types.PathMetadataFactory._;

import com.querydsl.scala.sql.RelationalPathImpl

import com.querydsl.sql._

object QProduct extends QProduct("qProduct") {
  override def as(variable: String) = new QProduct(variable)
  
}

class QProduct(md: PathMetadata) extends RelationalPathImpl[QProduct](md, null, "product") {
  def this(variable: String) = this(forVariable(variable))

  def this(parent: Path[_], property: String) = this(forProperty(parent, property))

  val id = createNumber[Long]("id")

  val name = createString("name")

  val price = createNumber[Long]("price")

  val releaseDate = createDateTime[java.sql.Timestamp]("releaseDate")

  val primary: PrimaryKey[QProduct] = createPrimaryKey(id)

  addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull())
  addMetadata(name, ColumnMetadata.named("name").ofType(12).withSize(30).notNull())
  addMetadata(price, ColumnMetadata.named("price").ofType(3).withSize(10).notNull())
  addMetadata(releaseDate, ColumnMetadata.named("release_date").ofType(93).withSize(19).notNull())
}

