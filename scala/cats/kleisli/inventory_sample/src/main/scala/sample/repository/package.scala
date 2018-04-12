package sample

import cats.data.NonEmptyList

package object repository {
  type RepoValid[A] = Either[NonEmptyList[String], A]
}
