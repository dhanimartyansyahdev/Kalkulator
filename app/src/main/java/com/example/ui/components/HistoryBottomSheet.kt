package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationHistoryItem
import com.example.ui.theme.KeypadNumberText
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.OrangeSolidButton
import com.example.ui.theme.PeachPastelButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
  history: List<CalculationHistoryItem>,
  soundEnabled: Boolean,
  hapticEnabled: Boolean,
  onDismiss: () -> Unit,
  onRestore: (CalculationHistoryItem) -> Unit,
  onClearHistory: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleHaptic: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = Modifier.testTag("history_bottom_sheet"),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(OrangeSolidButton.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Default.AccessTime,
              contentDescription = "History",
              tint = OrangeSolidButton,
              modifier = Modifier.size(20.dp),
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "History & Settings",
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = KeypadNumberText,
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = KeypadNumberText.copy(alpha = 0.6f),
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Settings toggles
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFFFF8F2))
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = null,
            tint = OrangeSolidButton,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Button Click Sound",
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = KeypadNumberText,
          )
        }
        Switch(
          checked = soundEnabled,
          onCheckedChange = { onToggleSound() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = OrangeSolidButton,
          ),
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFFFF8F2))
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Vibration,
            contentDescription = null,
            tint = OrangeSolidButton,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Haptic Vibration",
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = KeypadNumberText,
          )
        }
        Switch(
          checked = hapticEnabled,
          onCheckedChange = { onToggleHaptic() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = OrangeSolidButton,
          ),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // History Header with Clear button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Past Calculations",
          fontFamily = NunitoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = KeypadNumberText,
        )

        if (history.isNotEmpty()) {
          TextButton(
            onClick = onClearHistory,
            modifier = Modifier.testTag("clear_history_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              tint = OrangeSolidButton,
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Clear",
              fontFamily = NunitoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = OrangeSolidButton,
            )
          }
        }
      }

      HorizontalDivider(color = Color(0xFFF0EBE7), thickness = 1.dp)

      Spacer(modifier = Modifier.height(8.dp))

      if (history.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
          contentAlignment = Alignment.Center,
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "🐾",
              fontSize = 36.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No calculations yet!",
              fontFamily = NunitoFontFamily,
              fontWeight = FontWeight.SemiBold,
              fontSize = 16.sp,
              color = KeypadNumberText.copy(alpha = 0.6f),
            )
            Text(
              text = "Calculate something and Ryota will save it here.",
              fontFamily = NunitoFontFamily,
              fontSize = 13.sp,
              color = KeypadNumberText.copy(alpha = 0.4f),
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          items(history, key = { it.id }) { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFAFAFA))
                .clickable {
                  clipboardManager.setText(AnnotatedString(item.result))
                  Toast.makeText(context, "Copied result: ${item.result}", Toast.LENGTH_SHORT).show()
                  onRestore(item)
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(
                  text = item.expression,
                  fontFamily = NunitoFontFamily,
                  fontWeight = FontWeight.Normal,
                  fontSize = 15.sp,
                  color = KeypadNumberText.copy(alpha = 0.65f),
                )
                Text(
                  text = "= ${item.result}",
                  fontFamily = NunitoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  color = OrangeSolidButton,
                )
              }

              Text(
                text = item.timestamp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = KeypadNumberText.copy(alpha = 0.4f),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
