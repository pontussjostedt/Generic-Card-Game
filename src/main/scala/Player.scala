import Mana.*
import Game.*
import scala.collection.mutable.ArrayBuffer
import java.awt.Graphics2D
class Player(val name: String, val team: Team)(using ctx: Game) {
  var reserve = ManaReserve((Red, 20), (Blue, 10), (White, 2), (Green, 30), (Black, 10))(1700, Game.dimY)
  val hand = new Hand(Deck(team), 6*128, 0)

  def draw(g2d: Graphics2D): Unit = {
    var toDraw = reserve.curMan.filter{case (key, value) => value > 0}.toVector.sortBy{case (key, value) => value > value}
    for(i <- toDraw.indices){
      g2d.drawString(toDraw(i)(1).toString, reserve.x, reserve.lowestY)
      g2d.drawImage(toDraw(i)(0).icon, reserve.x, reserve.lowestY - i * reserve.iconY, reserve.iconX, reserve.iconY, null)
    }

  }
}

class Deck(team: Team) {
  var cards: ArrayBuffer[Card] = ArrayBuffer[Card]()
  for (i <- 0 to 30) {
    cards += Creature(4,6,2,"res/white/testPaladin/boardImage.png", "paladinInfo.png", team)
  }
  cards += CutThroat(team)
  cards += GoldForgePriest(team)
  cards += AngelicRecruit(team)
  cards += AngelicCommander(team)
  cards += DivineOrderProtector(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  //shuffle()
  def getNextCard(): Option[Card] = {
    var out = cards.last
    cards -= cards.last
    //println(cards.length)
    Some(out)
  }

  //Create a none bogo oriented sorting method
  def shuffle(): Unit = {
    for(i <- 0 to 1000)
      var r1 = (math.random*cards.length).toInt
      var r2 = (math.random*cards.length).toInt
      var temp = cards(r1)
      cards(r1) = cards(r2)
      cards(r2) = temp
    
  }
}

class Hand(deck: Deck, x: Int, y: Int)(using ctx: Game) {
  var cardX = 63 * 2
  var cardY = 88 * 2
  var cards = ArrayBuffer[Card]()
  /**Absolute coordinates! */
  def bound = Bound(x, y, cardX * cards.length, cardY)
  for (i <- 0 to 7)
  deck.getNextCard() match {
    case Some(card) => cards += card
    case None       => println("HELLO")
  }

  def drawCard(): Unit = {
    deck.getNextCard() match {
      case Some(card) => {cards += card}
      case None       => println("DECK EMPTY :)")
    }

  }

  def draw(g2d: Graphics2D): Unit = {
    for (i <- cards.indices) {
      cards(i).drawInfo(g2d, x + i * cardX, y, cardX, cardY)
      //g2d.drawImage(cards(i).infoImage, x + i * cardX, y, cardX, cardY, null)
    }
  }

  def updateBound(): Unit = {
    //bound = new Bound(x,y, cardX*cards.length, cardY)
  }

  //Prolly a bit over defensive since this will only be called if already in bounds :)
  def getMouseQuadrant(in: (Int, Int)): (Int) = {
    var out = ((in(0) - x) / cardX)
    if (in(0) < x && !bound.yRange.contains(y))
      out = -1
    //println(out)
    out
  }

  /** highlights selected card */
  def highlightCard(g2d: Graphics2D, hl: Highlight, index: Int): Unit = {
    assert(
      cards.indices.contains(index),
      s"WARNING------Youre out of bounds :) $index out of ${cards.indices}"
    )
    var card = cards(index)
    g2d.drawImage(hl.img, x + index * cardX, y, cardX, cardY, null)
    card.drawInfo(g2d, Game.highlightPos(0), Game.highlightPos(1), card.infoImage.getWidth, card.infoImage.getHeight)
  }

  def highlightInsert(g2d: Graphics2D, pos: (Int, Int)): Unit = {
    var insertIndex = getInsertIndex(pos)
    var p1 = (x + cardX * insertIndex, y)
    var p2 = (x + cardX * insertIndex, y + cardY)
    g2d.setStroke(java.awt.BasicStroke(5))
    g2d.setColor(java.awt.Color.RED)
    g2d.drawLine(p1(0), p1(1), p2(0), p2(1))
    
  }
  /** takes in absolute position */
  def contains(pos:(Int, Int)): Boolean = {
    bound.contains(pos)
  }

  /**
  *@param pos position in absolute coordinates
  */
  def getInsertIndex(pos: (Int, Int)): Int = {
    var insertPos = (pos(0)+cardX/2, pos(1))
    getMouseQuadrant(insertPos)
  }

  def insert(index: Int, elem: Card): Unit = {
    cards.insert(index, elem)
  }
}

/**Mana for player not cost
 * @param x position to drawFrom
 * @param lowestY -----lowest point to draw from, **NOT THE USUAL**-----
 */
class ManaReserve(in: (Mana, Int)*)(val x: Int = 0, val lowestY: Int = 0) {
  import Mana.*
  val iconX = 64
  val iconY = 64
  val height = Mana.values.length * iconY
  var maxMan = scala.collection.mutable.Map.empty[Mana, Int]
  var curMan = scala.collection.mutable.Map.empty[Mana, Int]
  init()

  /** Adds all mana types from @Mana to register
    */
  def init(): Unit = {
    Mana.values.foreach(maxMan += (_, 0))
    Mana.values.foreach(curMan += (_, 0))

    in.foreach(x => maxMan(x(0)) = x(1))
    reset()
  }

  /** Sets curMan to maxMan
    */
  def reset(): Unit = {
    maxMan.foreach{case (key, value) => curMan(key) = value}
  }

  def +=(in: (Mana, Int)): Unit = {
    curMan(in(0)) += in(1)
  }

  def -=(in: ManaCost): Unit = {
    in.cost.foreach { case (key, value) =>
      curMan(key) = (curMan(key) - value).clamp(0 to Int.MaxValue)
      
    }
    println(curMan)
  }

  def apply(in: Mana): Int = {
    curMan(in)
  }

  def >= (in: ManaCost): Boolean = {
    in.cost.forall{ case (key, value) =>
      curMan(key) >= in.cost(key)
    }
  }
}

class ManaCost(in: (Mana, Int)*){
  var cost = scala.collection.mutable.Map.empty[Mana, Int].withDefault(index => 0)
  in.foreach(cost += _)
  
  def apply(key: Mana): Int = {
    cost(key)
  }
}

enum Mana(val path: String) {
  println(path)
  val icon = Game.loadImage(path)
  case Red extends Mana("res/manaIcons/red.png")
  case Green extends Mana("res/manaIcons/green.png")
  case Black extends Mana("res/manaIcons/black.png")
  case Blue extends Mana("res/manaIcons/blue.png")
  case White extends Mana("res/manaIcons/white.png")
}
