package com.jjh.platformsciencedemo.businesslogic

import android.util.Log
import com.jjh.platformsciencedemo.data.Address
import com.jjh.platformsciencedemo.data.Driver
import com.jjh.platformsciencedemo.data.RoutesData
import com.jjh.platformsciencedemo.utils.consonantCount
import com.jjh.platformsciencedemo.utils.listOfLengthFactors
import com.jjh.platformsciencedemo.utils.vowelCount

internal class SecretAlgorithmImpl (
    val omitSpacesFromCalculations : Boolean = false
        )  : SecretAlgorithm {
    override fun getRoutingResults(data: RoutesData): RoutingResults {

        val routesMap : MutableMap<Driver, Address> = mutableMapOf()

        val drivers = data.drivers.map { Driver(it) }
        val addresses = data.shipments.map { Address(it) }


        //create int array matrix to hold actual value and one to hold opportunity cost value
        val scoreMatrix : Array<DoubleArray> = Array(drivers.size) { DoubleArray(addresses.size) }
        val operationMatrix : Array<DoubleArray> = Array(drivers.size) { DoubleArray(addresses.size) }

        var highScore = 0.0

        //populate matirx with initial values
        drivers.forEachIndexed{ i, driver ->
           addresses.forEachIndexed { j, address ->
               val curScore = calculateSuitabilityScore(driver,address)
               scoreMatrix[i][j] = curScore

               if(curScore > highScore)
                   highScore = curScore
           }
        }

        //populate operation matrix with opportunity cost transform data so we can find minimum
        for (i in scoreMatrix.indices){
            for(j in scoreMatrix[0].indices){
                operationMatrix[i][j] = highScore - scoreMatrix[i][j]
            }
        }

        val solution = HungarianAlgorithm(operationMatrix).execute()

        solution.forEachIndexed { i, idx ->
            routesMap.put(drivers[i], addresses[idx])
        }

        return RoutingResults(routesMap)
    }

    private fun calculateSuitabilityScore(driver : Driver, address: Address)  :Double {

        var baseScore : Double = if(address.streetAddress.length % 2 == 1) {//odd address length
                driver.name.consonantCount().toDouble()
            }
            else{//even address length
                driver.name.vowelCount() * 1.5
            }

        if(address.factors.intersect(driver.factors).isNotEmpty()){
            baseScore *= 1.5
        }

        return baseScore

    }
}