package com.turtlepaw.sleeptools.presentation.pages.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.scrollAway
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import com.turtlepaw.sleeptools.R
import com.turtlepaw.sleeptools.presentation.Routes
import com.turtlepaw.sleeptools.presentation.components.ItemsListWithModifier
import com.turtlepaw.sleeptools.presentation.theme.SleepTheme
import com.turtlepaw.sleeptools.utils.AlarmType
import com.turtlepaw.sleeptools.utils.Settings
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalWearFoundationApi::class, ExperimentalHorologistApi::class)
@Composable
fun WearSettings(
    navigate: (route: String) -> Unit,
    openWakeTimePicker: () -> Unit,
    wakeTime: Pair<LocalTime, AlarmType>,
    userWakeTime: LocalTime,
    setAlarm: (value: Boolean) -> Unit,
    useAlarm: Boolean,
    setAlerts: (value: Boolean) -> Unit,
    alerts: Boolean,
    context: Context
){
    SleepTheme {
        val focusRequester = rememberActiveFocusRequester()
        val scalingLazyListState = rememberScalingLazyListState()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
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
                verticalAlignment = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.Top,
                )
            ) {
                item {
                    Text(text = "Settings")
                }
                item {
                    Button(
                        onClick = {
                            openWakeTimePicker()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFE4C6FF)
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sleep),
                            contentDescription = "sleep",
                            modifier = Modifier
                                .size(32.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Wake Up ${userWakeTime.format(formatter)}",
                            color = Color.Black
                        )
                    }
                }
                item {
                    Button(
                        onClick = {
                            navigate(Routes.SETTINGS_BEDTIME.getRoute())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFE4C6FF)
                        )
                    ) {
                        Text(
                            text = "Bedtime Settings",
                            color = Color.Black
                        )
                    }
                }
                item {
                    ToggleChip(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                        checked = useAlarm,
                        onCheckedChange = { isEnabled ->
                            setAlarm(isEnabled)
                        },
                        label = {
                            Text("Alarm", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        appIcon = {
                            Icon(
                                painter = painterResource(id = if (useAlarm) R.drawable.alarm_on else R.drawable.alarm_off),
                                contentDescription = "alarm",
                                modifier = Modifier
                                    .size(24.dp)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        },
                        toggleControl = {
                            Switch(
                                checked = useAlarm,
                                enabled = true,
                                modifier = Modifier.semantics {
                                    this.contentDescription =
                                        if (useAlarm) "On" else "Off"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFE4C6FF)
                                )
                            )
                        },
                        enabled = true,
                        colors = ToggleChipDefaults.toggleChipColors(
                            checkedEndBackgroundColor = Color(0x80E4C6FF)
                        )
                    )
                }
                item {
                    ToggleChip(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        checked = alerts,
                        onCheckedChange = { isEnabled ->
                            setAlerts(isEnabled)
                            if(isEnabled){
//                                val alarmIntent = Intent(
//                                    context,
//                                    TimeoutReceiver::class.java
//                                )
//                                val pendingIntent = PendingIntent.getBroadcast(
//                                    context,
//                                    0,
//                                    alarmIntent,
//                                    PendingIntent.FLAG_UPDATE_CURRENT
//                                )
//                                // get alarm manager and set dynamic alarm
//                                // (dynamic means it will calculate the avg
//                                //  bedtime and then schedule the alarm)
//                                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                                val currentTime = System.currentTimeMillis()
//                                val calendar = Calendar.getInstance().apply {
//                                    timeInMillis = currentTime
//                                    set(Calendar.HOUR_OF_DAY, 20) // 8:00 PM
//                                    set(Calendar.MINUTE, 0)
//                                    set(Calendar.SECOND, 0)
//                                    if (timeInMillis <= currentTime) {
//                                        add(Calendar.DAY_OF_MONTH, 1) // Move to the next day if the current time has already passed 8:00 PM
//                                    }
//                                }
//                                val triggerAtMillis = calendar.timeInMillis
//                                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()){
//                                    alarmManager.setExact(
//                                        AlarmManager.RTC_WAKEUP,
//                                        triggerAtMillis,
//                                        pendingIntent
//                                    )
//                                } else {
//                                    alarmManager.set(
//                                        AlarmManager.RTC_WAKEUP,
//                                        triggerAtMillis,
//                                        pendingIntent
//                                    )
//                                }
                            } else {
                                // Unregister tonight's alarm and
                                // the dynamic alarm manager
                            }
                        },
                        label = {
                            Text("Alerts", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        appIcon = {
                            Icon(
                                painter = painterResource(id = if (alerts) R.drawable.alerts_on else R.drawable.alerts_off),
                                contentDescription = "alert",
                                modifier = Modifier
                                    .size(24.dp)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        },
                        toggleControl = {
                            Switch(
                                checked = alerts,
                                enabled = true,
                                modifier = Modifier.semantics {
                                    this.contentDescription =
                                        if (alerts) "On" else "Off"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFE4C6FF)
                                )
                            )
                        },
                        enabled = false,
                        colors = ToggleChipDefaults.toggleChipColors(
                            checkedEndBackgroundColor = Color(0x80E4C6FF)
                        )
                    )
                }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SettingsPreview() {
    WearSettings(
        navigate = {},
        openWakeTimePicker = {},
        wakeTime = Pair(
            Settings.WAKE_TIME.getDefaultAsLocalTime(),
            AlarmType.SYSTEM_ALARM
        ),
        userWakeTime = Settings.WAKE_TIME.getDefaultAsLocalTime(),
        setAlarm = {},
        useAlarm = Settings.ALARM.getDefaultAsBoolean(),
        setAlerts = {},
        alerts = Settings.ALERTS.getDefaultAsBoolean(),
        context = LocalContext.current
    )
}