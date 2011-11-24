package com.excilys.ebi.gatling.http.request.builder.n
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.util.StringHelper._

trait HttpRequestBuilderQueryParam { this: HttpRequestBuilder =>
	/**
	 * Adds a query parameter to the request
	 *
	 * @param paramKeyFunction a function that returns the key name
	 * @param paramValueFunction a function that returns the value
	 */
	def queryParam(paramKeyFunc: Context => String, paramValueFunc: Context => String) =
		newInstanceWithQueryParam(paramKeyFunc, paramValueFunc)

	/**
	 * Adds a query parameter to the request
	 *
	 * Its key and value are set by the user
	 *
	 * @param paramKey the key of the parameter
	 * @param paramValue the value of the parameter
	 */
	def queryParam(paramKey: String, paramValue: String): HttpRequestBuilder = queryParam(interpolate(paramKey), interpolate(paramValue))

	/**
	 * Adds a query parameter to the request
	 *
	 * The value is a context attribute with the same key
	 *
	 * @param paramKey the key of the parameter
	 */
	def queryParam(paramKey: String): HttpRequestBuilder = queryParam(paramKey, EL_START + paramKey + EL_END)
}