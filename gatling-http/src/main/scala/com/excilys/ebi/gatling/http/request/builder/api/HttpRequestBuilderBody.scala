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

import com.excilys.ebi.gatling.core.util.StringHelper.interpolate

trait HttpRequestBuilderBody extends HttpRequestBuilderOptions { this: AbstractHttpRequestWithBodyBuilder[_] =>
	/**
	 * Adds a body from a file to the request
	 *
	 * @param filePath the path of the file relative to GATLING_REQUEST_BODIES_FOLDER
	 */
	def withFile(filePath: String) = newInstanceWithFileBody(filePath)

	/**
	 * Adds a body to the request
	 *
	 * @param body a string containing the body of the request
	 */
	def withBody(body: String) = newInstanceWithStringBody(body)

	/**
	 * Adds a body from a template that has to be compiled
	 *
	 * @param tplPath the path to the template relative to GATLING_TEMPLATES_FOLDER
	 * @param values the values that should be merged into the template
	 */
	def withTemplateBody(tplPath: String, values: Map[String, String]) = {
		val interpolatedValues = values.map { entry => entry._1 -> interpolate(entry._2) }
		newInstanceWithTemplateBody(tplPath, interpolatedValues)
	}
}