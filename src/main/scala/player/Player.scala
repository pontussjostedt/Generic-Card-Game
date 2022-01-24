import Game.*
import scala.collection.mutable.ArrayBuffer
import java.awt.Graphics2D

/**Class holding relevant information for each player
 * @param name player Name
 * @param team player team
 */
class Player(val name: String, val team: Team)(using ctx: Game) {
  val hand = new Hand(Deck(team), 6*128, 0)
  val mana = ManaReserve()

  def draw(g2d: Graphics2D): Unit = {
    /*var toDraw = reserve.curMan.filter{case (key, value) => value > 0}.toVector.sortBy{case (key, value) => value > value}
    for(i <- toDraw.indices){
      g2d.drawString(toDraw(i)(1).toString, reserve.x, reserve.lowestY)
      g2d.drawImage(toDraw(i)(0).icon, reserve.x, reserve.lowestY - i * reserve.iconY, reserve.iconX, reserve.iconY, null)
    }
    */
  }
}

class ManaReserve(val manaCap: Int = 10){
  var curMaxMana = 3
  var curMana = curMaxMana

  /**Increments currentMaxMana by 1 and sets currentMana to currentMaxMana.
   * currentMaxMana may not exceed manaCap.
  */
  def onStartOfRound(): Unit = {
    curMaxMana = math.min(manaCap, curMaxMana + 1)
    curMana = curMaxMana
  }

  /**@return if in is greater than or equal to currentMana*/
  def >= (in: Int): Boolean = {
    in >= curMana
  }

  /**Increments curMana by -in */
  def -= (in: Int): Unit = {
    curMana -= in
  }
}
