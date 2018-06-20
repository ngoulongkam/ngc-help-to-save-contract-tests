package uk.gov.hmrc.ngchelptosavecontract.http

import play.utils.UriEncoding
import java.nio.charset.StandardCharsets

object UriPathEncoding {

  def encodePathSegments(pathSegments: String*): String =
    pathSegments.map(encodePathSegment).mkString("/", "/", "")

  def encodePathSegment(pathSegment: String): String =
    UriEncoding.encodePathSegment(pathSegment, StandardCharsets.UTF_8.name)

}