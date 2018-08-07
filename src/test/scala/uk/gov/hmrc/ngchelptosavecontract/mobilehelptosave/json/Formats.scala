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

package uk.gov.hmrc.ngchelptosavecontract.mobilehelptosave.json

import org.joda.time.YearMonth
import play.api.libs.json._

object Formats {
  implicit object JodaYearMonthFormat extends Format[YearMonth] {
    def writes(yearMonth: YearMonth): JsValue = JsString(yearMonth.toString)

    override def reads(json: JsValue): JsResult[YearMonth] = json match {
      case JsString(s) =>
        try {
          JsSuccess(YearMonth.parse(s))
        } catch {
          case _: IllegalArgumentException => JsError("error.expected.jodayearmonth.format")
        }

      case _ => JsError("error.expected.yearmonth")
    }
  }
}
