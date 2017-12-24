package com.sksamuel.scrimage.filter

import java.util

import com.sksamuel.scrimage.PipelineFilter

object GothamFilter extends PipelineFilter(util.Arrays.asList(
  HSBFilter(0, -0.85, 0.2),
  ColorizeFilter(34, 43, 109, 25),
  GammaFilter(0.5),
  ContrastFilter(1.4)
))
