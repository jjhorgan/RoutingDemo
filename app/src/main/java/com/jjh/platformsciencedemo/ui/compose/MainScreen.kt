package com.jjh.platformsciencedemo.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jjh.platformsciencedemo.businesslogic.RoutingResults
import com.jjh.platformsciencedemo.ui.viewmodel.MainActivityViewModel
import com.jjh.platformsciencedemo.ui.viewmodel.UIState


@Composable
fun MainScreen( viewModel : MainActivityViewModel = hiltViewModel() ){


    val uiState = viewModel.uiState.collectAsState()

    when(uiState.value){
        is UIState.Loading -> { Loading()}
        is UIState.Loaded -> {
            val state = uiState.value as UIState.Loaded
            Loaded(state.routingResults)

        }
        is UIState.ErrorState -> {
            val state = uiState.value as UIState.ErrorState
            Error(error = state.error)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Loaded(routing :RoutingResults){

    var selected by remember { mutableStateOf(-1)}

    LazyColumn(modifier = Modifier.fillMaxSize()){


        stickyHeader {
            Text(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
                text = "Tap a drivers name tp see the best fit delivery")
        }

        itemsIndexed(routing.getAssignments()){ i , assignment ->

            Card(onClick = {
                     selected = i
            },
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    )
            {

                Column() {
                    Text(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        text = assignment.first.name
                    )

                    if (selected == i) {//show assigned delivery

                        Text(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 10.dp),
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            text = assignment.second.streetAddress
                        )

                    }

                }
            }

        }
    }
}

@Composable
fun Loading(){
    //TODO: better loading placeholder.    something like https://github.com/valentinilk/compose-shimmer


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        CircularProgressIndicator()
    }
}

@Composable
fun Error(error : String){
    Text(text = error)

}