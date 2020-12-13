package com.amohnacs.currentrestaurants.domain

import com.apollographql.apollo.ApolloQueryCall
import yelpQL.BusinessDetailsQuery
import yelpQL.SearchQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YelpApolloService @Inject constructor(
    private val yelpApolloClient: YelpApolloClient
) {
    private val apolloClient = yelpApolloClient.apolloClient

    fun search(
        radius: Double,
        latitude: Double,
        longitude: Double,
        offset: Int,
        queryTerm: String
    ): ApolloQueryCall<SearchQuery.Data>? {

        val yelpSearch = SearchQuery(
            radius,
            latitude,
            longitude,
            offset,
            queryTerm
        )
        return apolloClient.query(yelpSearch)
    }

    fun businessDetails(businessId: String): ApolloQueryCall<BusinessDetailsQuery.Data>? {
        val businessQuery = BusinessDetailsQuery(businessId)
        return apolloClient.query(businessQuery)
    }
}