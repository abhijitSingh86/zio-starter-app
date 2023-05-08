package util
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.LongAdder

import util.MetricsAccumulator.{LongAcc, LongMetric}
import org.HdrHistogram.ConcurrentHistogram

import scala.util.control.NonFatal

class MetricsAccumulator(latencyMetrics: Boolean) {

  private val _latency = if (latencyMetrics) Some(new ConcurrentHistogram(TimeUnit.MINUTES.toNanos(1), 2)) else None
  def recordLatency(latencyNano: Long): Unit = {
    try {
      _latency.foreach(_.recordValue(latencyNano))
    } catch {
      case NonFatal(_) =>
    }
  }

  val numMessages       = new LongAcc("numMessages")
  val numFailedMessages = new LongAcc("numFailedMessages")

  def currentQueryMetrics: QueryMetrics = {
    val percentiles = _latency.map { latency =>
      Percentiles(
        latency.getMinValue,
        latency.getValueAtPercentile(50),
        latency.getValueAtPercentile(90),
        latency.getValueAtPercentile(99),
        latency.getValueAtPercentile(99.9),
        latency.getValueAtPercentile(99.99),
        latency.getMaxValue
      )
    }
    QueryMetrics(numMessages.toMetric, numFailedMessages.toMetric, percentiles)
  }
}

case class QueryMetrics(numMessages: LongMetric, numFailedMessages: LongMetric, latency: Option[Percentiles])

case class Percentiles(min: Long, `50`: Long, `90`: Long, `99`: Long, `99.9`: Long, `99.99`: Long, max: Long)

object MetricsAccumulator {
  sealed trait Metric[T] {
    val name: String
    def value: T

  }
  sealed trait Acc[T] extends Metric[T] {
    def inc(): Unit
    def add(by: T): Unit
    def dec(): Unit
    def valueThenReset(): T
    def reset(): Unit

    override def toString: String = s"$name: $value"
  }

  class LongAcc(val name: String) extends Acc[Long] {
    private val accumulator             = new LongAdder
    override def inc(): Unit            = accumulator.increment()
    override def add(by: Long): Unit    = accumulator.add(by)
    override def dec(): Unit            = accumulator.decrement()
    override def valueThenReset(): Long = accumulator.sumThenReset()
    override def value: Long            = accumulator.sum()
    override def reset(): Unit          = accumulator.reset()

    def toMetric: LongMetric = LongMetric(name, value)
  }

  case class LongMetric(name: String, value: Long) extends Metric[Long] {
    def -(other: Metric[Long]): LongMetric = LongMetric(name, value - other.value)
    def /(other: Metric[Long]): LongMetric = LongMetric(name, value / other.value)
  }
  object LongMetric {
    def from(other: Metric[Long]): LongMetric = LongMetric(other.name, other.value)
  }

}
