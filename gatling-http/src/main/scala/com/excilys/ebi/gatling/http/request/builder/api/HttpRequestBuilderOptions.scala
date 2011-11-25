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