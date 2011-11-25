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
	def queryParam(paramKey: String, paramValue: String): AbstractHttpRequestBuilder[_] with HttpRequestBuilderHeaders = queryParam(interpolate(paramKey), interpolate(paramValue))

	/**
	 * Adds a query parameter to the request
	 *
	 * The value is a context attribute with the same key
	 *
	 * @param paramKey the key of the parameter
	 */
	def queryParam(paramKey: String): AbstractHttpRequestBuilder[_] with HttpRequestBuilderHeaders = queryParam(paramKey, EL_START + paramKey + EL_END)
}