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
import uk.gov.hmrc.domain.Generator
import uk.gov.hmrc.ngchelptosavecontract.scalatest.WSClientSpec
import uk.gov.hmrc.ngchelptosavecontract.support.authloginapi.LoginSupport

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Tests that check that the /help-to-save/enrolment-status in help-to-save
  * meets the contract that mobile-help-to-save expects of it
  */
class EnrolmentStatusSpec
  extends AsyncWordSpec
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WSClientSpec
    with LoginSupport {


  private val generator = new Generator(0)

  "/help-to-save/enrolment-status" should {

    """include "enrolled": false when user is not enrolled in Help to Save""" in {
      withLoggedInUser(generator.nextNino) { implicit hc =>
        await(httpRequests.enrolmentStatus) shouldBe false
      }
    }

    """include "enrolled": true when user is enrolled in Help to Save""" in {
      val nino = generator.nextNino

      withLoggedInUser(nino) { implicit hc =>

        val enrolledUsersStatus = httpRequests.createAccount(nino).flatMap(_ => httpRequests.enrolmentStatus())
        await(enrolledUsersStatus) shouldBe true
      }
    }
  }

  // Workaround to prevent ClassCastException being thrown when setting up Play.xercesSaxParserFactory when "test" is run twice in the same sbt session.
  Play
}
