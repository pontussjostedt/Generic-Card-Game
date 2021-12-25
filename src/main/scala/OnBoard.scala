import java.awt.Font
import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer
import java.awt.Color
import java.awt.Graphics2D
class Creature(path: String, infoPath: String = "placeHolderInfo.png")
    extends OnBoard(path, infoPath) {
  var maxHealth = 20
  var hp = maxHealth
  var maxArmor = 2
  var armor = maxArmor

  /** Called at the start of round */
  override def onStartOfRound(using board: Board): Unit = {
    armor = maxArmor
  }

  /** damages armor and health accordingly to damage and checks if creature is
    * dead, also updates cardImage
    * @param dmgDealt
    *   damage to card
    */
  def damage(dmgDealt: Int): Unit = {
    var dmgLeft = dmgDealt
    armor -= dmgLeft
    dmgLeft = math.max(dmgLeft - armor, 0)
    hp -= dmgLeft
    updateCardImage()
    if (hp <= 0) {
      println("I am dead")
    }
  }

  override def updateCardImage(): Unit = {
    var img = new BufferedImage(
      infoImage.getWidth,
      infoImage.getHeight,
      BufferedImage.TYPE_4BYTE_ABGR
    )
    val manaList = manaCost.cost.toVector.sortBy { case (manaType, value) =>
      value < value
    }
    var g2d = img.createGraphics()
    g2d.drawImage(infoImage, 0, 0, img.getWidth, img.getHeight, null)
    val iconWidth = 20
    val iconHeight = 20
    val xIcon = 290
    val yIcon = 4
    var counter = 0
    for (i <- manaList.indices) {
      var icon = manaList(i)(0).icon
      for (j <- 0 until manaList(i)(1)) {
        g2d.drawImage(
          icon,
          xIcon - counter * iconWidth,
          yIcon,
          iconWidth,
          iconHeight,
          null
        )
        counter += 1
      }
    }
    g2d.setFont(new Font("Courier New", 1, 22))
    g2d.setColor(getHpTextColor(g2d))
    g2d.drawString(s"$hp/$maxHealth", 238, 415)

    g2d.dispose
    infoImage = img

  }

  /** Returns the Color which the hp is drawn in depending on current health relative to max health */
  def getHpTextColor(g2d: Graphics2D): Color = {
    var out: Color = Color(0, 0, 0)
    if (hp < maxHealth)
      out = Color.RED
    else if (hp > maxHealth)
      out = Color.BLUE
    out
  }

  /** Returns the Color which the armor is drawn with depending on current Armor relative to max armor*/
  def getArmorTextColor(g2d: Graphics2D): Color = {
    var out: Color = Color(0, 0, 0)
    if (armor < maxHealth)
      out = Color.RED
    else if (armor > maxHealth)
      out = Color.BLUE
    out
  }
}
abstract class OnBoard(path: String, infoPath: String)
    extends Card(path: String, infoPath: String) {
  val modifiers = new ArrayBuffer[(OnBoard) => Unit]()

  /** called on entering the board */
  def onEnter(using board: Board): Unit = {
    println("I entered")
  }

  /** Called on destruction */
  def onDestroy(using board: Board): Unit = {
    println("I was destroyed")
  }

  /** called by board at start of each round */
  def onStartOfRound(using board: Board): Unit = {
    println("Start of new round!")
  }
}
