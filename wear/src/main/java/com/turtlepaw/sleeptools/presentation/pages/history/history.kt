package com.turtlepaw.sleeptools.presentation.pages.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.turtlepaw.sleeptools.R
import com.turtlepaw.sleeptools.presentation.Routes
import com.turtlepaw.sleeptools.presentation.components.ItemsListWithModifier
import com.turtlepaw.sleeptools.presentation.theme.SleepTheme
import com.turtlepaw.sleeptools.utils.BedtimeSensor
import com.turtlepaw.sleeptools.utils.TimeManager
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import kotlin.math.abs
import kotlin.random.Random

@OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class)
@Composable
fun WearHistory(
    navigate: NavHostController,
    history: Set<Pair<LocalDateTime, BedtimeSensor>?>,
    loading: Boolean,
    goal: LocalTime?
) {
    SleepTheme {
        val focusRequester = rememberActiveFocusRequester()
        val scalingLazyListState = rememberScalingLazyListState()
        val timeManager = TimeManager()
        val dayFormatter = timeManager.getDayFormatter()
        val timeFormatter = timeManager.getTimeFormatter()

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
                }  else if(history.isEmpty()){
                    item {
                        Text(text = "No history")
                    }
                } else {
                    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                    val bottomAxisValueFormatter =
                        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> daysOfWeek[x.toInt() % daysOfWeek.size] }
                    val maxValue = 10f

                    val today = LocalDate.now()
                    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    val endOfWeek = startOfWeek.plusDays(6)

                    val thisWeekData = history.filterNotNull().filter { (date, _) ->
                        date.toLocalDate() in startOfWeek..endOfWeek
                    }

                    val rawData = List(7) { index ->
                        val currentDate = startOfWeek.plusDays(index.toLong())
                        val bedtimeData = thisWeekData.find { it.first.toLocalDate() == currentDate }

                        if (bedtimeData != null) {
                            val bedtimeDifference = Duration.between(bedtimeData.first.toLocalTime(), goal).toHours().toFloat()
                            Pair(
                                false,
                                entryOf(index.toFloat(), abs(bedtimeDifference - maxValue))
                            )
                        } else {
                            Pair(
                                true,
                                entryOf(index.toFloat(), 0f)
                            )
                        }
                    }

                    val data = rawData.map { data -> data.second }

                    val chartEntryModelProducer = ChartEntryModelProducer(
                        data
                    )

                    item {
                        Text(text = "Bedtime History")
                    }
                    item {
                        Spacer(modifier = Modifier.padding(3.dp))
                    }
                    if(thisWeekData.isEmpty()){
                        item {
                            Text(text = "No data this week", textAlign = TextAlign.Center)
                        }
                    } else {
                        item {
                            Chart(
                                chart = columnChart(
                                    spacing = 2.dp,
                                    columns = rawData.map { (_) ->
                                        LineComponent(
                                            thicknessDp = 5f,
                                            shape = Shapes.roundedCornerShape(allPercent = 40),
                                            color = MaterialTheme.colors.primary.toArgb(),
                                        )
                                    },
                                    axisValuesOverrider = AxisValuesOverrider.fixed(
                                        maxY = maxValue
                                    )
                                ),
                                chartModelProducer = chartEntryModelProducer,
                                startAxis = rememberStartAxis(
                                    label = textComponent {
                                        this.color = MaterialTheme.colors.onBackground.toArgb()
                                    },
                                    guideline = LineComponent(
                                        thicknessDp = 0.5f,
                                        color = MaterialTheme.colors.surface.toArgb(),
                                    ),
                                ),
                                bottomAxis = rememberBottomAxis(
                                    label = textComponent {
                                        this.color = MaterialTheme.colors.onBackground.toArgb()
                                    },
                                    valueFormatter = bottomAxisValueFormatter,
                                    axis = LineComponent(
                                        color = MaterialTheme.colors.surface.toArgb(),
                                        thicknessDp = 0.5f
                                    )
                                ),
                                modifier = Modifier
                                    .height(100.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.padding(3.dp))
                        }
                        item {
                            Text(text = "This chart shows how consistent you've been this week", textAlign = TextAlign.Center)
                        }
                    }
//                    item {
//                        Spacer(modifier = Modifier.padding(3.dp))
//                    }
//                    item {
//                        Text(text = "Click to delete an entry")
//                    }
                    item {
                        Spacer(modifier = Modifier.padding(3.dp))
                    }
                    items(history.filterNotNull().toList().asReversed()) { time ->
                        Chip(
                            onClick = { navigate.navigate(Routes.DELETE_HISTORY.getRoute(time.first.toString())) },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colors.secondary
                            ),
                            border = ChipDefaults.chipBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // Add this line to align text vertically
                            ) {
                                Icon(
                                    painter = painterResource(id = if(time.second == BedtimeSensor.BEDTIME) R.drawable.bedtime else R.drawable.charging),
                                    contentDescription = "History",
                                    tint = Color(0xFFE4C6FF),
                                    modifier = Modifier
                                        .padding(2.dp)
                                )

                                Spacer(modifier = Modifier.padding(6.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth() // Adjust the modifier as needed
                                ) {
                                    Text(
                                        text = dayFormatter.format(time.first),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.W500,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                    Text(
                                        fontSize = 22.sp,
                                        text = timeFormatter.format(time.first),
                                        fontWeight = FontWeight.W500,
                                        color = MaterialTheme.colors.onSecondary
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = { navigate.navigate(Routes.DELETE_HISTORY.getRoute("ALL")) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    start = 2.dp,
                                    end = 2.dp
                                ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFE4C6FF)
                            )
                        ) {
                            Text(
                                text = "Clear All",
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
@Composable
private fun rememberThresholdLine(range: ClosedFloatingPointRange<Float>, color: Color): ThresholdLine {
    val label =
        textComponent(
            color = Color.Black,
            background = shapeComponent(Shapes.rectShape),
            padding = thresholdLineLabelPadding,
            margins = thresholdLineLabelMargins,
            typeface = android.graphics.Typeface.MONOSPACE,
        )
    val line = shapeComponent(color = color)
    return remember(label, line) {
        ThresholdLine(thresholdRange = range, labelComponent = label, lineComponent = line)
    }
}

private val thresholdLineLabelHorizontalPaddingValue = 8.dp
private val thresholdLineLabelVerticalPaddingValue = 2.dp
private val thresholdLineLabelMarginValue = 4.dp
private val thresholdLineLabelPadding =
    dimensionsOf(thresholdLineLabelHorizontalPaddingValue, thresholdLineLabelVerticalPaddingValue)
private val thresholdLineLabelMargins = dimensionsOf(thresholdLineLabelMarginValue)
 */

fun getRandomTime(amount: Int): MutableSet<Pair<LocalDateTime, BedtimeSensor>> {
    val randomTimes = mutableSetOf<Pair<LocalDateTime, BedtimeSensor>>()

    repeat(amount) {
        val hour = Random.nextInt(0, 24)
        val minute = Random.nextInt(0, 60)
        val second = Random.nextInt(0, 60)

        val randomTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute, second))
        randomTimes.add(
            Pair(
                randomTime,
                BedtimeSensor.BEDTIME
            )
        )
    }

    return randomTimes
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun RandomHistoryPreview() {
    val history = getRandomTime(5)
    WearHistory(
        navigate = NavHostController(LocalContext.current),
        history,
        loading = false,
        goal = TimeManager().calculateAvgBedtime(history)
    )
}

fun getSunday(): Int {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.SUNDAY // Set Sunday as the first day of the week
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek) // Set the calendar to the first day of the week
    return calendar.get(Calendar.DAY_OF_MONTH)
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun HistoryPreview() {
    val now = LocalDate.now()
    val sunday = LocalDate.of(now.year, now.month, getSunday())
    WearHistory(
        navigate = NavHostController(LocalContext.current),
        history = listOf(
            LocalDateTime.of(
                sunday,
                LocalTime.of(0, 15)
            ),
            LocalDateTime.of(
                sunday.plusDays(1),
                LocalTime.of(0, 35)
            ),
            LocalDateTime.of(
                sunday.plusDays(2),
                LocalTime.of(3, 0)
            ),
            LocalDateTime.of(
                sunday.plusDays(3),
                LocalTime.of(0, 0)
            )
        ).map { Pair(it, BedtimeSensor.BEDTIME) }.toSet(),
        loading = false,
        goal = LocalTime.of(0, 15) // 1:15AM
    )
}