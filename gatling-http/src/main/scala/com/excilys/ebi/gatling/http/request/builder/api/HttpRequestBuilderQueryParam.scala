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
import com.excilys.ebi.gatling.core.util.StringHelper._

trait HttpRequestBuilderQueryParam extends HttpRequestBuilderHeaders { this: AbstractHttpRequestBuilder[_] =>
	/**
	 * Adds a query parameter to the request
	 *
	 * @param paramKeyFunction a function that returns the key name
	 * @param paramValueFunction a function that returns the value
	 */
	def queryParam(paramKeyFunc: Context => String, paramValueFunc: Context => String): AbstractHttpRequestBuilder[_] with HttpRequestBuilderQueryParam =
		newInstanceWithQueryParam(paramKeyFunc, paramValueFunc).asInstanceOf[AbstractHttpRequestBuilder[_] with HttpRequestBuilderQueryParam]

	/**
	 * Adds a query parameter to the request
	 *
	 * Its key and value are set by the user
	 *
	 * @param paramKey the key of the parameter
	 * @param paramValue the value of the parameter
	 */
	def queryParam(paramKey: String, paramValue: String): AbstractHttpRequestBuilder[_] with HttpRequestBuilderQueryParam = queryParam(interpolate(paramKey), interpolate(paramValue))

	/**
	 * Adds a query parameter to the request
	 *
	 * The value is a context attribute with the same key
	 *
	 * @param paramKey the key of the parameter
	 */
	def queryParam(paramKey: String): AbstractHttpRequestBuilder[_] with HttpRequestBuilderQueryParam = queryParam(paramKey, EL_START + paramKey + EL_END)
}