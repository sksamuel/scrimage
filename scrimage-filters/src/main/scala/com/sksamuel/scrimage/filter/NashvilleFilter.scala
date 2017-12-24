package com.sksamuel.scrimage.filter

import java.awt.Color
import java.util

import com.sksamuel.scrimage.PipelineFilter

object NashvilleFilter extends PipelineFilter(util.Arrays.asList(
  BackgroundBlendFilter,
  HSBFilter(0, -0.2, 0.5),
  GammaFilter(1.2),
  ContrastFilter(1.6),
  VignetteFilter(0.9, 1, 0.6, new Color(255, 140, 0))
))

