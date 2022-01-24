import java.awt.Graphics2D
import scala.collection.mutable.ArrayBuffer
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