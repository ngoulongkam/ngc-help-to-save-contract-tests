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

import uk.gov.hmrc.domain.{Generator, Nino}

trait HelpToSaveStubNinos {

  // random seed for generator chosen such that none of the NINOs include "401" or "500" which trigger 401 / 500 responses from the stub
  private val generator = new Generator(2)
  val beth = Nino("EM0" + generator.nextNino.value.substring(3, 5) + "001A")
  val accountClosedNino = Nino("EM0" + generator.nextNino.value.substring(3, 5) + "010A")
  val accountMissingNino = Nino("EM1" + generator.nextNino.value.substring(3, 5) + "111A")
  val accountBlockedNino = Nino("EM0" + generator.nextNino.value.substring(3, 5) + "011A")
  val ninoWith48MonthsWorthOfTransactions = Nino("EM0" + generator.nextNino.value.substring(3, 5) + "006A")

}
