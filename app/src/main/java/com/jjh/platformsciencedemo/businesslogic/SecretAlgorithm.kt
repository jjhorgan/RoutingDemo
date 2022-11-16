package com.jjh.platformsciencedemo.businesslogic

import com.jjh.platformsciencedemo.data.RoutesData

interface SecretAlgorithm {

    fun getRoutingResults(data : RoutesData) : RoutingResults
}