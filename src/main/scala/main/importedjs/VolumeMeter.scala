package main.importedjs

import scala.scalajs.js

package object Meter extends js.GlobalScope {
  def onVolumeChanged(volume: Int): Nothing = js.native
}
