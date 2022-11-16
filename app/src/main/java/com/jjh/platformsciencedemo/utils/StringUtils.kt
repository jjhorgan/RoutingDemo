package com.jjh.platformsciencedemo.utils


fun String.vowelCount () : Int{
    var count = 0

    forEach{ c->
        when(c) {
            'a', 'e', 'i', 'o', 'u' -> count++
        }
    }
    return count
}


fun String.consonantCount () : Int {
    return length - vowelCount()
}

fun String.listOfLengthFactors() : List<Int> {
    val ret = mutableListOf<Int>()

    for( i in 2 .. length){

        if(length % i == 0){
            ret.add(i)
        }
    }
    return ret
}