package com.amohnacs.currentrestaurants.domain.yelpQL

import com.amohnacs.currentrestaurants.R
import com.amohnacs.currentrestaurants.common.ResourceProvider
import com.apollographql.apollo.ApolloClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YelpApolloClient @Inject constructor(
    resourceProvider: ResourceProvider
) {
    private val authToken = resourceProvider.getString(R.string.yelp_api_key)

    val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(BASE_URL)
        .okHttpClient(getOkHttpClient(authToken))
        .build()

    private fun getOkHttpClient(authToken: String): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.connectTimeout(TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
        builder.readTimeout(TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
        builder.writeTimeout(TIME_OUT.toLong(), TimeUnit.MILLISECONDS)

        addLoggingInterceptor(builder)
        builder.addInterceptor(
            AuthorizationInterceptor(
                authToken
            )
        )

        return builder.build()
    }

    private fun addLoggingInterceptor(builder: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(loggingInterceptor)
    }

    /**
     * We construct "chains" that pass our our request through a chain/array of interceptors.
     * When the interceptor chain ends the result will be returned to the caller.
     * We are adding our Http requestbuilder so we can add our bearer token
     */
    private class AuthorizationInterceptor(private val localToken: String) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $localToken")
                .build()
            return chain.proceed(request)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.yelp.com/v3/graphql"
        private const val TIME_OUT = 5000
    }
}