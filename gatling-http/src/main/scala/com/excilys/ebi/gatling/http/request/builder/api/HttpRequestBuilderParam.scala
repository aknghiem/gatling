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

trait HttpRequestBuilderParam extends HttpRequestBuilderOptions { this: AbstractHttpRequestWithBodyAndParamBuilder[_] =>
	/**
	 *
	 */
	def param(paramKeyFunc: Context => String, paramValueFunc: Context => String): AbstractHttpRequestWithBodyAndParamBuilder[_] with HttpRequestBuilderOptions =
		newInstanceWithParam(paramKeyFunc, paramValueFunc).asInstanceOf[AbstractHttpRequestWithBodyAndParamBuilder[_] with HttpRequestBuilderOptions]

	/**
	 * Adds a parameter to the request
	 *
	 * @param paramKey the key of the parameter
	 * @param paramValue the value of the parameter
	 */
	def param(paramKey: String, paramValue: String): AbstractHttpRequestWithBodyAndParamBuilder[_] = param(interpolate(paramKey), interpolate(paramValue))

	def param(paramKey: String): AbstractHttpRequestWithBodyAndParamBuilder[_] = param(paramKey, EL_START + paramKey + EL_END)
}