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
package com.excilys.ebi.gatling.http.request.builder.api

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.http.action.HttpRequestActionBuilder
import com.excilys.ebi.gatling.http.request.HttpRequestBody
import com.ning.http.client.RequestBuilder
import com.ning.http.client.FluentStringsMap
import com.excilys.ebi.gatling.core.util.StringHelper._

/**
 * This class serves as model to HTTP request with a body and parameters
 *
 * @param httpRequestActionBuilder the HttpRequestActionBuilder with which this builder is linked
 * @param urlFunction the function returning the url
 * @param queryParams the query parameters that should be added to the request
 * @param params the parameters that should be added to the request
 * @param headers the headers that should be added to the request
 * @param body the body that should be added to the request
 * @param followsRedirects sets the follow redirect option of AHC
 * @param credentials sets the credentials in case of Basic HTTP Authentication
 */
abstract class AbstractHttpRequestWithBodyAndParamBuilder[B <: AbstractHttpRequestWithBodyAndParamBuilder[B]](httpRequestActionBuilder: HttpRequestActionBuilder, method: String,
	urlFunction: Context => String, queryParams: List[(Context => String, Context => String)], params: List[(Context => String, Context => String)], headers: Map[String, String], body: Option[HttpRequestBody],
	followsRedirects: Option[Boolean], credentials: Option[(String, String)])
		extends AbstractHttpRequestWithBodyBuilder[B](httpRequestActionBuilder, method, urlFunction, queryParams, headers, body, followsRedirects, credentials) {

	def newInstanceWithStringBody(body: String): B with HttpRequestBuilderParam

	def newInstanceWithFileBody(filePath: String): B with HttpRequestBuilderParam

	def newInstanceWithTemplateBody(tplPath: String, values: Map[String, Context => String]): B with HttpRequestBuilderParam

	def newInstanceWithParam(paramKeyFunc: Context => String, paramValueFunc: Context => String): B with HttpRequestBuilderOptions

	override def getRequestBuilder(context: Context): RequestBuilder = {
		val requestBuilder = super.getRequestBuilder(context)
		addParamsTo(requestBuilder, context)
		requestBuilder
	}

	/**
	 * This method adds the parameters to the request builder
	 *
	 * @param requestBuilder the request builder to which the parameters should be added
	 * @param params the parameters that should be added
	 * @param context the context of the current scenario
	 */
	private def addParamsTo(requestBuilder: RequestBuilder, context: Context) = {
		val paramsMap = new FluentStringsMap

		val keyValues = for ((keyFunction, valueFunction) <- params) yield (keyFunction.apply(context), valueFunction.apply(context))

		keyValues.groupBy(entry => entry._1).foreach { entry =>
			val (key, values) = entry
			paramsMap.add(key, values.map { value => value._2 }: _*)
		}

		if (!paramsMap.isEmpty) // FIXME patch AHC so that it won't see parameters if map size == 0
			requestBuilder setParameters paramsMap
	}
}