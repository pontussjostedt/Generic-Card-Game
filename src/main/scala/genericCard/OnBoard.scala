import java.awt.Font
import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer
import java.awt.Color
import java.awt.Graphics2D
import scala.collection.mutable
open class Creature(
    val power: Int,
    val maxHealth: Int,
    val maxArmor: Int,
    var manaCost: Int,
    path: String,
    infoPath: String = "placeHolderInfo.png",
    team: Team
) extends OnBoard(path, infoPath, team) {
  var hp: Int = maxHealth
  var armor = maxArmor
  updateCardImage()

  /** Called at the start of round */
  override def onStartOfRound(
      matrixPos: (Int, Int)
  )(using board: Board): Unit = {
    armor = maxArmor
    onStartOfRound.foreach(f => f(board, this, matrixPos))
    updateCardImage()
  }
  override def onStartOfSelfRound(
      matrixPos: (Int, Int)
  )(using board: Board): Unit = {
    if (tags.contains(Tag.Stunned))
      tags -= Tag.Stunned
    else
      setNoTurnTaken()
  }

  /** damages armor and health accordingly to damage and checks if creature is
    * dead, also updates cardImage
    * @param dmgDealt
    *   damage to card
    */
  def damage(dmgDealt: Int): Unit = {
    var dmgLeft = dmgDealt
    dmgLeft = math.max(dmgLeft - armor, 0)
    armor = math.max(armor - dmgLeft, 0)
    hp -= dmgLeft
    updateCardImage()
    if (hp <= 0) {
      toDestroy = true
    }
  }

  def heal(restoration: Int): Unit = {
    hp = math.min(restoration + hp, maxHealth)
  }

  override def updateCardImage(): Unit = {
    var img = new BufferedImage(
      infoImage.getWidth,
      infoImage.getHeight,
      BufferedImage.TYPE_4BYTE_ABGR
    )
    var g2d = img.createGraphics()
    g2d.drawImage(infoImageSource, 0, 0, img.getWidth, img.getHeight, null)
    val iconWidth = 20
    val iconHeight = 20
    val xIcon = 290
    val yIcon = 4
    var counter = 0
    /*
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
     */
    g2d.setFont(new Font("Courier New", 1, 22))
    g2d.setColor(getHpTextColor(g2d))
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
    g2d.drawImage(boardImageSource, 0, 0, img.getWidth, img.getHeight, null)
    g2d.drawImage(
      team.flag,
      355,
      -60,
      team.flag.getWidth,
      team.flag.getHeight,
      null
    )
    g2d.setFont(new Font("Courier New", 1, 60))
    g2d.setColor(Color.black)
    g2d.drawString(armor.toString, 400, 455)
    g2d.drawString(s"$power/$hp", 25, 455)
    image = img
    g2d.dispose
  }

  /** Returns the Color which the hp is drawn in depending on current health
    * relative to max health
    */
  def getHpTextColor(g2d: Graphics2D): Color = {
    var out: Color = Color(0, 0, 0)
    if (hp < maxHealth)
      out = Color.RED
    else if (hp > maxHealth)
      out = Color.BLUE
    out
  }

  /** Returns the Color which the armor is drawn with depending on current Armor
    * relative to max armor
    */
  def getArmorTextColor(g2d: Graphics2D): Color = {
    var out: Color = Color(0, 0, 0)
    if (armor < maxHealth)
      out = Color.RED
    else if (armor > maxHealth)
      out = Color.BLUE
    out
  }

  def fight(card: OnBoard): Unit = {
    card match {
      case a: Creature =>
      case _           => println("NOT A CREATURE MONKA")
    }
  }
}
abstract class OnBoard(path: String, infoPath: String, team: Team)
    extends Card(path: String, infoPath: String, team) {
  var hasTakenTurn = false
  var toDestroy = false
  val modifiers = new ArrayBuffer[(OnBoard) => Unit]()
  val onStartOfRound = new ArrayBuffer[(Board, OnBoard, (Int, Int)) => Unit]()
  val boardImageSource = Game.loadImage(path)

  /** called on entering the board */
  def onEnter(matrixPos: (Int, Int))(using board: Board): Unit = {
    println("I entered")
  }

  /** Called on destruction */
  def onDestroy(matrixPos: (Int, Int))(using board: Board): Unit = {
    println(s"I was destroyed at ($matrixPos)")
  }

  /** called by board at start of each round */
  def onStartOfRound(matrixPos: (Int, Int))(using board: Board): Unit = {
    onStartOfRound.foreach(f => f(board, this, matrixPos))
  }

  /** called by board at start of players turn */
  def onStartOfSelfRound(matrixPos: (Int, Int))(using board: Board): Unit = {
    println("Start of my round!")
  }

  /** Called before combat */
  def onCombat(card: Card)(using board: Board): Unit = {}

  /** Called on taking dmage */
  def onDamage(using board: Board): Unit = {}

  /** Makes this card unvailable override me if you want to change this logic
    */
  def setTurnTaken(): Unit = {
    println("Im on cd")
    hasTakenTurn = true
  }

  /** Makes this card available override me if you want to change this logic
    */
  def setNoTurnTaken(): Unit = {
    hasTakenTurn = false
  }

  def getTeam(): Team = {
    team
  }

  def getPossibleSpawnLocation(using board: Board): Set[(Int, Int)] = {
    given Board = board
    val xs = team.spawnRegion ++ getExtraSpawn(board)
    xs
  }

  def getExtraSpawn(board: Board): Set[(Int, Int)] = {
    Set.empty[(Int, Int)]
  }

  def draw(g2d: Graphics2D, x: Int, y: Int, width: Int, height: Int): Unit = {
    g2d.drawImage(image, x, y, width, height, null)
    if (hasTakenTurn)
      drawSleepOverlay(g2d, x, y, width, height)
  }

  def drawSleepOverlay(
      g2d: Graphics2D,
      x: Int,
      y: Int,
      width: Int,
      height: Int
  ): Unit = {
    g2d.drawImage(Highlight.SleepHL.img, x, y, width, height, null)
  }
}

object OnBoard {

  /** forces two cards to fight eachother */

  def fight(attackingCreature: Creature, defendingCreature: Creature): Unit = {
    if (attackingCreature.tags.contains(Tag.FirstStrike))
      firstStrikeFight(attackingCreature, defendingCreature)
    else
      normalFight(attackingCreature, defendingCreature)
    updateImages(attackingCreature, defendingCreature)

    /*
    if(attackingCreature.hp <= 0)
      attackingCreature.toDestroy = true
    if(defendingCreature.hp <= 0)
      attackingCreature.toDestroy= true
     */
    attackingCreature.setTurnTaken()
  }

  def firstStrikeFight(
      attackingCreature: Creature,
      defendingCreature: Creature
  ): Unit = {
    defendingCreature.damage(attackingCreature.power)
    if (defendingCreature.hp > 0)
      attackingCreature.damage(defendingCreature.power)
  }

  def normalFight(
      attackingCreature: Creature,
      defendingCreature: Creature
  ): Unit = {
    defendingCreature.damage(attackingCreature.power)
    attackingCreature.damage(defendingCreature.power)
  }

  def updateImages(in: OnBoard*): Unit = {
    in.foreach(_.updateCardImage())
  }
}
