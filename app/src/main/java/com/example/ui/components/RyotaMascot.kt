package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.OrangeSolidButton
import com.example.ui.theme.ShibaBlush
import com.example.ui.theme.ShibaFurTan
import com.example.ui.theme.ShibaFurTanShadow
import com.example.ui.theme.ShibaInnerEar
import com.example.ui.theme.ShibaNose
import com.example.ui.theme.ShibaTongue
import com.example.ui.theme.ShibaWhiteMuzzle
import com.example.viewmodel.RyotaMood

@Composable
fun RyotaMascot(
  mood: RyotaMood,
  speechBubbleText: String?,
  onTap: () -> Unit,
  modifier: Modifier = Modifier,
) {
  // Idle subtle breathing animation
  val infiniteTransition = rememberInfiniteTransition(label = "idle_bounce")
  val idleBob by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -3f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "idle_bob",
  )

  // Mood animations
  val celebrationBounce by animateFloatAsState(
    targetValue = if (mood == RyotaMood.CELEBRATING) -10f else 0f,
    animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
    label = "celebrate_bounce",
  )

  val headTilt by animateFloatAsState(
    targetValue = when (mood) {
      RyotaMood.CONFUSED -> -8f
      RyotaMood.WINKING -> 5f
      RyotaMood.BARKING -> 3f
      else -> 0f
    },
    animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
    label = "head_tilt",
  )

  val earWiggle by animateFloatAsState(
    targetValue = if (mood == RyotaMood.CELEBRATING || mood == RyotaMood.BARKING) 6f else 0f,
    animationSpec = spring(dampingRatio = 0.35f, stiffness = 500f),
    label = "ear_wiggle",
  )

  val mascotScale by animateFloatAsState(
    targetValue = if (mood == RyotaMood.CELEBRATING) 1.06f else 1.0f,
    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
    label = "scale",
  )

  Box(
    modifier = modifier
      .wrapContentSize()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onTap,
      ),
    contentAlignment = Alignment.BottomCenter,
  ) {
    // Speech bubble if present
    AnimatedVisibility(
      visible = speechBubbleText != null,
      enter = fadeIn() + scaleIn(spring(dampingRatio = 0.6f)),
      exit = fadeOut() + scaleOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-36).dp),
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
          modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
          Text(
            text = speechBubbleText.orEmpty(),
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = OrangeSolidButton,
          )
        }
        // Little triangle tail
        Canvas(modifier = Modifier.size(8.dp, 6.dp)) {
          val tailPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width / 2f, size.height)
            close()
          }
          drawPath(tailPath, Color.White)
        }
      }
    }

    // The Ryota Dog Vector Canvas
    Canvas(
      modifier = Modifier
        .size(width = 130.dp, height = 115.dp)
        .offset(y = (idleBob + celebrationBounce).dp)
        .rotate(headTilt)
        .scale(mascotScale),
    ) {
      val w = size.width
      val h = size.height

      // --- Left Ear ---
      val leftEar = Path().apply {
        moveTo(w * 0.23f, h * 0.44f)
        // Outer ear curve up to point
        cubicTo(w * 0.16f, h * 0.28f, w * 0.14f, h * 0.08f, w * 0.26f, h * 0.06f)
        // Inner ear curve down to head
        cubicTo(w * 0.32f, h * 0.08f, w * 0.40f, h * 0.22f, w * 0.42f, h * 0.38f)
        close()
      }
      drawPath(leftEar, color = ShibaFurTan)

      // Left Inner Ear Fluff
      val leftInnerEar = Path().apply {
        moveTo(w * 0.25f, h * 0.40f)
        cubicTo(w * 0.20f, h * 0.26f, w * 0.21f, h * 0.14f, w * 0.27f, h * 0.13f)
        cubicTo(w * 0.30f, h * 0.15f, w * 0.35f, h * 0.25f, w * 0.37f, h * 0.37f)
        close()
      }
      drawPath(leftInnerEar, color = ShibaInnerEar)

      // --- Right Ear ---
      val rightEar = Path().apply {
        moveTo(w * 0.77f, h * 0.44f)
        cubicTo(w * 0.84f, h * 0.28f, w * 0.86f, h * 0.08f, w * 0.74f, h * 0.06f)
        cubicTo(w * 0.68f, h * 0.08f, w * 0.60f, h * 0.22f, w * 0.58f, h * 0.38f)
        close()
      }
      drawPath(rightEar, color = ShibaFurTan)

      // Right Inner Ear Fluff
      val rightInnerEar = Path().apply {
        moveTo(w * 0.75f, h * 0.40f)
        cubicTo(w * 0.80f, h * 0.26f, w * 0.79f, h * 0.14f, w * 0.73f, h * 0.13f)
        cubicTo(w * 0.70f, h * 0.15f, w * 0.65f, h * 0.25f, w * 0.63f, h * 0.37f)
        close()
      }
      drawPath(rightInnerEar, color = ShibaInnerEar)

      // --- Head Body Base (Rounded Shiba Shape) ---
      val headPath = Path().apply {
        moveTo(w * 0.16f, h * 0.75f)
        // Left cheek bulging out cute
        cubicTo(w * 0.10f, h * 0.55f, w * 0.18f, h * 0.36f, w * 0.35f, h * 0.32f)
        // Top of head between ears
        cubicTo(w * 0.44f, h * 0.30f, w * 0.56f, h * 0.30f, w * 0.65f, h * 0.32f)
        // Right cheek
        cubicTo(w * 0.82f, h * 0.36f, w * 0.90f, h * 0.55f, w * 0.84f, h * 0.75f)
        // Bottom curve (will meet edge)
        cubicTo(w * 0.75f, h * 0.95f, w * 0.25f, h * 0.95f, w * 0.16f, h * 0.75f)
        close()
      }
      drawPath(headPath, color = ShibaFurTan)

      // Head bottom shadow/depth
      drawPath(
        headPath,
        color = ShibaFurTanShadow,
        style = Stroke(width = 3.dp.toPx(), join = StrokeJoin.Round),
      )

      // --- White Muzzle & Cheeks (Spread like cute wings) ---
      val muzzlePath = Path().apply {
        moveTo(w * 0.15f, h * 0.74f)
        // Left cheek white fur flare
        cubicTo(w * 0.18f, h * 0.56f, w * 0.32f, h * 0.54f, w * 0.42f, h * 0.63f)
        // Bridge between eyes
        cubicTo(w * 0.47f, h * 0.60f, w * 0.53f, h * 0.60f, w * 0.58f, h * 0.63f)
        // Right cheek white fur flare
        cubicTo(w * 0.68f, h * 0.54f, w * 0.82f, h * 0.56f, w * 0.85f, h * 0.74f)
        // Bottom chin
        cubicTo(w * 0.78f, h * 0.94f, w * 0.22f, h * 0.94f, w * 0.15f, h * 0.74f)
        close()
      }
      drawPath(muzzlePath, color = ShibaWhiteMuzzle)

      // --- Cute Eyebrow Dots (white ovals) ---
      drawOval(
        color = ShibaWhiteMuzzle,
        topLeft = Offset(w * 0.35f, h * 0.40f),
        size = Size(w * 0.08f, h * 0.065f),
      )
      drawOval(
        color = ShibaWhiteMuzzle,
        topLeft = Offset(w * 0.57f, h * 0.40f),
        size = Size(w * 0.08f, h * 0.065f),
      )

      // --- Cheeks Pink Blush ---
      drawOval(
        color = ShibaBlush,
        topLeft = Offset(w * 0.19f, h * 0.66f),
        size = Size(w * 0.11f, h * 0.07f),
      )
      drawOval(
        color = ShibaBlush,
        topLeft = Offset(w * 0.70f, h * 0.66f),
        size = Size(w * 0.11f, h * 0.07f),
      )

      // --- Eyes (Dynamic Based on Mood) ---
      val eyeStroke = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
      val eyeColor = ShibaNose

      when (mood) {
        RyotaMood.BLINKING -> {
          // Horizontal blink lines
          drawLine(
            color = eyeColor,
            start = Offset(w * 0.32f, h * 0.54f),
            end = Offset(w * 0.42f, h * 0.54f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
          )
          drawLine(
            color = eyeColor,
            start = Offset(w * 0.58f, h * 0.54f),
            end = Offset(w * 0.68f, h * 0.54f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
          )
        }
        RyotaMood.WINKING -> {
          // Left eye open, right eye closed wink
          drawCircle(
            color = eyeColor,
            radius = w * 0.038f,
            center = Offset(w * 0.37f, h * 0.52f),
          )
          drawCircle(
            color = Color.White,
            radius = w * 0.012f,
            center = Offset(w * 0.36f, h * 0.50f),
          )
          // Right wink arch
          val winkPath = Path().apply {
            moveTo(w * 0.58f, h * 0.54f)
            quadraticTo(w * 0.63f, h * 0.49f, w * 0.68f, h * 0.54f)
          }
          drawPath(winkPath, color = eyeColor, style = eyeStroke)
        }
        RyotaMood.CONFUSED -> {
          // Confused spirals or dots
          drawCircle(color = eyeColor, radius = w * 0.04f, center = Offset(w * 0.36f, h * 0.52f))
          drawCircle(color = eyeColor, radius = w * 0.03f, center = Offset(w * 0.64f, h * 0.50f))
        }
        else -> {
          // Happy smiling eyes (`^ ^`) like the mockup!
          val leftEyePath = Path().apply {
            moveTo(w * 0.31f, h * 0.54f)
            quadraticTo(w * 0.37f, h * 0.46f, w * 0.43f, h * 0.54f)
          }
          drawPath(leftEyePath, color = eyeColor, style = eyeStroke)

          val rightEyePath = Path().apply {
            moveTo(w * 0.57f, h * 0.54f)
            quadraticTo(w * 0.63f, h * 0.46f, w * 0.69f, h * 0.54f)
          }
          drawPath(rightEyePath, color = eyeColor, style = eyeStroke)
        }
      }

      // --- Button Nose ---
      val nosePath = Path().apply {
        moveTo(w * 0.46f, h * 0.63f)
        lineTo(w * 0.54f, h * 0.63f)
        quadraticTo(w * 0.55f, h * 0.67f, w * 0.50f, h * 0.68f)
        quadraticTo(w * 0.45f, h * 0.67f, w * 0.46f, h * 0.63f)
        close()
      }
      drawPath(nosePath, color = ShibaNose)

      // Little nose highlight
      drawCircle(
        color = Color(0x66FFFFFF),
        radius = w * 0.012f,
        center = Offset(w * 0.485f, h * 0.64f),
      )

      // --- Smiling Mouth with Tongue ---
      val mouthStroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
      // Center seam
      drawLine(
        color = ShibaNose,
        start = Offset(w * 0.50f, h * 0.68f),
        end = Offset(w * 0.50f, h * 0.72f),
        strokeWidth = 2.2.dp.toPx(),
        cap = StrokeCap.Round,
      )

      // Cute open mouth with pink tongue!
      val tonguePath = Path().apply {
        moveTo(w * 0.45f, h * 0.72f)
        cubicTo(w * 0.45f, h * 0.82f, w * 0.55f, h * 0.82f, w * 0.55f, h * 0.72f)
        close()
      }
      // Mouth cavity background
      drawPath(tonguePath, color = Color(0xFF6B2228), style = Fill)
      // Pink Tongue
      val tonguePinkPath = Path().apply {
        moveTo(w * 0.46f, h * 0.74f)
        cubicTo(w * 0.46f, h * 0.81f, w * 0.54f, h * 0.81f, w * 0.54f, h * 0.74f)
        close()
      }
      drawPath(tonguePinkPath, color = ShibaTongue, style = Fill)

      // Cute cat/shiba mouth smile arcs (`ω`)
      val leftSmile = Path().apply {
        moveTo(w * 0.42f, h * 0.71f)
        quadraticTo(w * 0.46f, h * 0.75f, w * 0.50f, h * 0.72f)
      }
      val rightSmile = Path().apply {
        moveTo(w * 0.50f, h * 0.72f)
        quadraticTo(w * 0.54f, h * 0.75f, w * 0.58f, h * 0.71f)
      }
      drawPath(leftSmile, color = ShibaNose, style = mouthStroke)
      drawPath(rightSmile, color = ShibaNose, style = mouthStroke)

      // --- Little White Paws Resting on Edge ---
      // Left Paw
      drawRoundRect(
        color = ShibaWhiteMuzzle,
        topLeft = Offset(w * 0.16f, h * 0.82f),
        size = Size(w * 0.20f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.08f, h * 0.08f),
      )
      // Left paw claw slits
      drawLine(
        color = Color(0x33000000),
        start = Offset(w * 0.22f, h * 0.85f),
        end = Offset(w * 0.22f, h * 0.95f),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
      )
      drawLine(
        color = Color(0x33000000),
        start = Offset(w * 0.28f, h * 0.85f),
        end = Offset(w * 0.28f, h * 0.95f),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
      )

      // Right Paw
      drawRoundRect(
        color = ShibaWhiteMuzzle,
        topLeft = Offset(w * 0.64f, h * 0.82f),
        size = Size(w * 0.20f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.08f, h * 0.08f),
      )
      // Right paw claw slits
      drawLine(
        color = Color(0x33000000),
        start = Offset(w * 0.71f, h * 0.85f),
        end = Offset(w * 0.71f, h * 0.95f),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
      )
      drawLine(
        color = Color(0x33000000),
        start = Offset(w * 0.77f, h * 0.85f),
        end = Offset(w * 0.77f, h * 0.95f),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
      )
    }
  }
}
