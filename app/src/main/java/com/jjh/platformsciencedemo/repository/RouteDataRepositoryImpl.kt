package com.jjh.platformsciencedemo.repository

import android.content.Context
import com.jjh.platformsciencedemo.data.RoutesData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


internal class  RouteDataRepositoryImpl constructor(
    val context : Context
) : RouteDataRepo {
    override suspend fun getRputeData(): Flow<RoutesData?> {

        val routesJson = context.assets.open("routes_data.json").bufferedReader().use{
            it.readText()
        }
        return flowOf(routesAdapter.fromJson(routesJson))
    }

    companion object{
        private val routesAdapter  : JsonAdapter<RoutesData> by lazy{
            Moshi.Builder().build().adapter(RoutesData::class.java)
        }
    }
}