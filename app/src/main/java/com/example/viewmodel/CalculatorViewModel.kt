package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CalculationHistoryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RyotaMood {
  HAPPY,
  BLINKING,
  CELEBRATING,
  CONFUSED,
  WINKING,
  BARKING,
}

data class CalculatorUiState(
  val expression: String = "",
  val displayValue: String = "0",
  val isEvaluated: Boolean = false,
  val history: List<CalculationHistoryItem> = emptyList(),
  val ryotaMood: RyotaMood = RyotaMood.HAPPY,
  val speechBubbleText: String? = null,
  val soundEnabled: Boolean = true,
  val hapticEnabled: Boolean = true,
  val showHistorySheet: Boolean = false,
)

class CalculatorViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(
    CalculatorUiState(
      expression = "124 * 56",
      displayValue = "6944",
      isEvaluated = true,
      history = listOf(
        CalculationHistoryItem(
          id = 1L,
          expression = "124 * 56",
          result = "6944",
          timestamp = "Just now",
        ),
        CalculationHistoryItem(
          id = 2L,
          expression = "250 + 75",
          result = "325",
          timestamp = "5m ago",
        ),
      ),
    )
  )
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  private var isInitialExample = true

  init {
    startIdleEyeBlinkRoutine()
  }

  private fun startIdleEyeBlinkRoutine() {
    viewModelScope.launch {
      while (true) {
        delay(4000)
        if (_uiState.value.ryotaMood == RyotaMood.HAPPY) {
          _uiState.update { it.copy(ryotaMood = RyotaMood.BLINKING) }
          delay(250)
          _uiState.update { it.copy(ryotaMood = RyotaMood.HAPPY) }
        }
      }
    }
  }

  fun onDigit(digit: String) {
    if (isInitialExample) {
      isInitialExample = false
      _uiState.update {
        it.copy(
          expression = "",
          displayValue = digit,
          isEvaluated = false,
        )
      }
      return
    }

    _uiState.update { state ->
      if (state.isEvaluated) {
        state.copy(
          expression = "",
          displayValue = digit,
          isEvaluated = false,
        )
      } else {
        val newDisplay = if (state.displayValue == "0" || state.displayValue.isEmpty()) {
          digit
        } else {
          state.displayValue + digit
        }
        state.copy(
          displayValue = newDisplay,
          expression = if (state.expression.isEmpty() || state.expression.endsWith(" ")) {
            state.expression + digit
          } else {
            val parts = state.expression.split(" ").toMutableList()
            if (parts.isNotEmpty()) {
              parts[parts.lastIndex] = parts.last() + digit
              parts.joinToString(" ")
            } else {
              digit
            }
          },
        )
      }
    }
  }

  fun onDecimal() {
    if (isInitialExample) {
      isInitialExample = false
      _uiState.update {
        it.copy(
          expression = "0,",
          displayValue = "0,",
          isEvaluated = false,
        )
      }
      return
    }

    _uiState.update { state ->
      val currentVal = if (state.isEvaluated) "0" else state.displayValue
      if (!currentVal.contains(",")) {
        val newDisplay = "$currentVal,"
        val newExpr = if (state.isEvaluated || state.expression.isEmpty()) {
          "0,"
        } else if (state.expression.endsWith(" ")) {
          state.expression + "0,"
        } else {
          state.expression + ","
        }
        state.copy(
          displayValue = newDisplay,
          expression = newExpr,
          isEvaluated = false,
        )
      } else {
        state
      }
    }
  }

  fun onOperator(opSymbol: String) {
    // Standardize symbol for display as in the mockup: `124 * 56`
    val opChar = when (opSymbol) {
      "×" -> "*"
      "÷" -> "/"
      else -> opSymbol
    }

    if (isInitialExample) {
      isInitialExample = false
    }

    _uiState.update { state ->
      val baseVal = if (state.isEvaluated) state.displayValue else state.displayValue
      val cleanBase = baseVal.ifEmpty { "0" }

      if (state.expression.isEmpty() || state.isEvaluated) {
        state.copy(
          expression = "$cleanBase $opChar ",
          isEvaluated = false,
        )
      } else if (state.expression.endsWith(" ")) {
        // Change existing operator
        val trimmed = state.expression.trimEnd()
        val parts = trimmed.split(" ")
        if (parts.size >= 2) {
          val prefix = parts.dropLast(1).joinToString(" ")
          state.copy(expression = "$prefix $opChar ")
        } else {
          state.copy(expression = "$trimmed $opChar ")
        }
      } else {
        // Compute running preview
        val intermediate = evaluateExpression(state.expression)
        val newDisplay = intermediate ?: state.displayValue
        state.copy(
          expression = "${state.expression} $opChar ",
          displayValue = newDisplay,
          isEvaluated = false,
        )
      }
    }
  }

  fun onCalculate() {
    val state = _uiState.value
    if (state.expression.isEmpty()) return

    val cleanExpr = state.expression.trim()
    val result = evaluateExpression(cleanExpr)

    if (result != null) {
      val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
      val historyItem = CalculationHistoryItem(
        expression = cleanExpr,
        result = result,
        timestamp = timestamp,
      )

      _uiState.update {
        it.copy(
          displayValue = result,
          isEvaluated = true,
          history = listOf(historyItem) + it.history.take(49),
          ryotaMood = RyotaMood.CELEBRATING,
          speechBubbleText = "Good job! 🐾",
        )
      }

      // Reset celebration mood after delay
      viewModelScope.launch {
        delay(2200)
        _uiState.update { it.copy(ryotaMood = RyotaMood.HAPPY, speechBubbleText = null) }
      }
    } else {
      _uiState.update {
        it.copy(
          displayValue = "Oops! 🐶",
          ryotaMood = RyotaMood.CONFUSED,
          speechBubbleText = "Woof?!",
        )
      }
      viewModelScope.launch {
        delay(2200)
        _uiState.update { it.copy(ryotaMood = RyotaMood.HAPPY, speechBubbleText = null) }
      }
    }
  }

  fun onClear() {
    isInitialExample = false
    _uiState.update {
      it.copy(
        expression = "",
        displayValue = "0",
        isEvaluated = false,
        ryotaMood = RyotaMood.WINKING,
      )
    }
    viewModelScope.launch {
      delay(800)
      _uiState.update { it.copy(ryotaMood = RyotaMood.HAPPY) }
    }
  }

  fun onDelete() {
    if (isInitialExample) {
      isInitialExample = false
      _uiState.update { it.copy(expression = "", displayValue = "0", isEvaluated = false) }
      return
    }

    _uiState.update { state ->
      if (state.isEvaluated) {
        state.copy(expression = "", displayValue = "0", isEvaluated = false)
      } else {
        val newExpr = if (state.expression.isNotEmpty()) {
          if (state.expression.endsWith(" ")) {
            state.expression.dropLast(3) // drop " + "
          } else {
            state.expression.dropLast(1)
          }
        } else ""

        val newDisplay = if (state.displayValue.length > 1) {
          state.displayValue.dropLast(1)
        } else {
          "0"
        }

        state.copy(
          expression = newExpr,
          displayValue = newDisplay,
        )
      }
    }
  }

  fun onToggleSign() {
    _uiState.update { state ->
      val current = state.displayValue
      if (current == "0" || current == "Oops! 🐶") return@update state

      val toggled = if (current.startsWith("-")) {
        current.removePrefix("-")
      } else {
        "-$current"
      }

      state.copy(
        displayValue = toggled,
        expression = if (state.isEvaluated) toggled else state.expression,
      )
    }
  }

  fun onPercent() {
    _uiState.update { state ->
      try {
        val raw = state.displayValue.replace(",", ".")
        val num = BigDecimal(raw)
        val percent = num.divide(BigDecimal("100"), MathContext.DECIMAL64)
        val formatted = formatResult(percent)
        state.copy(
          displayValue = formatted,
          expression = if (state.isEvaluated) formatted else "${state.expression}%",
        )
      } catch (_: Exception) {
        state
      }
    }
  }

  fun onTapRyota() {
    val quotes = listOf(
      "Wan! 🐶",
      "Ryota is happy! 🦴",
      "Math puppy! ✨",
      "100/10 good boy! 🐾",
      "You got this! ❤️",
    )
    val randomQuote = quotes.random()
    _uiState.update {
      it.copy(
        ryotaMood = RyotaMood.BARKING,
        speechBubbleText = randomQuote,
      )
    }
    viewModelScope.launch {
      delay(2000)
      _uiState.update { it.copy(ryotaMood = RyotaMood.HAPPY, speechBubbleText = null) }
    }
  }

  fun setHistorySheetVisible(visible: Boolean) {
    _uiState.update { it.copy(showHistorySheet = visible) }
  }

  fun onRestoreHistory(item: CalculationHistoryItem) {
    _uiState.update {
      it.copy(
        expression = item.expression,
        displayValue = item.result,
        isEvaluated = true,
        showHistorySheet = false,
      )
    }
  }

  fun onClearHistory() {
    _uiState.update { it.copy(history = emptyList()) }
  }

  fun toggleSound() {
    _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
  }

  fun toggleHaptic() {
    _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
  }

  // --- Evaluation Logic ---
  private fun evaluateExpression(expr: String): String? {
    try {
      if (expr.isBlank()) return null
      val tokens = tokenize(expr)
      if (tokens.isEmpty()) return null

      // First pass: resolve multiplication and division
      val higherOpsTokens = mutableListOf<String>()
      var i = 0
      while (i < tokens.size) {
        val token = tokens[i]
        if (token == "*" || token == "/" || token == "×" || token == "÷") {
          if (higherOpsTokens.isEmpty() || i + 1 >= tokens.size) return null
          val prevNum = BigDecimal(higherOpsTokens.removeAt(higherOpsTokens.lastIndex).replace(",", "."))
          val nextToken = tokens[i + 1].replace(",", ".")
          val nextNum = BigDecimal(nextToken)
          val res = if (token == "*" || token == "×") {
            prevNum.multiply(nextNum, MathContext.DECIMAL64)
          } else {
            if (nextNum.compareTo(BigDecimal.ZERO) == 0) return null
            prevNum.divide(nextNum, 10, RoundingMode.HALF_UP)
          }
          higherOpsTokens.add(res.stripTrailingZeros().toPlainString())
          i += 2
        } else {
          higherOpsTokens.add(token)
          i++
        }
      }

      // Second pass: resolve addition and subtraction
      var accumulator = BigDecimal(higherOpsTokens[0].replace(",", "."))
      var j = 1
      while (j < higherOpsTokens.size) {
        val op = higherOpsTokens[j]
        if (j + 1 >= higherOpsTokens.size) break
        val next = BigDecimal(higherOpsTokens[j + 1].replace(",", "."))
        accumulator = when (op) {
          "+" -> accumulator.add(next, MathContext.DECIMAL64)
          "-" -> accumulator.subtract(next, MathContext.DECIMAL64)
          else -> accumulator
        }
        j += 2
      }

      return formatResult(accumulator)
    } catch (_: Exception) {
      return null
    }
  }

  private fun tokenize(expr: String): List<String> {
    val clean = expr.trim()
    val parts = clean.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return parts
  }

  private fun formatResult(num: BigDecimal): String {
    val stripped = num.stripTrailingZeros()
    val str = stripped.toPlainString()
    return str.replace(".", ",")
  }
}
