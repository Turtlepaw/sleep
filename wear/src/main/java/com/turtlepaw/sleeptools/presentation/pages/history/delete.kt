package com.turtlepaw.sleeptools.presentation.pages.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.turtlepaw.sleeptools.R
import com.turtlepaw.sleeptools.presentation.components.ItemsListWithModifier
import com.turtlepaw.sleeptools.presentation.theme.SleepTheme
import com.turtlepaw.sleeptools.utils.BedtimeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class Item {
    class LocalDateTimeItem(val value: LocalDateTime) : Item()
    class StringItem(val value: String) : Item()
}

@OptIn(ExperimentalWearFoundationApi::class, ExperimentalHorologistApi::class)
@Composable
fun WearHistoryDelete(
    bedtimeViewModel: BedtimeViewModel,
    item: Item,
    navigation: NavHostController,
    onDelete: (time: Item) -> Unit
) {
    SleepTheme {
        val focusRequester = rememberActiveFocusRequester()
        val scalingLazyListState = rememberScalingLazyListState()
        val dayFormatter = DateTimeFormatter.ofPattern("E d")
        val timeFormatter = DateTimeFormatter.ofPattern("E h:mm a")
        var history by remember { mutableStateOf<LocalDateTime?>(null) }
        var loading by remember { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = Unit) {
            when (item) {
                is Item.LocalDateTimeItem -> {
                    history = bedtimeViewModel.getItem(item.value.toString())
                }
                is Item.StringItem -> {
                    // do something with item.value as a String
                }
            }
            loading = false
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            TimeText(
                modifier = Modifier.scrollAway(scalingLazyListState)
            )
            PositionIndicator(
                scalingLazyListState = scalingLazyListState
            )
            ItemsListWithModifier(
                modifier = Modifier
                    .rotaryWithScroll(
                        reverseDirection = false,
                        focusRequester = focusRequester,
                        scrollableState = scalingLazyListState,
                    ),
                scrollableState = scalingLazyListState,
            ) {
            if(loading){
                item {
                    CircularProgressIndicator()
                }
            } else {
                    item {
                        Text(
                            text = "Delete ${when (item) {
                                is Item.LocalDateTimeItem -> {
                                    dayFormatter.format(item.value)
                                }
                                is Item.StringItem -> {
                                    "all"
                                }
                            }}?",
                            style = MaterialTheme.typography.title3
                        )
                    }
                    item {
                        Text(
                            text = "${if(item is Item.LocalDateTimeItem) timeFormatter.format(item.value) else "All of your recorded bedtimes"} will be permanently deleted",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                )
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.padding(3.dp))
                    }
                @Suppress("KotlinConstantConditions")
                if(
                    //item is Item.StringItem
                    "true" == "true"
                ){
                    item {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    onDelete(item)
                                    bedtimeViewModel.deleteAll()
                                    navigation.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error
                            )
                        ) {
                            Text(
                                text = "Delete${if(item is Item.StringItem) "All" else ""}",
                                color = Color.Black
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                navigation.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface,
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White
                            )
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = {
                                    navigation.popBackStack()
                                },
                                colors = ButtonDefaults.secondaryButtonColors(),
                                modifier = Modifier
                                    .size(ButtonDefaults.DefaultButtonSize)
                                //.wrapContentSize(align = Alignment.Center)
                            ) {
                                // Icon for history button
                                Icon(
                                    painter = painterResource(id = R.drawable.cancel),
                                    contentDescription = "Cancel",
                                    tint = Color(0xFFE4C6FF),
                                    modifier = Modifier
                                        .padding(2.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        onDelete(
                                            item
                                        )

                                        if(item is Item.LocalDateTimeItem){
                                            bedtimeViewModel.delete(item.value)
                                        } else {
                                            bedtimeViewModel.deleteAll()
                                        }

                                        navigation.popBackStack()
                                    }
                                },
                                colors = ButtonDefaults.secondaryButtonColors(),
                                modifier = Modifier
                                    .size(ButtonDefaults.DefaultButtonSize)
                                //.wrapContentSize(align = Alignment.Center)
                            ) {
                                // Icon for history button
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "Delete",
                                    tint = Color(0xFFE4C6FF),
                                    modifier = Modifier
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}