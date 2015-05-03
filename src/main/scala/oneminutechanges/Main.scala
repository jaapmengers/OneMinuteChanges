package oneminutechanges

import japgolly.scalajs.react.{ReactEventI, React, ReactComponentB, BackendScope}
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
object Main extends JSApp {

  trait States

  case object EmptyState extends States
  case class StateA(val a: String, val b: String) extends States
  case class StateB(val x: Int) extends States

  val states = PublishChannel[States]()

  class Backend($: BackendScope[_, States], states: PublishChannel[States]){
    states.foreach { s =>
      $.modState(_ => s)
    }

    def handleSubmit(e: ReactEventI) = {
      e.preventDefault()
      states.pushNext(StateA("AAAAAAA", "BBBBBBBB"))
    }
  }

  @JSExport
  def main(): Unit = {
    val ButtonComponent = ReactComponentB[Backend]("ButtonComponent")
      .render { B =>
        <.button("Klik", ^.onClick ==> B.handleSubmit)
      }
      .build

    val mainComponent = ReactComponentB[Unit]("MainComponent")
      .initialState[States](EmptyState)
      .backend(new Backend(_, states))
      .render { (_, S, B) =>
        S match {
          case StateA(a: String, b: String) => <.div(s"$a $b")
          case StateB(x: Int) => <.div(x)
          case EmptyState => ButtonComponent(B)
        }
      }
    .buildU

    React.render(mainComponent(), dom.document.body)
  }
}
