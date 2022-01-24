import Game.*
import scala.collection.mutable.ArrayBuffer
import java.awt.Graphics2D
class Player(val name: String, val team: Team)(using ctx: Game) {
  val hand = new Hand(Deck(team), 6*128, 0)

  def draw(g2d: Graphics2D): Unit = {
    /*var toDraw = reserve.curMan.filter{case (key, value) => value > 0}.toVector.sortBy{case (key, value) => value > value}
    for(i <- toDraw.indices){
      g2d.drawString(toDraw(i)(1).toString, reserve.x, reserve.lowestY)
      g2d.drawImage(toDraw(i)(0).icon, reserve.x, reserve.lowestY - i * reserve.iconY, reserve.iconX, reserve.iconY, null)
    }
    */
  }
}

class ManaReserve(){

}
