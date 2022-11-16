package com.jjh.platformsciencedemo.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoutesData(
    @Json val drivers : List<String>,
    @Json val shipments : List<String>

)
