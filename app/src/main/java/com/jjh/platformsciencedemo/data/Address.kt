package com.jjh.platformsciencedemo.data

import com.jjh.platformsciencedemo.utils.listOfLengthFactors


data class Address(
    val streetAddress : String,
    val factors : List<Int> = streetAddress.listOfLengthFactors()
)
