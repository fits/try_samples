package sample.repository

import cats.data.NonEmptyList

import sample.model.InventoryItem
import sample.model.common.ItemId

object common {
  type RepoValid[A] = Either[NonEmptyList[String], A]
}

import common._

trait InventoryRepository {
  def store(s: InventoryItem): RepoValid[InventoryItem]
  def find(id: ItemId): RepoValid[Option[InventoryItem]]
}
