package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagnosticResult
import com.example.data.model.DiagnosticStatus
import com.example.ui.theme.LocalWafaColors

@Composable
fun WafaBrandBadge(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .size(36.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(
        Brush.linearGradient(
          listOf(
            LocalWafaColors.current.elevatedSurface,
            MaterialTheme.colorScheme.surface
          )
        )
      )
      .border(1.dp, LocalWafaColors.current.cardBorder, RoundedCornerShape(10.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "W",
      color = LocalWafaColors.current.accentGold,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaTopBar(
  title: String,
  subtitle: String? = null,
  onSettingsClick: (() -> Unit)? = null,
  onRefreshClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier.testTag("wafa_top_bar"),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onBackground
    ),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        WafaBrandBadge()
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.labelSmall,
              color = LocalWafaColors.current.accentGold
            )
          }
        }
      }
    },
    actions = {
      if (onRefreshClick != null) {
        IconButton(onClick = onRefreshClick, modifier = Modifier.testTag("topbar_refresh_button")) {
          Icon(
            imageVector = WafaIcons.Refresh,
            contentDescription = "Refresh",
            tint = LocalWafaColors.current.accentBlue
          )
        }
      }
      if (onSettingsClick != null) {
        IconButton(onClick = onSettingsClick, modifier = Modifier.testTag("topbar_settings_button")) {
          Icon(
            imageVector = WafaIcons.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  )
}

@Composable
fun WafaSectionHeader(
  title: String,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = title.uppercase(),
      style = MaterialTheme.typography.labelLarge.copy(
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold
      ),
      color = LocalWafaColors.current.accentGold
    )
    if (actionText != null && onActionClick != null) {
      Text(
        text = actionText,
        style = MaterialTheme.typography.labelMedium,
        color = LocalWafaColors.current.accentBlue,
        modifier = Modifier
          .clickable { onActionClick() }
          .padding(4.dp)
      )
    }
  }
}

@Composable
fun WafaMetricCard(
  title: String,
  value: String,
  subvalue: String? = null,
  icon: ImageVector,
  accentColor: Color = LocalWafaColors.current.accentGold,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .testTag("metric_card_$title"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
      listOf(LocalWafaColors.current.cardBorder, Color.Transparent)
    ))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      if (subvalue != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subvalue,
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
          color = LocalWafaColors.current.textMuted
        )
      }
    }
  }
}

@Composable
fun WafaStatusChip(
  status: DiagnosticStatus,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (status) {
    DiagnosticStatus.PASSED, DiagnosticStatus.AVAILABLE ->
      Pair(LocalWafaColors.current.statusSuccess.copy(alpha = 0.15f), LocalWafaColors.current.statusSuccess)
    DiagnosticStatus.WARNING, DiagnosticStatus.LIMITED ->
      Pair(LocalWafaColors.current.statusWarning.copy(alpha = 0.15f), LocalWafaColors.current.statusWarning)
    DiagnosticStatus.FAILED, DiagnosticStatus.UNSUPPORTED ->
      Pair(LocalWafaColors.current.statusError.copy(alpha = 0.15f), LocalWafaColors.current.statusError)
    DiagnosticStatus.RUNNING ->
      Pair(LocalWafaColors.current.accentBlue.copy(alpha = 0.15f), LocalWafaColors.current.accentBlue)
    DiagnosticStatus.PERMISSION_REQUIRED ->
      Pair(LocalWafaColors.current.statusWarning.copy(alpha = 0.2f), LocalWafaColors.current.statusWarning)
    else ->
      Pair(LocalWafaColors.current.cardBorder.copy(alpha = 0.3f), MaterialTheme.colorScheme.onSurfaceVariant)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = bgColor
  ) {
    Text(
      text = status.label.uppercase(),
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp
      ),
      color = textColor,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun WafaDiagnosticResultCard(
  result: DiagnosticResult,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable { expanded = !expanded }
      .testTag("diag_card_${result.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
      listOf(LocalWafaColors.current.cardBorder, Color.Transparent)
    ))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = result.name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${result.category} • Source: ${result.measurementSource}",
            style = MaterialTheme.typography.labelSmall,
            color = LocalWafaColors.current.textMuted
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          WafaStatusChip(status = result.status)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = "Toggle Details",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = result.summary,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      AnimatedVisibility(visible = expanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(LocalWafaColors.current.elevatedSurface, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          result.details.forEach { (key, valStr) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = key, style = MaterialTheme.typography.labelMedium, color = LocalWafaColors.current.textMuted)
              Text(text = valStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }
          }
        }
      }
    }
  }
}

@Composable
fun WafaPrimaryButton(
  text: String,
  onClick: () -> Unit,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
      .testTag("primary_button_$text"),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = LocalWafaColors.current.accentGold,
      contentColor = Color.Black
    )
  ) {
    if (icon != null) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
  }
}

@Composable
fun WafaSecondaryButton(
  text: String,
  onClick: () -> Unit,
  icon: ImageVector? = null,
  modifier: Modifier = Modifier
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
      .testTag("secondary_button_$text"),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = LocalWafaColors.current.accentBlue
    ),
    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.verticalGradient(
      listOf(LocalWafaColors.current.accentBlue, LocalWafaColors.current.accentBlue.copy(alpha = 0.5f))
    ))
  ) {
    if (icon != null) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
  }
}
