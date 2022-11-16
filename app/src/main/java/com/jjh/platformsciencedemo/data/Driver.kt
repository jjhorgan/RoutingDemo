package com.jjh.platformsciencedemo.data

import com.jjh.platformsciencedemo.utils.listOfLengthFactors

data class Driver(
    val name : String,
    val factors : List<Int> = name.listOfLengthFactors()
)
