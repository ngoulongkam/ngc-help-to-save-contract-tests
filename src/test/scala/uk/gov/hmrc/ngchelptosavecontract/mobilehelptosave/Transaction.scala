package uk.gov.hmrc.ngchelptosavecontract.mobilehelptosave

import cats.Eq
import org.joda.time.LocalDate
import play.api.libs.json._

sealed trait Operation {
  def stringValue: String =  this match {
    case Credit => "credit"
    case Debit  => "debit"
  }
}
case object Credit extends Operation
case object Debit extends Operation

object Operation {
  implicit val eqOperation: Eq[Operation] = Eq.fromUniversalEquals
  implicit val writes: Writes[Operation] = new Writes[Operation] {
    override def writes(o: Operation): JsValue = JsString(o.stringValue)
  }

  implicit val reads: Reads[Operation] = new Reads[Operation] {
    override def reads(json: JsValue): JsResult[Operation] = json match {
      case JsString("credit") => JsSuccess(Credit)
      case JsString("debit") => JsSuccess(Debit)
      case JsString(unknown) => JsError(s"[$unknown] in not a valid Operation e.g. [credit|debit]")
      case unknown => JsError(s"Cannot parse $unknown to a valid Operation e.g. [credit|debit]")
    }
  }
}

case class Transaction(
  operation:            Operation,
  amount:               BigDecimal,
  transactionDate:      LocalDate,
  accountingDate:       LocalDate,
  balanceAfter:         BigDecimal
)

object Transaction {
  implicit val format: OFormat[Transaction] = Json.format[Transaction]
}

case class Transactions(transactions: Seq[Transaction])

object Transactions {

  implicit val format: OFormat[Transactions] = Json.format[Transactions]
}