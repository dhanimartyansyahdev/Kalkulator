package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundHelper {
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
    } catch (e: Exception) {
      Log.w("SoundHelper", "Could not init ToneGenerator", e)
    }
  }

  fun playClickSound() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
    } catch (_: Exception) {}
  }

  fun playSuccessSound() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
    } catch (_: Exception) {}
  }

  fun playBarkTone() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 60)
    } catch (_: Exception) {}
  }
}
