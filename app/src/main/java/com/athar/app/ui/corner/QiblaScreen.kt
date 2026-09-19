package com.athar.app.ui.corner

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.LocationHelper
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.QiblaCalculator
import com.athar.app.data.formatDigits
import com.athar.app.data.rememberLocationEnabler
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimaryMuted
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Qibla compass matching the reference: big bearing from north,
 * circular dial with degree ring + needle, signal + help texts.
 */
@Composable
fun QiblaScreen(onBack: () -> Unit, onOpenSettings: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }
    val lat by prefs.latitude.collectAsState(initial = null)
    val lng by prefs.longitude.collectAsState(initial = null)
    val city by prefs.cityLabel.collectAsState(initial = null)
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)

    var liveLoc by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var providersOn by remember { mutableStateOf(LocationHelper.isProvidersOn(context)) }
    var locating by remember { mutableStateOf(false) }
    var prompted by remember { mutableStateOf(false) }

    fun refresh() {
        providersOn = LocationHelper.isProvidersOn(context)
        if (!LocationHelper.hasPermission(context)) return
        locating = true
        scope.launch {
            liveLoc = LocationHelper.freshFix(context.applicationContext)
            providersOn = LocationHelper.isProvidersOn(context)
            locating = false
        }
    }
    val requestEnableLocation = rememberLocationEnabler(onEnabled = { refresh() })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) refresh()
    }

    LaunchedEffect(Unit) { refresh() }
    // One automatic popup asking to turn location on.
    LaunchedEffect(providersOn) {
        if (!prompted && LocationHelper.hasPermission(context) && !providersOn) {
            prompted = true
            requestEnableLocation()
        }
    }

    // Live fix wins; otherwise the last saved location.
    val effective: Pair<Double, Double>? = liveLoc
        ?: if (lat != null && lng != null) lat!! to lng!! else null

    val qiblaBearing = remember(effective) {
        effective?.let { QiblaCalculator.bearing(it.first, it.second) }
    }
    val distanceKm = remember(effective) {
        effective?.let { QiblaCalculator.distanceKm(it.first, it.second) }
    }

    var azimuth by remember { mutableFloatStateOf(0f) }
    var accuracy by remember { mutableIntStateOf(3) }

    DisposableEffect(context) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        var gravity: FloatArray? = null
        var geomag: FloatArray? = null
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> gravity = event.values.clone()
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomag = event.values.clone()
                        accuracy = event.accuracy
                    }
                }
                val g = gravity ?: return
                val m = geomag ?: return
                val r = FloatArray(9)
                val i = FloatArray(9)
                if (SensorManager.getRotationMatrix(r, i, g, m)) {
                    val orient = FloatArray(3)
                    SensorManager.getOrientation(r, orient)
                    var deg = Math.toDegrees(orient[0].toDouble()).toFloat()
                    deg = (deg + 360) % 360
                    azimuth = deg
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, acc: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) accuracy = acc
            }
        }
        if (accel != null) manager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
        if (magnet != null) manager.registerListener(listener, magnet, SensorManager.SENSOR_DELAY_UI)
        onDispose { manager.unregisterListener(listener) }
    }

    // Smooth the needle.
    val smoothAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 120f),
        label = "azimuth"
    )

    val signalPct = when (accuracy) {
        3 -> 94
        2 -> 72
        1 -> 45
        else -> 30
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = AtharTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(R.string.qibla_title),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(44.dp))
        }

        if (qiblaBearing == null) {
            if (locating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = AtharPrimaryLight,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.quran_loading),
                            fontFamily = ThmanyahSans,
                            fontSize = 13.sp,
                            color = AtharTextSecondary
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(18.dp))
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(R.string.qibla_no_location),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AtharTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(14.dp))
                        if (!LocationHelper.hasPermission(context)) {
                            QiblaActionButton(
                                label = stringResource(R.string.setup_use_gps),
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )
                        } else {
                            QiblaActionButton(
                                label = stringResource(R.string.quran_retry),
                                onClick = { refresh() }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        QiblaGhostButton(
                            label = stringResource(R.string.settings_title),
                            onClick = onOpenSettings
                        )
                    }
                }
            }
            return@Column
        }

        // Red warning when providers are off: bearing comes from the last saved fix.
        if (!providersOn) {
            StaleBanner(onTurnOn = { requestEnableLocation() })
            Spacer(Modifier.height(6.dp))
        }

        val bearing = qiblaBearing
        val bearingInt = bearing.toInt()

        Text(
            "${formatDigits(bearingInt.toString(), numberStyle)}°",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 64.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            stringResource(R.string.qibla_from_north),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Angular difference to detect when phone faces the Qibla (within +-4 degrees)
        val diff = kotlin.math.abs((bearing.toFloat() - smoothAzimuth + 540f) % 360f - 180f)
        val isAligned = diff <= 4f

        Spacer(Modifier.height(14.dp))

        // Compass dial with top-centered alignment target icon outside the dial
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kaaba alignment target badge (outside dial at 12 o'clock position)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isAligned) AtharPrimary.copy(alpha = 0.28f) else AtharCardSurface)
                    .border(
                        width = if (isAligned) 2.dp else 1.dp,
                        color = if (isAligned) AtharPrimaryLight else AtharCardBorder,
                        shape = CircleShape
                    )
                    .shadow(
                        elevation = if (isAligned) 14.dp else 0.dp,
                        shape = CircleShape,
                        spotColor = AtharPrimaryLight,
                        ambientColor = AtharPrimaryLight.copy(alpha = 0.6f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_kaaba),
                    contentDescription = "Qibla Target",
                    tint = if (isAligned) Color.Unspecified else AtharTextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Subtle vertical alignment pointer guide downward toward compass ring
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(10.dp)
                    .background(if (isAligned) AtharPrimaryLight else AtharCardBorder)
            )

            // Compass Dial (needle points toward 12 o'clock when aligned)
            QiblaDial(
                azimuth = smoothAzimuth,
                qiblaBearing = bearing.toFloat(),
                isAligned = isAligned,
                modifier = Modifier.size(300.dp)
            )

            // Locked-on feedback banner
            AnimatedVisibility(
                visible = isAligned,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AtharPrimary.copy(alpha = 0.22f))
                        .border(1.dp, AtharPrimaryLight, RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.qibla_facing_target),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AtharPrimaryLight,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.qibla_signal) + " ",
                fontFamily = ThmanyahSans,
                fontSize = 14.sp,
                color = AtharTextSecondary
            )
            Text(
                "${formatDigits(signalPct.toString(), numberStyle)}%",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.size(6.dp))
            Icon(
                Icons.Rounded.Info,
                contentDescription = null,
                tint = AtharTextSecondary,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.qibla_crosscheck),
                fontFamily = ThmanyahSans,
                fontSize = 13.5.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                Icons.Rounded.Info,
                contentDescription = null,
                tint = AtharTextSecondary,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            stringResource(R.string.qibla_help),
            fontFamily = ThmanyahSans,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

        if (distanceKm != null) {
            Spacer(Modifier.height(10.dp))
            val formattedDistance = formatDigits("%,.0f".format(distanceKm), numberStyle)
            Text(
                stringResource(R.string.qibla_distance, formattedDistance),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AtharPrimaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (city != null) {
            Text(
                city!!,
                fontFamily = ThmanyahSans,
                fontSize = 12.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Small red warning: location is off, bearing uses the last saved fix. */
@Composable
private fun StaleBanner(onTurnOn: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2A1210))
            .border(1.dp, Color(0xFF7A2E28), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.qibla_stale),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = Color(0xFFE08D84),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF7A2E28))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTurnOn
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.qibla_turn_on),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QiblaActionButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(AtharPrimary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 26.dp, vertical = 11.dp)
    ) {
        Text(
            text = label,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp,
            color = AtharTextOnPrimary
        )
    }
}

@Composable
private fun QiblaGhostButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = AtharTextSecondary
        )
    }
}

@Composable
private fun QiblaDial(
    azimuth: Float,
    qiblaBearing: Float,
    isAligned: Boolean = false,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 8.dp.toPx()

        // Outer progress-style ring (highlights with emerald gold when aligned)
        drawArc(
            color = if (isAligned) AtharPrimaryLight else AtharPrimaryLight.copy(alpha = 0.85f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(c.x - radius, c.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = if (isAligned) 6.dp.toPx() else 4.5.dp.toPx())
        )
        // Faint track underneath for depth.
        drawArc(
            color = AtharPrimaryMuted.copy(alpha = 0.25f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(c.x - radius, c.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Rotating rose: counter-rotate by azimuth so N points to true north.
        rotate(degrees = -azimuth, pivot = c) {
            // Degree ticks + labels every 30°.
            for (deg in 0 until 360 step 5) {
                val major = deg % 30 == 0
                val a = Math.toRadians(deg.toDouble() - 90)
                val outer = radius - 14.dp.toPx()
                val inner = outer - (if (major) 14.dp.toPx() else 7.dp.toPx())
                drawLine(
                    AtharTextSecondary.copy(alpha = if (major) 0.9f else 0.4f),
                    Offset(c.x + (inner * cos(a)).toFloat(), c.y + (inner * sin(a)).toFloat()),
                    Offset(c.x + (outer * cos(a)).toFloat(), c.y + (outer * sin(a)).toFloat()),
                    strokeWidth = if (major) 2.5f else 1.5f
                )
                if (major && deg % 30 == 0 && deg != 0) {
                    val labelR = radius - 52.dp.toPx()
                    val pos = Offset(
                        c.x + (labelR * cos(a)).toFloat(),
                        c.y + (labelR * sin(a)).toFloat()
                    )
                    val label = when (deg) {
                        90 -> "E"; 180 -> "S"; 270 -> "W"
                        else -> "$deg"
                    }
                    val layout = measurer.measure(
                        label,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (label.length == 1) AtharTextSecondary else AtharTextSecondary.copy(alpha = 0.7f)
                        )
                    )
                    drawText(
                        layout,
                        topLeft = Offset(pos.x - layout.size.width / 2f, pos.y - layout.size.height / 2f)
                    )
                }
            }
            // Cardinal N label.
            val nPos = Offset(c.x, c.y - (radius - 52.dp.toPx()))
            val nLayout = measurer.measure(
                "N",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = AtharTextPrimary
                )
            )
            drawText(
                nLayout,
                topLeft = Offset(nPos.x - nLayout.size.width / 2f, nPos.y - nLayout.size.height / 2f)
            )

            // Qibla marker triangle at the bearing (rotates with the rose).
            val qa = Math.toRadians(qiblaBearing.toDouble() - 90)
            val tipR = radius - 6.dp.toPx()
            val tip = Offset(c.x + (tipR * cos(qa)).toFloat(), c.y + (tipR * sin(qa)).toFloat())
            val baseR = tipR - 16.dp.toPx()
            val perp = qa + Math.PI / 2
            val half = 9.dp.toPx()
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(
                    (c.x + baseR * cos(qa) + half * cos(perp)).toFloat(),
                    (c.y + baseR * sin(qa) + half * sin(perp)).toFloat()
                )
                lineTo(
                    (c.x + baseR * cos(qa) - half * cos(perp)).toFloat(),
                    (c.y + baseR * sin(qa) - half * sin(perp)).toFloat()
                )
                close()
            }
            drawPath(path, AtharPrimaryLight)
        }

        // Needle: points toward the Qibla relative to phone heading.
        val relative = qiblaBearing - azimuth
        val needleColor = if (isAligned) AtharPrimaryLight else AtharPrimaryLight.copy(alpha = 0.95f)
        rotate(degrees = relative, pivot = c) {
            // Needle shaft upward toward the 12 o'clock target
            drawLine(
                needleColor,
                Offset(c.x, c.y + 26.dp.toPx()),
                Offset(c.x, c.y - (radius - 78.dp.toPx())),
                strokeWidth = if (isAligned) 5.dp.toPx() else 4.dp.toPx()
            )
            // Kaaba-end cap (rounded weight at the bottom).
            drawCircle(
                needleColor,
                radius = 9.dp.toPx(),
                center = Offset(c.x, c.y + 34.dp.toPx())
            )
            drawCircle(
                AtharBackground,
                radius = 5.dp.toPx(),
                center = Offset(c.x, c.y + 34.dp.toPx())
            )
            // Center pivot.
            drawCircle(AtharBackground, radius = 8.dp.toPx(), center = c)
            drawCircle(needleColor, radius = 8.dp.toPx(), center = c, style = Stroke(if (isAligned) 3.5.dp.toPx() else 3.dp.toPx()))
            drawCircle(needleColor, radius = 2.5.dp.toPx(), center = c)
        }
    }
}
