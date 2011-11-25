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

import java.io.File

import org.fusesource.scalate.support.ScalaCompiler
import org.fusesource.scalate.{ TemplateEngine, Binding }

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.resource.ResourceRegistry
import com.excilys.ebi.gatling.core.util.FileHelper.SSP_EXTENSION
import com.excilys.ebi.gatling.core.util.PathHelper.{ GATLING_TEMPLATES_FOLDER, GATLING_REQUEST_BODIES_FOLDER }
import com.excilys.ebi.gatling.core.util.StringHelper.interpolate
import com.excilys.ebi.gatling.http.action.HttpRequestActionBuilder
import com.excilys.ebi.gatling.http.request.{ TemplateBody, StringBody, HttpRequestBody, FilePathBody }
import com.ning.http.client.RequestBuilder

object AbstractHttpRequestWithBodyBuilder {
	val ENGINE = new TemplateEngine(List(new File(GATLING_TEMPLATES_FOLDER)))
	ENGINE.allowReload = false
	ENGINE.escapeMarkup = false
	// Register engine shutdown
	ResourceRegistry.registerOnCloseCallback(() => ENGINE.compiler.asInstanceOf[ScalaCompiler].compiler.askShutdown)
}

/**
 * This class serves as model to HTTP request with a body
 *
 * @param httpRequestActionBuilder the HttpRequestActionBuilder with which this builder is linked
 * @param urlFunction the function returning the url
 * @param queryParams the query parameters that should be added to the request
 * @param headers the headers that should be added to the request
 * @param body the body that should be added to the request
 * @param followsRedirects sets the follow redirect option of AHC
 * @param credentials sets the credentials in case of Basic HTTP Authentication
 */
abstract class AbstractHttpRequestWithBodyBuilder[B <: AbstractHttpRequestWithBodyBuilder[B]](httpRequestActionBuilder: HttpRequestActionBuilder, method: String, urlFunction: Context => String,
	queryParams: List[(Context => String, Context => String)], headers: Map[String, String], body: Option[HttpRequestBody], followsRedirects: Option[Boolean], credentials: Option[(String, String)])
		extends AbstractHttpRequestBuilder[B](httpRequestActionBuilder, method, urlFunction, queryParams, headers, followsRedirects, credentials) {

	def newInstanceWithQueryParam(paramKeyFunc: Context => String, paramValueFunc: Context => String): B with HttpRequestBuilderQueryParam with HttpRequestBuilderBody

	def newInstanceWithHeaders(givenHeaders: Map[String, String]): B with HttpRequestBuilderHeader with HttpRequestBuilderBody

	def newInstanceWithHeader(header: (String, String)): B with HttpRequestBuilderContentType with HttpRequestBuilderBody

	def newInstanceWithContentType(mimeType: String): B with HttpRequestBuilderBody

	def newInstanceWithStringBody(body: String): B with HttpRequestBuilderOptions

	def newInstanceWithFileBody(filePath: String): B with HttpRequestBuilderOptions

	def newInstanceWithTemplateBody(tplPath: String, values: Map[String, Context => String]): B with HttpRequestBuilderOptions

	def newInstanceWithFollowsRedirect(followRedirect: Boolean): B

	def newInstanceWithCredentials(username: String, password: String): B
	override def getRequestBuilder(context: Context): RequestBuilder = {
		val requestBuilder = super.getRequestBuilder(context)
		requestBuilder setMethod method
		addBodyTo(requestBuilder, body, context)
		requestBuilder
	}

	/**
	 * This method adds the body to the request builder
	 *
	 * @param requestBuilder the request builder to which the body should be added
	 * @param body the body that should be added
	 * @param context the context of the current scenario
	 */
	private def addBodyTo(requestBuilder: RequestBuilder, body: Option[HttpRequestBody], context: Context) = {
		body match {
			case Some(thing) =>
				thing match {
					case FilePathBody(filePath) => requestBuilder setBody new File(GATLING_REQUEST_BODIES_FOLDER + "/" + filePath)
					case StringBody(body) => requestBuilder setBody body
					case TemplateBody(tplPath, values) => requestBuilder setBody compileBody(tplPath, values, context)
					case _ =>
				}
			case None =>
		}
	}

	/**
	 * This method compiles the template for a TemplateBody
	 *
	 * @param tplPath the path to the template relative to GATLING_TEMPLATES_FOLDER
	 * @param values the values that should be merged into the template
	 * @param context the context of the current scenario
	 */
	private def compileBody(tplPath: String, values: Map[String, Context => String], context: Context): String = {

		val bindings = for (value <- values) yield Binding(value._1, "String")
		val templateValues = for (value <- values) yield (value._1 -> (value._2(context)))

		AbstractHttpRequestWithBodyBuilder.ENGINE.layout(tplPath + SSP_EXTENSION, templateValues, bindings)
	}
}