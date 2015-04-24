package oneminutechanges

import monifu.reactive.channels.PublishChannel
import monifu.concurrent.Implicits.globalScheduler

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends JSApp{

  val channel = PublishChannel[Double]()

  @JSExport
  def main(): Unit = {

    channel.scan

  }

  @JSExport
  def onVolumeSignal(volume: Double): Unit = {
    channel.pushNext(volume)
  }

}
