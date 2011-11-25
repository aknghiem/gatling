package com.excilys.ebi.gatling.http.request.builder.api

trait HttpRequestBuilderHeader extends HttpRequestBuilderContentType { this: AbstractHttpRequestBuilder[_] =>
	/**
	 * Adds a header to the request
	 *
	 * @param header the header to add, eg: ("Content-Type", "application/json")
	 */
	def header(header: (String, String)) = newInstanceWithHeader(header)
}