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

import java.time.LocalDate

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
  * Tests that check that the /help-to-save/:nino/account/transactions in help-to-save
  * meets the contract that mobile-help-to-save expects of it
  *
  * The test date used in this test can be viewed here:
  * confluence /pages/viewpage.action?pageId=113218311
  */
class TransactionsSpec extends AsyncWordSpec
  with Matchers
  with FutureAwaits
  with DefaultAwaitTimeout
  with WSClientSpec
  with LoginSupport {

  val beth = Nino("EM000001A")
  val accountMissingNino = Nino("EM111111A")

  "/help-to-save/{nino}/account/transactions" should {

    s"return 400 when there is no system id provided" in {

      withLoggedInUser(beth) { implicit headerCarrier =>

        val badRequestException = intercept[BadRequestException](await(httpRequests.getTransactionsFor(beth, systemId = None)))
        badRequestException.responseCode shouldBe 400
      }
    }

    s"return 404 when there is no associated Help to Save account" in {

      withLoggedInUser(accountMissingNino) { implicit hc =>

        val notFoundException = intercept[NotFoundException](getTransactionsFor(accountMissingNino))
        notFoundException.responseCode shouldBe 404
      }
    }

    s"return transactions for Beth's nino ($beth) (happy path)" in {

      withLoggedInUser(beth) { implicit hc =>

        val transactions = getTransactionsFor(beth)

        transactions.head shouldBe Transaction(
          operation = Credit,
          amount = BigDecimal(50L),
          transactionDate = dateOf(2017, 11, 7),
          accountingDate = dateOf(2017, 11, 7),
          balanceAfter = BigDecimal(50L)
        )

        transactions(1) shouldBe Transaction(
          operation = Credit,
          amount = BigDecimal(50L),
          transactionDate = dateOf(2017, 12, 9),
          accountingDate = dateOf(2017, 12, 9),
          balanceAfter = BigDecimal(100L)
        )

        transactions(2) shouldBe Transaction(
          operation = Credit,
          amount = BigDecimal(50L),
          transactionDate = dateOf(2018, 1, 16),
          accountingDate = dateOf(2018, 1, 16),
          balanceAfter = BigDecimal(150L)
        )

        transactions(3) shouldBe Transaction(
          operation = Credit,
          amount = BigDecimal(50L),
          transactionDate = dateOf(2018, 2, 3),
          accountingDate = dateOf(2018, 2, 3),
          balanceAfter = BigDecimal(200L)
        )

        transactions(4) shouldBe Transaction(
          operation = Credit,
          amount = BigDecimal(50L),
          transactionDate = dateOf(2018, 3, 1),
          accountingDate = dateOf(2018, 3, 1),
          balanceAfter = BigDecimal(250L)
        )
      }
    }


  }


  private def getTransactionsFor(nino: Nino)(implicit hc: HeaderCarrier, ec: ExecutionContext): Seq[Transaction] = {
    await(httpRequests.getTransactionsFor(nino)).as[Transactions].transactions
  }

  private def dateOf(year: Int, month: Int, day: Int) = {
    LocalDate.of(year, month, day)
  }

  Play
}
