/**
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
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
package com.excilys.ebi.gatling.http.request.builder

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.http.action.HttpRequestActionBuilder
import com.excilys.ebi.gatling.http.request.builder.api.AbstractHttpRequestBuilder
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderHeaders
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderHeader
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderContentType
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderOptions
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderQueryParam

/**
 * This class defines an HTTP request with word GET in the DSL
 */
class GetHttpRequestBuilder(httpRequestActionBuilder: HttpRequestActionBuilder, urlFunction: Context => String, queryParams: List[(Context => String, Context => String)],
	headers: Map[String, String], followsRedirects: Option[Boolean], credentials: Option[(String, String)])
		extends AbstractHttpRequestBuilder[GetHttpRequestBuilder](httpRequestActionBuilder, "GET", urlFunction, queryParams, headers, followsRedirects, credentials) {

	def newInstanceWithQueryParam(paramKeyFunc: Context => String, paramValueFunc: Context => String) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, (paramKeyFunc, paramValueFunc) :: queryParams, headers, followsRedirects, credentials) with HttpRequestBuilderQueryParam

	def newInstanceWithHeaders(givenHeaders: Map[String, String]) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers ++ givenHeaders, followsRedirects, credentials) with HttpRequestBuilderHeader

	def newInstanceWithHeader(header: (String, String)) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers + (header._1 -> header._2), followsRedirects, credentials) with HttpRequestBuilderContentType

	def newInstanceWithContentType(mimeType: String) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers ++ Map(ACCEPT -> mimeType, CONTENT_TYPE -> mimeType), followsRedirects, credentials) with HttpRequestBuilderOptions

	def newInstanceWithFollowsRedirect(followRedirect: Boolean) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, Some(followRedirect), credentials)

	def newInstanceWithCredentials(username: String, password: String) =
		new GetHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, followsRedirects, Some((username, password)))
}