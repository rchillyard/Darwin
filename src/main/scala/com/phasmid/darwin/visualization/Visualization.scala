package com.phasmid.darwin.visualization

trait Visualization[O] {
  def updateAvatar(o: O): Unit

  def destroyAvatar(o: O): Unit

}
