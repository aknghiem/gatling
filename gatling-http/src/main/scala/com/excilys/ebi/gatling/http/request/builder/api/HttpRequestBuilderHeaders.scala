package com.excilys.ebi.gatling.http.request.builder.api

trait HttpRequestBuilderHeaders extends HttpRequestBuilderHeader { this: AbstractHttpRequestBuilder[_] =>
	/**
	 * Adds several headers to the request at the same time
	 *
	 * @param givenHeaders a scala map containing the headers to add
	 */
	def headers(givenHeaders: Map[String, String]) = newInstanceWithHeaders(givenHeaders)
}