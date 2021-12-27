import java.awt.Font
import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer
import java.awt.Color
import java.awt.Graphics2D
class Creature(path: String, infoPath: String = "placeHolderInfo.png", team: Team)
    extends OnBoard(path, infoPath, team) {
  val maxHealth: Int = 1000
  val hp: Int = 1000
  //Här är hp = 0 när jag printar
  val maxArmor = 2
  val armor = maxArmor

  val power = 4

  /** Called at the start of round */
  override def onStartOfRound(using board: Board): Unit = {
    //armor = maxArmor
  }

  /** damages armor and health accordingly to damage and checks if creature is
    * dead, also updates cardImage
    * @param dmgDealt damage to card
    */
  def damage(dmgDealt: Int): Unit = {
    var dmgLeft = dmgDealt
    //armor -= dmgLeft
    dmgLeft = math.max(dmgLeft - armor, 0)
    //hp -= dmgLeft
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
    println(s"$hp/$maxHealth")
    g2d.drawString(s"$hp/$maxHealth", 238, 415)
    infoImage = img
    //-------------------------------------------------

    g2d.dispose
    img = new BufferedImage(
      image.getWidth,
      image.getHeight,
      BufferedImage.TYPE_4BYTE_ABGR
    )
    g2d = img.createGraphics()
    g2d.drawImage(image, 0,0, img.getWidth, img.getHeight, null)

    image = img
    g2d.dispose
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
abstract class OnBoard(path: String, infoPath: String, team: Team)
    extends Card(path: String, infoPath: String, team) {
  val modifiers = new ArrayBuffer[(OnBoard) => Unit]()

  /** called on entering the board */
  def onEnter(matrixPos: (Int, Int))(using board: Board): Unit = {
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

  /**Called before combat */
  def onCombat(card: Card)(using board: Board): Unit = {

  }

  /** Called on taking dmage */
  def onDamage(using board: Board): Unit = {

  }
}

object OnBoard {
    /** forces two cards to fight eachother */
    def fight(card1: Creature, card2: Creature): Unit = {
      card1.damage(card2.power)
      card2.damage(card1.power)
      println("MORTAL FIGHT BAT BAH BAHA BAHA BAH")
    }
  }