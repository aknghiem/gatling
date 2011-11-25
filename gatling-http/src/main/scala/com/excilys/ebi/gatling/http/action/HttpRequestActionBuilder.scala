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
package com.excilys.ebi.gatling.http.action

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.action.builder.AbstractActionBuilder
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.feeder.Feeder
import com.excilys.ebi.gatling.core.util.StringHelper._
import com.excilys.ebi.gatling.http.request.HttpRequest
import com.excilys.ebi.gatling.http.request.builder.GetHttpRequestBuilder
import com.excilys.ebi.gatling.http.request.builder.PostHttpRequestBuilder
import com.excilys.ebi.gatling.http.request.builder.DeleteHttpRequestBuilder
import com.excilys.ebi.gatling.http.request.builder.PutHttpRequestBuilder
import akka.actor.TypedActor
import com.excilys.ebi.gatling.http.request.builder.api.AbstractHttpRequestBuilder
import com.excilys.ebi.gatling.http.check.HttpCheckBuilder
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderQueryParam
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderBody
import com.excilys.ebi.gatling.http.request.builder.api.HttpRequestBuilderParam

/**
 * HttpRequestActionBuilder class companion
 */
object HttpRequestActionBuilder {
	/**
	 * This method is used in DSL to declare a new HTTP request
	 */
	def http(requestName: String) = new HttpRequestActionBuilder(requestName, null, null, None, Some(Nil))
}

/**
 * Builder for HttpRequestActionBuilder
 *
 * @constructor creates an HttpRequestActionBuilder
 * @param requestName the name of the request
 * @param request the actual HTTP request that will be sent
 * @param next the next action to be executed
 * @param processorBuilders
 */
class HttpRequestActionBuilder(val requestName: String, request: HttpRequest, next: Action, processorBuilders: Option[List[HttpCheckBuilder[_]]], groups: Option[List[String]])
		extends AbstractActionBuilder {

	/**
	 * Adds givenProcessors to builder
	 *
	 * @param givenProcessors the processors specified by the user
	 * @return a new builder with givenProcessors set
	 */
	private[http] def withProcessors(givenProcessors: Seq[HttpCheckBuilder[_]]) = {
		new HttpRequestActionBuilder(requestName, request, next, Some(givenProcessors.toList ::: processorBuilders.getOrElse(Nil)), groups)
	}

	def withRequest(request: HttpRequest) = new HttpRequestActionBuilder(requestName, request, next, processorBuilders, groups)

	def withNext(next: Action) = new HttpRequestActionBuilder(requestName, request, next, processorBuilders, groups)

	def inGroups(groups: List[String]) = new HttpRequestActionBuilder(requestName, request, next, processorBuilders, Some(groups))

	def build: Action = {
		TypedActor.newInstance(classOf[Action], new HttpRequestAction(next, request, processorBuilders, groups.get))
	}

	/**
	 * Starts the definition of an HTTP request with word DELETE
	 *
	 * @param url the url on which this request will be made
	 * @param interpolations context keys for interpolation
	 */
	def delete(url: String): DeleteHttpRequestBuilder with HttpRequestBuilderQueryParam with HttpRequestBuilderBody = delete(interpolate(url))

	/**
	 * Starts the definition of an HTTP request with word DELETE
	 *
	 * @param f the function returning the url of this request
	 */
	def delete(f: Context => String) = new DeleteHttpRequestBuilder(this, f, Nil, Map(), None, None, None) with HttpRequestBuilderQueryParam with HttpRequestBuilderBody

	/**
	 * Starts the definition of an HTTP request with word GET
	 *
	 * @param url the url on which this request will be made
	 * @param interpolations context keys for interpolation
	 */
	def get(url: String): GetHttpRequestBuilder with HttpRequestBuilderQueryParam = get(interpolate(url))

	/**
	 * Starts the definition of an HTTP request with word GET
	 *
	 * @param f the function returning the url of this request
	 */
	def get(f: Context => String) = new GetHttpRequestBuilder(this, f, Nil, Map(), None, None) with HttpRequestBuilderQueryParam

	/**
	 * Starts the definition of an HTTP request with word POST
	 *
	 * @param url the url on which this request will be made
	 * @param interpolations context keys for interpolation
	 */
	def post(url: String): PostHttpRequestBuilder with HttpRequestBuilderQueryParam with HttpRequestBuilderBody with HttpRequestBuilderParam = post(interpolate(url))

	/**
	 * Starts the definition of an HTTP request with word POST
	 *
	 * @param f the function returning the url of this request
	 */
	def post(f: Context => String) = new PostHttpRequestBuilder(this, f, Nil, Nil, Map(), None, None, None) with HttpRequestBuilderQueryParam with HttpRequestBuilderBody with HttpRequestBuilderParam

	/**
	 * Starts the definition of an HTTP request with word PUT
	 *
	 * @param url the url on which this request will be made
	 * @param interpolations context keys for interpolation
	 */
	def put(url: String): PutHttpRequestBuilder with HttpRequestBuilderQueryParam with HttpRequestBuilderBody = put(interpolate(url))

	/**
	 * Starts the definition of an HTTP request with word PUT
	 *
	 * @param f the function returning the url of this request
	 */
	def put(f: Context => String) = new PutHttpRequestBuilder(this, f, Nil, Map(), None, None, None) with HttpRequestBuilderQueryParam with HttpRequestBuilderBody
}

