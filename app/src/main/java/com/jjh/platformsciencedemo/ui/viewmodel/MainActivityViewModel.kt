package com.jjh.platformsciencedemo.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjh.platformsciencedemo.businesslogic.RoutingResults
import com.jjh.platformsciencedemo.businesslogic.SecretAlgorithm
import com.jjh.platformsciencedemo.repository.RouteDataRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val routeRepo : RouteDataRepo,
    val algorithm: SecretAlgorithm
): ViewModel() {


    private val mutableState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState = mutableState.asStateFlow()


    init{
        viewModelScope.launch {

            try {
                routeRepo.getRputeData().collect {
                    it?.let {
                        mutableState.value = UIState.Loaded(algorithm.getRoutingResults(it))
                    } ?: run {
                        mutableState.value = UIState.ErrorState("error loading route data")
                    }
                }
            }
            catch( e: Exception){
                Log.e("routes request", "Exception getting routing data", e )
                mutableState.value = UIState.ErrorState("Exception loading route data")
            }
        }
    }
}

sealed class UIState{
    object Loading : UIState()
    data class Loaded( val routingResults: RoutingResults ) : UIState()
    data class  ErrorState(val error : String) : UIState()
}