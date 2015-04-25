package oneminutechanges

import monifu.reactive.channels.PublishChannel
import monifu.reactive._
import monifu.concurrent.Implicits.globalScheduler

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends JSApp{

  trait VolumeLevel
  case object Peak extends VolumeLevel
  case object Baseline extends VolumeLevel

  val channel = PublishChannel[Double]()

  @JSExport
  def main(): Unit = {

    val averages = channel.scan((Nil, 0.0): Tuple2[List[Double], Double] ) { (acc, cur) =>
      val newList = acc._1 :+ cur
      (newList, newList.sum / newList.length)
    }

    var peakCounter = 0
    var isPeak = false

    val buffered  = channel
      .buffer(10)
      .map(x => x.sum / x.length)
      .combineLatest(averages)
      .map { x =>
        val curValue = x._1
        val avg = x._2._2

        if((curValue / avg) * 100 > 150)
          Peak
        else
          Baseline
      }: Observable[VolumeLevel]

    val peaks = buffered.distinctUntilChanged.filter(x => x match {
      case Peak => true
      case Baseline => false
    })

    peaks.foreach(println)
  }

  @JSExport
  def onVolumeSignal(volume: Double): Unit = {
    channel.pushNext(volume)
  }

}
