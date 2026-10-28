package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DelButtonBackground
import com.example.ui.theme.DelButtonText
import com.example.ui.theme.KeypadNumberText
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.OrangeSolidButton
import com.example.ui.theme.PeachPastelButton

@Composable
fun CalculatorKeypad(
  onDigit: (String) -> Unit,
  onDecimal: () -> Unit,
  onOperator: (String) -> Unit,
  onCalculate: () -> Unit,
  onClear: () -> Unit,
  onDelete: () -> Unit,
  onToggleSign: () -> Unit,
  onPercent: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // Delete pill button aligned to the right (above the operator column)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 4.dp),
      horizontalArrangement = Arrangement.End,
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(DelButtonBackground)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = OrangeSolidButton),
            onClick = onDelete,
          )
          .padding(horizontal = 18.dp, vertical = 6.dp)
          .testTag("del_button"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "del",
          fontFamily = NunitoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = DelButtonText,
        )
      }
    }

    // Row 1: C, +/-, %, ÷
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      SolidOrangeButton(
        label = "C",
        onClick = onClear,
        modifier = Modifier.weight(1f),
        testTag = "btn_c",
      )
      SolidOrangeButton(
        label = "+/-",
        onClick = onToggleSign,
        modifier = Modifier.weight(1f),
        testTag = "btn_toggle_sign",
      )
      SolidOrangeButton(
        label = "%",
        onClick = onPercent,
        modifier = Modifier.weight(1f),
        testTag = "btn_percent",
      )
      PastelPeachButton(
        label = "÷",
        onClick = { onOperator("÷") },
        modifier = Modifier.weight(1f),
        testTag = "btn_divide",
      )
    }

    // Row 2: 7, 8, 9, ×
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      NumberButton(
        label = "7",
        onClick = { onDigit("7") },
        modifier = Modifier.weight(1f),
        testTag = "btn_7",
      )
      NumberButton(
        label = "8",
        onClick = { onDigit("8") },
        modifier = Modifier.weight(1f),
        testTag = "btn_8",
      )
      NumberButton(
        label = "9",
        onClick = { onDigit("9") },
        modifier = Modifier.weight(1f),
        testTag = "btn_9",
      )
      PastelPeachButton(
        label = "×",
        onClick = { onOperator("×") },
        modifier = Modifier.weight(1f),
        testTag = "btn_multiply",
      )
    }

    // Row 3: 4, 5, 6, -
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      NumberButton(
        label = "4",
        onClick = { onDigit("4") },
        modifier = Modifier.weight(1f),
        testTag = "btn_4",
      )
      NumberButton(
        label = "5",
        onClick = { onDigit("5") },
        modifier = Modifier.weight(1f),
        testTag = "btn_5",
      )
      NumberButton(
        label = "6",
        onClick = { onDigit("6") },
        modifier = Modifier.weight(1f),
        testTag = "btn_6",
      )
      PastelPeachButton(
        label = "-",
        onClick = { onOperator("-") },
        modifier = Modifier.weight(1f),
        testTag = "btn_minus",
      )
    }

    // Row 4: 1, 2, 3, +
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      NumberButton(
        label = "1",
        onClick = { onDigit("1") },
        modifier = Modifier.weight(1f),
        testTag = "btn_1",
      )
      NumberButton(
        label = "2",
        onClick = { onDigit("2") },
        modifier = Modifier.weight(1f),
        testTag = "btn_2",
      )
      NumberButton(
        label = "3",
        onClick = { onDigit("3") },
        modifier = Modifier.weight(1f),
        testTag = "btn_3",
      )
      PastelPeachButton(
        label = "+",
        onClick = { onOperator("+") },
        modifier = Modifier.weight(1f),
        testTag = "btn_plus",
      )
    }

    // Row 5: 0, ,, = (spans 2 columns)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      NumberButton(
        label = "0",
        onClick = { onDigit("0") },
        modifier = Modifier.weight(1f),
        testTag = "btn_0",
      )
      NumberButton(
        label = ",",
        onClick = onDecimal,
        modifier = Modifier.weight(1f),
        testTag = "btn_comma",
      )
      PastelPeachWideButton(
        label = "=",
        onClick = onCalculate,
        modifier = Modifier.weight(2.1f),
        testTag = "btn_equals",
      )
    }
  }
}

@Composable
private fun SolidOrangeButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String,
) {
  Box(
    modifier = modifier
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(OrangeSolidButton)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color.White),
        onClick = onClick,
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      fontFamily = NunitoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp,
      color = Color.White,
    )
  }
}

@Composable
private fun PastelPeachButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String,
) {
  Box(
    modifier = modifier
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(PeachPastelButton)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color.White),
        onClick = onClick,
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      fontFamily = NunitoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 26.sp,
      color = Color.White,
    )
  }
}

@Composable
private fun PastelPeachWideButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String,
) {
  Box(
    modifier = modifier
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(PeachPastelButton)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color.White),
        onClick = onClick,
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      fontFamily = NunitoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 26.sp,
      color = Color.White,
    )
  }
}

@Composable
private fun NumberButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String,
) {
  Box(
    modifier = modifier
      .height(56.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = PeachPastelButton.copy(alpha = 0.4f)),
        onClick = onClick,
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      fontFamily = NunitoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 28.sp,
      color = KeypadNumberText,
    )
  }
}
