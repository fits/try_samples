package sample.repository.interpreter

import java.time.OffsetDateTime

import sample.model.{Amount, DateTime, Item, ItemId}
import sample.repository.ItemRepository
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Items(tag: Tag) extends Table[Item](tag, "items") {
  implicit val mapper = MappedColumnType.base[DateTime, String](
    t => t.toString,
    s => OffsetDateTime.parse(s)
  )

  def id = column[ItemId]("id", O.PrimaryKey)
  def name = column[String]("name")
  def price = column[Amount]("price")
  def created = column[DateTime]("created")

  override def * = (id, name, price, created) <> (Item.tupled, Item.unapply)
}

object Items {
  val items = TableQuery[Items]
}

object DbItemRepositoryInterpreter extends ItemRepository {
  val db = Database.forConfig("sample")

  val setup = DBIO.seq(Items.items.schema.create)
  val setupFuture = db.run(setup)

  val find = Compiled { id: Rep[ItemId] =>
    Items.items.filter(_.id === id)
  }

  override def store(item: Item): Future[Item] =
    db.run(DBIO.seq( Items.items += item)).map(_ => item)

  override def restore(id: ItemId): Future[Option[Item]] =
    db.run(find(id).result).map(_.headOption)

  override def delete(id: ItemId): Future[Option[Item]] = db.run(
    for {
      s <- find(id).result
      _ <- find(id).delete
    } yield s.headOption
  )
}
