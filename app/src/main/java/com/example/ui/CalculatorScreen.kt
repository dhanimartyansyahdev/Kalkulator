package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.RyotaMascot
import com.example.ui.theme.DisplayExpressionWhite
import com.example.ui.theme.DisplayTextWhite
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.OrangeGradientBottom
import com.example.ui.theme.OrangeGradientMid
import com.example.ui.theme.OrangeGradientTop
import com.example.util.SoundHelper
import com.example.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel = viewModel(),
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsState()
  val haptic = LocalHapticFeedback.current

  fun feedback(isSuccess: Boolean = false) {
    if (uiState.soundEnabled) {
      if (isSuccess) SoundHelper.playSuccessSound() else SoundHelper.playClickSound()
    }
    if (uiState.hapticEnabled) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White),
    contentAlignment = Alignment.TopCenter,
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 540.dp),
    ) {
      val screenHeight = maxHeight

      Column(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
      ) {
        // --- TOP SECTION (Warm Orange Gradient with Mascot & Display) ---
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.42f)
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  OrangeGradientTop,
                  OrangeGradientMid,
                  OrangeGradientBottom,
                ),
              ),
            )
            .drawBehind {
              // Draw the subtle organic curved wave at the bottom where white keypad meets orange
              val w = size.width
              val h = size.height

              val wavePath = Path().apply {
                moveTo(0f, h)
                // Left curve underneath Ryota, gently sloping into the right edge
                cubicTo(
                  w * 0.30f, h + 2.dp.toPx(),
                  w * 0.55f, h - 16.dp.toPx(),
                  w, h - 8.dp.toPx(),
                )
                lineTo(w, h + 20.dp.toPx())
                lineTo(0f, h + 20.dp.toPx())
                close()
              }
              drawPath(wavePath, color = Color.White)
            }
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        ) {
          // Top Bar: "more" and clock history icon on the right
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(
                  interactionSource = remember { MutableInteractionSource() },
                  indication = ripple(color = Color.White),
                  onClick = {
                    feedback()
                    viewModel.setHistorySheetVisible(true)
                  },
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("more_button"),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "more",
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = DisplayTextWhite,
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Calculation History",
                tint = DisplayTextWhite,
                modifier = Modifier.size(20.dp),
              )
            }
          }

          // Expression & Result Display on the right
          Column(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(top = 56.dp, end = 4.dp),
            horizontalAlignment = Alignment.End,
          ) {
            // Calculation expression (e.g. "124 * 56")
            Text(
              text = uiState.expression.ifEmpty { " " },
              fontFamily = NunitoFontFamily,
              fontWeight = FontWeight.Normal,
              fontSize = 22.sp,
              color = DisplayExpressionWhite,
              textAlign = TextAlign.End,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("expression_display"),
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main result display (e.g. "6944")
            val fontSize = when {
              uiState.displayValue.length > 10 -> 32.sp
              uiState.displayValue.length > 7 -> 38.sp
              else -> 48.sp
            }

            AnimatedContent(
              targetState = uiState.displayValue,
              transitionSpec = { fadeIn() togetherWith fadeOut() },
              label = "result_animation",
            ) { displayNum ->
              Text(
                text = displayNum,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = DisplayTextWhite,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("result_display"),
              )
            }
          }

          // Ryota Shiba Mascot peeking at bottom left of the orange section!
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .offset(x = 10.dp, y = 14.dp)
              .testTag("ryota_mascot"),
          ) {
            RyotaMascot(
              mood = uiState.ryotaMood,
              speechBubbleText = uiState.speechBubbleText,
              onTap = {
                feedback(isSuccess = true)
                viewModel.onTapRyota()
              },
            )
          }
        }

        // --- BOTTOM SECTION (Pure White Keypad) ---
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.58f)
            .background(Color.White)
            .navigationBarsPadding(),
          contentAlignment = Alignment.TopCenter,
        ) {
          CalculatorKeypad(
            onDigit = { digit ->
              feedback()
              viewModel.onDigit(digit)
            },
            onDecimal = {
              feedback()
              viewModel.onDecimal()
            },
            onOperator = { op ->
              feedback()
              viewModel.onOperator(op)
            },
            onCalculate = {
              feedback(isSuccess = true)
              viewModel.onCalculate()
            },
            onClear = {
              feedback()
              viewModel.onClear()
            },
            onDelete = {
              feedback()
              viewModel.onDelete()
            },
            onToggleSign = {
              feedback()
              viewModel.onToggleSign()
            },
            onPercent = {
              feedback()
              viewModel.onPercent()
            },
            modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }

    // History and Settings Sheet
    if (uiState.showHistorySheet) {
      HistoryBottomSheet(
        history = uiState.history,
        soundEnabled = uiState.soundEnabled,
        hapticEnabled = uiState.hapticEnabled,
        onDismiss = { viewModel.setHistorySheetVisible(false) },
        onRestore = { item ->
          feedback()
          viewModel.onRestoreHistory(item)
        },
        onClearHistory = {
          feedback()
          viewModel.onClearHistory()
        },
        onToggleSound = {
          feedback()
          viewModel.toggleSound()
        },
        onToggleHaptic = {
          feedback()
          viewModel.toggleHaptic()
        },
      )
    }
  }
}
