package uk.gov.hmrc.ngchelptosavecontract.http

import java.net.URL

import io.lemonlabs.uri.dsl._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.ngchelptosavecontract.http.UriPathEncoding.encodePathSegment
import uk.gov.hmrc.ngchelptosavecontract.support.ScalaUriConfig.uriConfig
import uk.gov.hmrc.ngchelptosavecontract.support.authloginapi.AuthLoginApiConnector
import uk.gov.hmrc.ngchelptosavecontract.support.{ServicesConfig, TestWSHttp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


case class HttpRequests(wsClient: WSClient, config: ServicesConfig = new ServicesConfig) {

  private val http: TestWSHttp = new TestWSHttp(wsClient)
  private val auth: AuthLoginApiConnector = new AuthLoginApiConnector(config, wsClient)
  private val helpToSaveBaseUrl: URL = new URL(config.baseUrl("help-to-save"))
  private val enrolmentStatusUrl = new URL(helpToSaveBaseUrl, "/help-to-save/enrolment-status")
  private val createAccountUrl = new URL(helpToSaveBaseUrl, "/help-to-save/create-account")

  def enrolmentStatus()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[JsValue](enrolmentStatusUrl.toString).map { json: JsValue =>
      (json \ "enrolled").as[Boolean]
    }

  def createAccount(nino: Nino)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    val createAccountRequest = Json.parse(
      s"""
         |{
         |  "userInfo": {
         |    "forename": "TestForename",
         |    "surname": "TestSurname",
         |    "dateOfBirth": "19500101",
         |    "nino": "${nino.value}",
         |    "contactDetails": {
         |      "address1": "1",
         |      "address2": "2",
         |      "postcode": "AA1 1AA",
         |      "communicationPreference": "00"
         |    },
         |    "registrationChannel": "online"
         |  },
         |  "eligibilityReason": 6,
         |  "source": "Digital"
         |}
      """.stripMargin
    )
    http.POST[JsValue, HttpResponse](createAccountUrl.toString, createAccountRequest).map { _ => () }
  }

  def governmentGatewayLoginFor(nino:Nino)(implicit rec: ExecutionContext): Future[HeaderCarrier] = {
    auth.governmentGatewayLogin(Some(nino))
  }

  private val SystemId = "MDTP-MOBILE"

  private def accountUrlForNino(nino:String, systemId: Option[String]): URL = {
    val urlPart = s"/help-to-save/${encodePathSegment(nino)}/account"
    new URL(helpToSaveBaseUrl, urlPart ? ("systemId" -> systemId))
  }

  def getAccountFor(nino: Nino, systemId: Option[String] = Some(SystemId))(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsValue] = {
    http.GET[JsValue](accountUrlForNino(nino.value, systemId).toString)
  }

  private def transactionsUrlForNino(nino: String, systemId: Option[String]): URL = {
    val urlPart = s"/help-to-save/${encodePathSegment(nino)}/account/transactions"
    new URL(helpToSaveBaseUrl, urlPart ? ("systemId" -> systemId))
  }

  def getTransactionsFor(nino: Nino, systemId: Option[String] = Some(SystemId))(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsValue] = {
    http.GET[JsValue](transactionsUrlForNino(nino.value, systemId).toString)
  }
}
