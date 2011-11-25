package com.excilys.ebi.gatling.http.request.builder.api

import com.excilys.ebi.gatling.http.Predef._

trait HttpRequestBuilderContentType extends HttpRequestBuilderOptions { this: AbstractHttpRequestBuilder[_] =>
	/**
	 * Adds Accept and Content-Type headers to the request set with "application/json" values
	 */
	def asJSON() = newInstanceWithContentType(APPLICATION_JSON)

	/**
	 * Adds Accept and Content-Type headers to the request set with "application/xml" values
	 */
	def asXML() = newInstanceWithContentType(APPLICATION_XML)
}