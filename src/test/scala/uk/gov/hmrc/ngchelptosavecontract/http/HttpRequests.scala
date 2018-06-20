package uk.gov.hmrc.ngchelptosavecontract.http

import java.net.URL

import io.lemonlabs.uri.dsl._
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{CoreGet, HeaderCarrier}
import uk.gov.hmrc.ngchelptosavecontract.http.UriPathEncoding.encodePathSegment
import uk.gov.hmrc.ngchelptosavecontract.support.authloginapi.AuthLoginApiConnector
import uk.gov.hmrc.ngchelptosavecontract.support.{ServicesConfig, TestWSHttp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


case class HttpRequests(wsClient: WSClient, config: ServicesConfig = new ServicesConfig) {

  private val http: CoreGet = new TestWSHttp(wsClient)
  private val auth: AuthLoginApiConnector = new AuthLoginApiConnector(config, wsClient)
  private val helpToSaveBaseUrl: URL = new URL(config.baseUrl("help-to-save"))

  def governmentGatewayLoginFor(nino:Nino)(implicit rec: ExecutionContext): Future[HeaderCarrier] = {
    auth.governmentGatewayLogin(Some(nino))
  }

  private def transactionsUrlForNino(nino:String, systemId:Option[String]) : URL = {
    val urlPart = s"/help-to-save/${encodePathSegment(nino)}/account/transactions"
    systemId match {
      case Some(sid) => new URL(helpToSaveBaseUrl, urlPart ? ("systemId" -> systemId))
      case _ => new URL(helpToSaveBaseUrl, urlPart)
    }
  }

  private val SystemId = "MDTP-MOBILE"

  def getTransactionsFor(nino:Nino, systemId:Option[String] = Some(SystemId))(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsValue] = {
    http.GET[JsValue](transactionsUrlForNino(nino.value, systemId).toString)
  }
}
