package com.jjh.platformsciencedemo.businesslogic

import com.jjh.platformsciencedemo.data.Address
import com.jjh.platformsciencedemo.data.Driver

data class RoutingResults(
    private val driverRoutes : Map<Driver, Address>
){
    fun drivers() : List<Driver> = driverRoutes.keys.toList()

    fun getAssignments() : List<Pair<Driver, Address>> =  driverRoutes.map {
        Pair(it.key, it.value)
    }.sortedBy{it.first.name}



}
