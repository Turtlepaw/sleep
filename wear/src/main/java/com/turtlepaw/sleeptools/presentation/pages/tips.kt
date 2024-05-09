package com.turtlepaw.sleeptools.presentation.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.pager.PagerScreen
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.turtlepaw.sleeptools.presentation.components.ItemsListWithModifier
import com.turtlepaw.sleeptools.presentation.theme.SleepTheme
import com.turtlepaw.sleeptools.utils.TimeManager
import java.time.LocalTime

@OptIn(
    ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun Tips(
    sunlight: Int,
    bedtimeGoal: LocalTime?,
    timeManager: TimeManager,
    goBack: () -> Unit
) {
    SleepTheme {
        val formatter = timeManager.getTimeFormatter(false)
        val formatterWithDetails = timeManager.getTimeFormatter()
        val pagerState = rememberPagerState {
            2
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
//            PositionIndicator(
//                scalingLazyListState = scalingLazyListState
//            )
            PagerScreen(state = pagerState) {
                val focusRequester = rememberActiveFocusRequester()
                val scalingLazyListState = rememberScalingLazyListState()
                TimeText(
                    modifier = Modifier.scrollAway(scalingLazyListState)
                )

                ItemsListWithModifier(
                    modifier = Modifier
                        .rotaryWithScroll(
                            reverseDirection = false,
                            focusRequester = focusRequester,
                            scrollableState = scalingLazyListState,
                        ),
                    scrollableState = scalingLazyListState,
                    verticalAlignment = Arrangement.spacedBy(
                        0.dp,
                        Alignment.Top
                    )
                ) {
                    if (it == 0) {
                        item {
                            Text(
                                text = "Sunlight",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp),
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.title3
                            )
                        }
                        item {
                            Text(
                                text = "Aim for at least 15 minutes of sunlight every day",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.padding(10.dp))
                        }
                        item {
                            Text(
                                text = buildAnnotatedString {
                                    append("You currently have ")
                                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                                        append("$sunlight minutes")
                                    }
                                    append(" of sunlight")
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "Bedtime",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp),
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.title3
                            )
                        }
                        item {
                            Text(
                                text = if (bedtimeGoal != null) "Aim for a bedtime around ${
                                    formatterWithDetails.format(
                                        bedtimeGoal
                                    )
                                } to be consistent" else "No bedtime estimate, check tomorrow.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp),
                                //color = Color(0xFF939AA3)
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                goBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                        ) {
                            Text(text = "Go Back")
                        }
                    }
                }
                PositionIndicator(
                    scalingLazyListState = scalingLazyListState
                )
            }
//            ItemsListWithModifier(
//                modifier = Modifier
//                    .rotaryWithScroll(
//                        reverseDirection = false,
//                        focusRequester = focusRequester,
//                        scrollableState = scalingLazyListState,
//                    ),
//                scrollableState = scalingLazyListState,
//            ) {
//                item {
//                    Icon(
//                        painter = painterResource(id = R.drawable.lightbulb),
//                        tint = MaterialTheme.colors.primary,
//                        contentDescription = "lightbulb",
//                        modifier = Modifier
//                            .size(32.dp)
//                            .padding(bottom = 8.dp)
//                    )
//                }
//                item {
//                    Text(
//                        text = "Tips",
//                        modifier = Modifier.padding(bottom = 4.dp)
//                    )
//                }

//            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun TipsPreview() {
    Tips(sunlight = 0, bedtimeGoal = LocalTime.now(), timeManager = TimeManager()){

    }
}