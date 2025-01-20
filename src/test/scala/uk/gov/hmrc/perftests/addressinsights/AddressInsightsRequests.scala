/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.addressinsights

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object AddressInsightsRequests extends ServicesConfiguration {

  val baseUrl: String         = baseUrlFor("address-gateway")
  val appContext: String      = "/address-gateway"
  val reputationRoute: String = "/reputation/sa-reg"
  val cacheRoute: String      = "/cache"

  val checkAddressInsightsViaGateway: HttpRequestBuilder =
    http("Check address-insights via gateway")
      .post(s"$baseUrl$appContext$reputationRoute")
      .header(HttpHeaderNames.ContentType, "application/json")
      .header(HttpHeaderNames.UserAgent, "ai-performance-tests")
      .body(
        StringBody(
          """{
            |"address": {
            |  "addressLine1": "30-31",
            |  "postcode": "BN2 1QB",
            |  "country": "GB"
            |},
            |"lookbackDays": 10
          |}""".stripMargin
        )
      )
      .check(status.is(200))
      .check(jsonPath("$.reputation").exists)
      .check(jsonPath("$.reputation.assessment.action").is("CHECK"))
      .check(
        jsonPath("$.reputation.assessment.reasons[*]").findAll
          .is(List("LONGER_TERM_RISK_120DAYS_POSTCODE_THRESHOLD_BREACHED"))
      )
}
