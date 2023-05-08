import util.MetricsAccumulator

import scala.util.Random

object App {

  def main(args: Array[String]): Unit = {
    val metrics = new MetricsAccumulator(true)
    val start = System.nanoTime()

    1 to 100 foreach (x => {
      if(x % 7 == 0)
        metrics.numFailedMessages.inc()
        else
      metrics.numMessages.inc()

      metrics.recordLatency(Random.nextInt(x))
    })

    println(metrics.currentQueryMetrics)


  }

}
