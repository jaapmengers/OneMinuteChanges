package oneminutechanges

import japgolly.scalajs.react.{React, ReactComponentB, BackendScope}
import monifu.reactive.channels.PublishChannel
import monifu.reactive._
import monifu.concurrent.Implicits.globalScheduler
import org.scalajs.dom
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.collection.mutable
import scala.scalajs.js
import scala.concurrent.duration._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object Main extends JSApp{

  case class State(strumCount: Long, timeleft: Long)
  class Backend($: BackendScope[_, State], peaks: Observable[Long], time: Observable[Long]){
    peaks.foreach { p =>
      $.modState(s => State(p , s.timeleft))
    }

    time.foreach { t =>
      $.modState(s => State(s.strumCount, t))
    }
  }


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

    val buffered  = channel
      .buffer(10)
      .map(x => x.sum / x.length)
      .combineLatest(averages)
      .map { x =>
        val curValue = x._1
        val avg = x._2._2

        if((curValue / avg) * 100 > 130)
          Peak
        else
          Baseline
      }: Observable[VolumeLevel]

    val peaks = buffered.distinctUntilChanged.filter {
      case Peak => true
      case Baseline => false
    }

    // Count only works after Observable is complete. We want a running count
    val peaksCount = peaks.scan(0L) { (acc, cur) =>
      acc + 1
    }

    val time = Observable.interval(1 second).takeWhile(x => x <= 60)

    val TimeAndCounter = ReactComponentB[Unit]("TimeAndCounter")
      .initialState(State(0, 0))
      .backend(new Backend(_, peaksCount, time))
      .render($ => <.div($.state.strumCount + " " + $.state.timeleft))
      .buildU

    React.render(TimeAndCounter(), dom.document.body)
  }

  @JSExport
  def onVolumeSignal(volume: Double): Unit = {
    channel.pushNext(volume)
  }

}
