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
import com.excilys.ebi.gatling.http.request.HttpRequestBody
import com.excilys.ebi.gatling.http.request.builder.api.AbstractHttpRequestWithBodyBuilder
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderHeaders
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderHeader
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderContentType
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderOptions
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.http.request._
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderBody
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderQueryParam

/**
 * This class defines an HTTP request with word DELETE in the DSL
 */
class DeleteHttpRequestBuilder(httpRequestActionBuilder: HttpRequestActionBuilder, urlFunction: Context => String, queryParams: List[(Context => String, Context => String)],
	headers: Map[String, String], body: Option[HttpRequestBody], followsRedirects: Option[Boolean], credentials: Option[(String, String)])
		extends AbstractHttpRequestWithBodyBuilder[DeleteHttpRequestBuilder](httpRequestActionBuilder, "DELETE", urlFunction, queryParams, headers, body, followsRedirects, credentials) {

	def newInstanceWithQueryParam(paramKeyFunc: Context => String, paramValueFunc: Context => String) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, (paramKeyFunc, paramValueFunc) :: queryParams, headers, body, followsRedirects, credentials) with HttpRequestBuilderQueryParam

	def newInstanceWithHeaders(givenHeaders: Map[String, String]) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers ++ givenHeaders, body, followsRedirects, credentials) with HttpRequestBuilderHeader

	def newInstanceWithHeader(header: (String, String)) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers + (header._1 -> header._2), body, followsRedirects, credentials) with HttpRequestBuilderContentType

	def newInstanceWithContentType(mimeType: String) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers ++ Map(ACCEPT -> mimeType, CONTENT_TYPE -> mimeType), body, followsRedirects, credentials) with HttpRequestBuilderBody

	def newInstanceWithFollowsRedirect(followRedirect: Boolean) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, body, Some(followRedirect), credentials)

	def newInstanceWithCredentials(username: String, password: String) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, body, followsRedirects, Some((username, password)))

	def newInstanceWithStringBody(body: String) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, Some(StringBody(body)), followsRedirects, credentials) with HttpRequestBuilderOptions

	def newInstanceWithFileBody(filePath: String) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, Some(FilePathBody(filePath)), followsRedirects, credentials) with HttpRequestBuilderOptions

	def newInstanceWithTemplateBody(tplPath: String, values: Map[String, Context => String]) =
		new DeleteHttpRequestBuilder(httpRequestActionBuilder, urlFunction, queryParams, headers, Some(TemplateBody(tplPath, values)), followsRedirects, credentials) with HttpRequestBuilderOptions
}
