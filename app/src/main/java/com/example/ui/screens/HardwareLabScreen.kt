package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.engine.HardwareTestLabEngine
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSecondaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import kotlinx.coroutines.launch

enum class LabSubMode {
  OVERVIEW,
  COLOR_SCREEN,
  GRADIENT_SCREEN,
  TOUCH_GRID
}

@Composable
fun HardwareLabScreen(
  engine: HardwareTestLabEngine,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  var currentSubMode by remember { mutableStateOf(LabSubMode.OVERVIEW) }

  // Speaker state
  var isPlayingSpeakerTone by remember { mutableStateOf(false) }
  var speakerTestPassed by remember { mutableStateOf<Boolean?>(null) }

  // Mic state
  var isRecordingMic by remember { mutableStateOf(false) }
  var micLevel by remember { mutableFloatStateOf(0f) }
  var micTestPassed by remember { mutableStateOf<Boolean?>(null) }

  // Vibration state
  var vibrationTested by remember { mutableStateOf(false) }

  // Flashlight state
  var isFlashlightOn by remember { mutableStateOf(false) }

  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      scope.launch {
        isRecordingMic = true
        val ok = engine.measureMicrophoneLevel(durationMs = 2500) { level ->
          micLevel = level
        }
        isRecordingMic = false
        micTestPassed = ok
      }
    }
  }

  BackHandler(enabled = currentSubMode != LabSubMode.OVERVIEW) {
    currentSubMode = LabSubMode.OVERVIEW
  }

  when (currentSubMode) {
    LabSubMode.COLOR_SCREEN -> {
      ColorScreenTest(onExit = { currentSubMode = LabSubMode.OVERVIEW })
    }
    LabSubMode.GRADIENT_SCREEN -> {
      GradientScreenTest(onExit = { currentSubMode = LabSubMode.OVERVIEW })
    }
    LabSubMode.TOUCH_GRID -> {
      TouchGridTest(onExit = { currentSubMode = LabSubMode.OVERVIEW })
    }
    LabSubMode.OVERVIEW -> {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
          .testTag("hardware_lab_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          ) {
            Text(
              text = "WAFA HARDWARE TEST LAB",
              style = MaterialTheme.typography.titleLarge.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = colors.accentGold
            )
            Text(
              text = "Interactive verification of physical touch, display, speaker, microphone and vibration",
              style = MaterialTheme.typography.bodyMedium,
              color = colors.textMuted
            )
          }
        }

        // Section 1: Display & Touch Screen Tests
        item {
          WafaSectionHeader(title = "Display & Touch Digitizer")

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = Brush.verticalGradient(
                listOf(colors.cardBorder, Color.Transparent)
              )
            )
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Manual Visual & Digitizer Checks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Test for dead pixels, backlight bleed, color uniformity, color gradients, and digitizer coverage.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WafaSecondaryButton(
                  text = "Color Screen Test",
                  icon = WafaIcons.DisplayTouch,
                  onClick = { currentSubMode = LabSubMode.COLOR_SCREEN },
                  modifier = Modifier.weight(1f)
                )
                WafaSecondaryButton(
                  text = "Gradient Banding",
                  icon = WafaIcons.DisplayTouch,
                  onClick = { currentSubMode = LabSubMode.GRADIENT_SCREEN },
                  modifier = Modifier.weight(1f)
                )
              }

              WafaPrimaryButton(
                text = "Interactive Touch-Grid Matrix",
                icon = WafaIcons.DisplayTouch,
                onClick = { currentSubMode = LabSubMode.TOUCH_GRID }
              )
            }
          }
        }

        // Section 2: Audio Speaker Playback Test
        item {
          WafaSectionHeader(title = "Acoustic & Audio Verification")

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = Brush.verticalGradient(
                listOf(colors.cardBorder, Color.Transparent)
              )
            )
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Speaker Output Test (440 Hz)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Generates a real 16-bit PCM concert pitch sine wave using AudioTrack",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = colors.textMuted
                  )
                }
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.accentBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = WafaIcons.Audio,
                    contentDescription = null,
                    tint = colors.accentBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              WafaPrimaryButton(
                text = if (isPlayingSpeakerTone) "Playing 440 Hz Tone..." else "Play Test Tone (1.5s)",
                icon = WafaIcons.Waveform,
                enabled = !isPlayingSpeakerTone,
                onClick = {
                  scope.launch {
                    isPlayingSpeakerTone = true
                    val success = engine.playTestTone(440, 1500)
                    isPlayingSpeakerTone = false
                    speakerTestPassed = success
                  }
                }
              )

              if (speakerTestPassed != null) {
                Text(
                  text = if (speakerTestPassed == true) "✓ Tone generated successfully. Did you hear the audio?" else "⚠ AudioTrack initialization failed",
                  style = MaterialTheme.typography.labelMedium,
                  color = if (speakerTestPassed == true) colors.statusSuccess else colors.statusError
                )
              }
            }
          }
        }

        // Section 3: Microphone Real-Time VU Meter Test
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = Brush.verticalGradient(
                listOf(colors.cardBorder, Color.Transparent)
              )
            )
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Microphone Live RMS Level Meter",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Requires RECORD_AUDIO permission to capture input and calculate RMS amplitude.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = colors.textMuted
              )

              if (isRecordingMic) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = "Live Input Level:", style = MaterialTheme.typography.labelSmall, color = colors.accentBlue)
                    Text(text = "%.1f %%".format(micLevel), style = MaterialTheme.typography.labelSmall, color = colors.accentGold)
                  }
                  LinearProgressIndicator(
                    progress = { micLevel / 100f },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(10.dp)
                      .clip(RoundedCornerShape(5.dp)),
                    color = colors.accentGold,
                    trackColor = colors.elevatedSurface
                  )
                }
              }

              WafaSecondaryButton(
                text = if (isRecordingMic) "Listening & Measuring..." else "Test Microphone Recording",
                icon = WafaIcons.Waveform,
                onClick = {
                  val hasPerm = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                  ) == PackageManager.PERMISSION_GRANTED

                  if (hasPerm) {
                    scope.launch {
                      isRecordingMic = true
                      val ok = engine.measureMicrophoneLevel(durationMs = 2500) { level ->
                        micLevel = level
                      }
                      isRecordingMic = false
                      micTestPassed = ok
                    }
                  } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                  }
                }
              )

              if (micTestPassed != null) {
                Text(
                  text = if (micTestPassed == true) "✓ Microphone recorded and computed live audio RMS" else "⚠ Microphone test incomplete",
                  style = MaterialTheme.typography.labelMedium,
                  color = if (micTestPassed == true) colors.statusSuccess else colors.statusWarning
                )
              }
            }
          }
        }

        // Section 4: Vibration & Flashlight Tests
        item {
          WafaSectionHeader(title = "Physical Actuators")

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                  listOf(colors.cardBorder, Color.Transparent)
                )
              )
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "Vibration Motor",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Haptic motor pulse test",
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                  color = colors.textMuted
                )
                WafaSecondaryButton(
                  text = "Trigger Vibration",
                  icon = WafaIcons.Vibration,
                  onClick = {
                    val ok = engine.testVibration()
                    vibrationTested = ok
                  }
                )
                if (vibrationTested) {
                  Text(text = "✓ Pulse sent", style = MaterialTheme.typography.labelSmall, color = colors.statusSuccess)
                }
              }
            }

            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                  listOf(colors.cardBorder, Color.Transparent)
                )
              )
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "Camera Torch",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Optical LED flashlight test",
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                  color = colors.textMuted
                )
                WafaPrimaryButton(
                  text = if (isFlashlightOn) "Turn Off" else "Toggle Flash",
                  icon = WafaIcons.Flashlight,
                  onClick = {
                    val nextState = !isFlashlightOn
                    val ok = engine.toggleFlashlight(nextState)
                    if (ok) isFlashlightOn = nextState
                  }
                )
                if (isFlashlightOn) {
                  Text(text = "⚡ Torch is Active", style = MaterialTheme.typography.labelSmall, color = colors.accentGold)
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
fun ColorScreenTest(onExit: () -> Unit) {
  val testColors = listOf(
    Pair("Red", Color.Red),
    Pair("Green", Color.Green),
    Pair("Blue", Color.Blue),
    Pair("White", Color.White),
    Pair("Black", Color.Black),
    Pair("Yellow", Color.Yellow),
    Pair("Cyan", Color.Cyan),
    Pair("Magenta", Color.Magenta)
  )

  var colorIndex by remember { mutableIntStateOf(0) }
  val (name, color) = testColors[colorIndex]

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color)
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = {
            if (colorIndex < testColors.size - 1) {
              colorIndex++
            } else {
              onExit()
            }
          }
        )
      }
      .testTag("color_screen_test"),
    contentAlignment = Alignment.BottomCenter
  ) {
    Surface(
      modifier = Modifier.padding(24.dp),
      shape = RoundedCornerShape(12.dp),
      color = Color.Black.copy(alpha = 0.75f)
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Testing: $name (${colorIndex + 1}/${testColors.size})",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Text(
          text = "Tap anywhere to advance • Inspect for dead/stuck pixels",
          color = Color.LightGray,
          fontSize = 12.sp
        )
      }
    }
  }
}

@Composable
fun GradientScreenTest(onExit: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color.Black, Color.DarkGray, Color.Gray, Color.LightGray, Color.White)
        )
      )
      .clickable { onExit() }
      .testTag("gradient_screen_test"),
    contentAlignment = Alignment.BottomCenter
  ) {
    Surface(
      modifier = Modifier.padding(24.dp),
      shape = RoundedCornerShape(12.dp),
      color = Color.Black.copy(alpha = 0.75f)
    ) {
      Text(
        text = "Gradient Banding Test • Tap to exit",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
      )
    }
  }
}

@Composable
fun TouchGridTest(onExit: () -> Unit) {
  val totalRows = 8
  val totalCols = 5
  val totalCells = totalRows * totalCols
  val touchedIndices = remember { mutableStateListOf<Int>() }

  val completionPct = (touchedIndices.size.toFloat() / totalCells.toFloat() * 100).toInt()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0B0D10))
      .testTag("touch_grid_test")
  ) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(totalCols),
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 70.dp)
    ) {
      items(totalCells) { index ->
        val isTouched = touchedIndices.contains(index)
        Box(
          modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isTouched) Color(0xFF48C78E) else Color(0xFF1B2027))
            .border(1.dp, Color(0xFF2A3039), RoundedCornerShape(4.dp))
            .pointerInput(index) {
              detectTapGestures(
                onTap = {
                  if (!touchedIndices.contains(index)) touchedIndices.add(index)
                }
              )
            }
        )
      }
    }

    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFF14171C),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3039))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Touch Coverage: $completionPct%",
            color = Color(0xFFD6B46A),
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Tap all blocks across screen",
            color = Color.Gray,
            fontSize = 11.sp
          )
        }
        WafaPrimaryButton(
          text = "Done",
          onClick = onExit,
          modifier = Modifier.width(90.dp)
        )
      }
    }
  }
}
