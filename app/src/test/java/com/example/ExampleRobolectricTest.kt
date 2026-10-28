package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ryota Calculator", appName)
  }

  @Test
  fun `verify calculation 124 times 56 equals 6944`() {
    val viewModel = CalculatorViewModel()
    // Initial state is 124 * 56 -> 6944
    assertEquals("6944", viewModel.uiState.value.displayValue)
    assertEquals("124 * 56", viewModel.uiState.value.expression)

    // Clear and recalculate
    viewModel.onClear()
    viewModel.onDigit("1")
    viewModel.onDigit("2")
    viewModel.onDigit("4")
    viewModel.onOperator("×")
    viewModel.onDigit("5")
    viewModel.onDigit("6")
    viewModel.onCalculate()

    assertEquals("6944", viewModel.uiState.value.displayValue)
  }
}
