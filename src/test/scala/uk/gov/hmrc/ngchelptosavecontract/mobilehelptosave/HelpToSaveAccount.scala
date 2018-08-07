/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngchelptosavecontract.mobilehelptosave

import org.joda.time.{LocalDate, YearMonth}
import play.api.libs.json._
import uk.gov.hmrc.ngchelptosavecontract.mobilehelptosave.json.Formats.JodaYearMonthFormat


import scala.collection.Seq

case class BonusTerm(
  bonusEstimate: BigDecimal,
  bonusPaid: BigDecimal,
  endDate: LocalDate,
  bonusPaidOnOrAfterDate: LocalDate
)

object BonusTerm {
  implicit val format: OFormat[BonusTerm] = Json.format[BonusTerm]
}

case class Blocking(
  unspecified: Boolean
)

object Blocking {
  implicit val format: OFormat[Blocking] = Json.format[Blocking]
}

/** Account in help-to-save microservice's domain */
case class HelpToSaveAccount(
  accountNumber: String,
  openedYearMonth: YearMonth,

  isClosed: Boolean,

  blocked: Blocking,

  balance: BigDecimal,

  paidInThisMonth: BigDecimal,
  canPayInThisMonth: BigDecimal,
  maximumPaidInThisMonth: BigDecimal,
  thisMonthEndDate: LocalDate,

  accountHolderForename: String,
  accountHolderSurname: String,
  accountHolderEmail: Option[String],

  bonusTerms: Seq[BonusTerm],

  closureDate: Option[LocalDate],
  closingBalance: Option[BigDecimal]
)

object HelpToSaveAccount {
  implicit val format: OFormat[HelpToSaveAccount] = Json.format[HelpToSaveAccount]
}
