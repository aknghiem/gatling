package com.excilys.ebi.gatling.http.request.builder.api

trait HttpRequestBuilderOptions { this: AbstractHttpRequestBuilder[_] =>
	/**
	 * Sets the follow redirect option that will be applied on AHC
	 *
	 * @param followRedirect a boolean that activates (true) or deactivates (false) the follow redirect option
	 */
	def followsRedirect(followRedirect: Boolean) = newInstanceWithFollowsRedirect(followRedirect)

	/**
	 * Adds BASIC authentication to the request
	 *
	 * @param username the username needed
	 * @param password the password needed
	 */
	def basicAuth(username: String, password: String) = newInstanceWithCredentials(username, password)

}