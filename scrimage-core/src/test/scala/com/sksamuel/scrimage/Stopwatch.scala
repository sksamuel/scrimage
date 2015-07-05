package com.sksamuel.scrimage

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class Stopwatch {

  private val instants = new ArrayBuffer[FiniteDuration]

  def start(): Unit = {
    reset()
    lap
  }

  def stop(): FiniteDuration = lap

  private def zero: FiniteDuration = 0.nanos
  private def now: FiniteDuration = System.nanoTime.nanos

  /**
   * Adds another lap point to the stopwatch and returns the duration of the last lap.
   */
  def lap: FiniteDuration = {
    instants append now
    lastLap
  }

  /**
   * Returns the durations of each lap.
   */
  def laps: Array[FiniteDuration] = {
    var previous = instants.head
    instants.tail.map { next =>
      val y = next - previous
      previous = next
      y
    }.toArray
  }

  def firstLap: FiniteDuration = laps.headOption.getOrElse(zero)

  def lastLap: FiniteDuration = if (instants.size < 2) zero else instants(instants.length - 2) - instants.last

  def averageLap: Duration = if (laps.isEmpty) zero else elapsed / instants.length.toDouble

  /**
   * Shows how much time is currently on the stopwatch without affecting the accumulating time.
   */
  def current: FiniteDuration = now - instants.head

  /**
   * Shows how much time elapsed between starting the stopwatch and stopping it.
   */
  def elapsed: FiniteDuration = {
    require(laps.length > 1, "Cannot call .elapsed without starting and stopping the stopwatch")
    instants.last - instants.head
  }

  def reset(): Unit = {
    instants.clear()
  }
}