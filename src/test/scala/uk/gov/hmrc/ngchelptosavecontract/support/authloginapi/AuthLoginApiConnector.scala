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

package uk.gov.hmrc.ngchelptosavecontract.support.authloginapi

import java.net.URL
import java.util.UUID

import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.ngchelptosavecontract.support.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

class AuthLoginApiConnector(servicesConfig: ServicesConfig, wsClient: WSClient) {

  private val baseUrl = new URL(servicesConfig.baseUrl("auth-login-api"))
  private val governmentGatewayLoginUrl = new URL(baseUrl, "/government-gateway/session/login")

  private implicit val writes: Writes[GovernmentGatewayLogin] = Json.writes[GovernmentGatewayLogin]

  def governmentGatewayLogin(nino: Option[Nino] = None)(implicit ec: ExecutionContext): Future[HeaderCarrier] = {
    val postBodyJson = Json.toJson(GovernmentGatewayLogin(
      CredId(UUID.randomUUID().toString),
      AffinityGroup.Individual,
      ConfidenceLevel.L200,
      CredentialStrength.Strong,
      Some(CredentialRole.User),
      Seq.empty[Enrolment],
      nino
    ))

    wsClient.url(governmentGatewayLoginUrl.toString).post(
      postBodyJson
    ).map { response =>
      if (response.status != 201) {
        throw new RuntimeException(s"auth-login-api returned unexpected status ${response.status}")
      } else {
        response.header("Authorization")
          .map(bearerToken => HeaderCarrier(Some(Authorization(bearerToken))))
          .getOrElse(throw new RuntimeException("auth-login-api did not return an Authorization header"))
      }
    }
  }
}
