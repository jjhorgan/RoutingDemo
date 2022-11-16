package com.jjh.platformsciencedemo.repository

import com.jjh.platformsciencedemo.data.RoutesData
import kotlinx.coroutines.flow.Flow

interface RouteDataRepo {

    suspend fun getRputeData() : Flow<RoutesData?>
}