package com.excilys.ebi.gatling.http.request.builder.api
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.util.StringHelper._

trait HttpRequestBuilderParam { this: AbstractHttpRequestWithBodyAndParamBuilder[_] =>
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