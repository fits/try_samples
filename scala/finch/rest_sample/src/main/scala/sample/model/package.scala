package sample

import java.time.OffsetDateTime

package object model {
  type ItemId = String
  type Amount = BigDecimal
  type DateTime = OffsetDateTime

  def now: DateTime = OffsetDateTime.now()
}
