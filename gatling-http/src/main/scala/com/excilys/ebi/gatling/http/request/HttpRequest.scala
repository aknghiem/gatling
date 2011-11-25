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
package com.excilys.ebi.gatling.http.request

import com.excilys.ebi.gatling.core.action.request.Request
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.http.request.builder.AbstractHttpRequestBuilder
import com.ning.http.client.{ Request => AHCRequest }
import com.excilys.ebi.gatling.http.request.builder.n.HttpRequestBuilder

/**
 * This class wraps an HTTP Request
 *
 * @param givenName the name of the request
 * @param httpRequestBuilder the httpRequestBuilder that will build the actual HTTP request
 */
class HttpRequest(givenName: String, val httpRequestBuilder: HttpRequestBuilder) extends Request(givenName) with Logging {

	/**
	 * Builds and returns the actual Request
	 *
	 * @param context the context of the scenario
	 * @return an HTTP request
	 */
	def getRequest(context: Context): AHCRequest = {
		val request = httpRequestBuilder build context
		logger.debug("Request created: {}, body data: {}", request.getUrl(), request.getStringData())
		request
	}

	override def toString = "[HttpRequest] " + name
}