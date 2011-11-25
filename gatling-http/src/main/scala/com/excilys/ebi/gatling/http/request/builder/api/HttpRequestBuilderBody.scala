package com.excilys.ebi.gatling.http.request.builder.api

import com.excilys.ebi.gatling.core.util.StringHelper.interpolate

trait HttpRequestBuilderBody { this: AbstractHttpRequestWithBodyBuilder[_] =>
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