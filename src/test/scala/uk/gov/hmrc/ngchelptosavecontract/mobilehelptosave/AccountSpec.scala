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

import org.scalatest.{AsyncWordSpec, Matchers}
import play.api.Play
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, NotFoundException}
import uk.gov.hmrc.ngchelptosavecontract.scalatest.WSClientSpec
import uk.gov.hmrc.ngchelptosavecontract.support.authloginapi.LoginSupport

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Tests that check that the /help-to-save/:nino/account in help-to-save
  * meets the contract that mobile-help-to-save expects of it
  *
  * The test date used in this test can be viewed here:
  * confluence /pages/viewpage.action?pageId=101649537
  */
class AccountSpec extends AsyncWordSpec
  with Matchers
  with FutureAwaits
  with DefaultAwaitTimeout
  with WSClientSpec
  with LoginSupport {

  val beth = Nino("EM000001A")
  val accountClosedNino = Nino("EM000010A")
  val accountMissingNino = Nino("EM111111A")

  "/help-to-save/{nino}/account" should {

    s"return 400 when there is no system id provided" in {

      withLoggedInUser(beth) { implicit headerCarrier =>

        val badRequestException = intercept[BadRequestException](await(httpRequests.getAccountFor(beth, systemId = None)))
        badRequestException.responseCode shouldBe 400
      }
    }

    s"return 404 when there is no associated Help to Save account" in {

      withLoggedInUser(accountMissingNino) { implicit hc =>

        val notFoundException = intercept[NotFoundException](getAccountFor(accountMissingNino))
        notFoundException.responseCode shouldBe 404
      }
    }

    s"return account for Beth's nino ($beth) (happy path)" in {

      withLoggedInUser(beth) { implicit hc =>

        val account = getAccountFor(beth)
        // we don't exhaustively check values for simplicity because help-to-save-stub returns dates relative to the current date
        // checking that the value can be parsed into the case class used by mobile-help-to-save should give sufficient reassurance
        account.isClosed shouldBe false
        account.balance shouldBe BigDecimal(250)
      }
    }

    s"return closed account ($accountClosedNino)" in {

      withLoggedInUser(accountClosedNino) { implicit hc =>

        val account = getAccountFor(accountClosedNino)
        account.isClosed shouldBe true
        account.closureDate should not be None
        account.closingBalance should not be None
      }
    }
  }


  private def getAccountFor(nino: Nino)(implicit hc: HeaderCarrier, ec: ExecutionContext): Account = {
    await(httpRequests.getAccountFor(nino)).as[Account]
  }

  // Workaround to prevent ClassCastException being thrown when setting up Play.xercesSaxParserFactory when "test" is run twice in the same sbt session.
  Play
}
