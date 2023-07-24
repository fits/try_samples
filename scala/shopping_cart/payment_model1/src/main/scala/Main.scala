
type Amount = BigDecimal

enum Direct:
  case Cash(value: Amount)
  case Voucher(value: Amount, faceValue: Amount, kind: String)

enum Indirect:
  case PaymentService(value: Amount, paymentId: String)

enum Method:
  case Receive(body: Direct)
  case Return(body: Direct)
  case Contract(body: Indirect)
  case Cancel(body: Indirect)
  case Then(a: Method, b: Method)

case class Payment(amount: Amount, by: Method)

extension (d: Direct)
  def value(): Amount = d match
    case Direct.Cash(v) => v
    case Direct.Voucher(v, _, _) => v

extension (d: Indirect)
  def value(): Amount = d match
    case Indirect.PaymentService(v, _) => v

extension (p: Payment)  
  def diff(): Amount = p.amount - calc(p.by)
  def validate(): Boolean = p.diff() == 0

  private def calc(m: Method): Amount = m match
    case Method.Receive(b) => b.value()
    case Method.Return(b) => -b.value()
    case Method.Contract(b) => b.value()
    case Method.Cancel(b) => -b.value()
    case Method.Then(a, b) => calc(a) + calc(b)

def printPayment(p: Payment) =
  println(s"${p}, valid: ${p.validate()}, diff: ${p.diff()}")

@main def main(): Unit =
  import Method.*
  import Direct.*
  import Indirect.*

  val p1 = Payment(800, Receive(Voucher(800, 1000, "1000 off")))
  printPayment(p1)

  val p2 = Payment(800, Then(Receive(Cash(1000)), Return(Cash(200))))
  printPayment(p2)

  val p3 = Payment(-2000, Then(Cancel(PaymentService(1500, "p01")), Return(Cash(500))))
  printPayment(p3)

  printPayment(Payment(600, Receive(Cash(500))))
