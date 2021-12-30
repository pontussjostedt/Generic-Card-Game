import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import scala.collection.mutable.ArrayBuffer
import scala.annotation.targetName
import creatures.Paladin
class Board(path: String, var x: Int = 0, var y: Int = 0)(using ctx: Game) {
  given Board = this
  var arr = Array.fill[Option[OnBoard]](5,10)(None)
  var image: BufferedImage = javax.imageio.ImageIO.read(new File(path))
  /**absolute Bound */
  var bound = new Bound(x,y,image.getWidth,image.getHeight)
  /**matrix Bound */
  var arrBound = new Bound(0,0,arr.length, arr(0).length)
  var cardSize = 128
  var deleteMe = Game.loadImage("highlight.png")

  def draw(g2d: java.awt.Graphics2D): Unit = {
      val pos = getMouseQuadrant()
      g2d.drawImage(image,x,y,image.getWidth, image.getHeight, null)
      for(x <- arr.indices; y <- arr(x).indices){
          arr(x)(y) match {
            case Some(card) => card.draw(g2d, this.x + x*cardSize, this.y + y*cardSize, cardSize, cardSize)
            case None => //println("no card here!")
          }
      }
      g2d.setColor(java.awt.Color.red)
    
      //if(bound.contains(ctx.window.mouseManager.pos))
        //g2d.drawImage(deleteMe,pos(0)*cardSize + x, pos(1)*cardSize + y, cardSize, cardSize, null)
  }

  /**Pos is matrix position */
  def highlightSquare(g2d: Graphics2D, hl: Highlight, pos: (Int, Int)): Unit = {
    if(arrBound.contains(pos)){
      g2d.drawImage(hl.img, pos(0)*cardSize + x, pos(1)*cardSize + y, cardSize, cardSize, null)
      this(pos) match {
        case Some(card) => card.drawInfo(g2d, Game.highlightPos(0), Game.highlightPos(1), card.infoImage.getWidth, card.infoImage.getHeight)
        case None => 
      }
    }
      
  }
  /**Returns Matrix position from absPos*/
  def getMouseQuadrant(): (Int, Int) = {
    var in = ctx.window.mouseManager.pos
    var out = ((in(0)-x)/cardSize, (in(1)-y)/cardSize)
    if(in(0)<x || in(1) < y)
      out = (-1, -1)
    out
  }
  /**Returns Matrix position from absPos*/
  def getMouseQuadrant(pos: (Int, Int)): (Int, Int) = {
    var in = pos
    var out = ((in(0)-x)/cardSize, (in(1)-y)/cardSize)
    if(in(0)<x || in(1) < y)
      out = (-1, -1)
    out
  }


  def getCard(pos: (Int, Int)): Option[Card] = {
    arr(pos(0))(pos(1))
  }

  /**Returns a list of all cards which the filter applies to*/
  def filter(f: (OnBoard, (Int, Int)) => Boolean): Seq[OnBoard] = {
    val out = ArrayBuffer[OnBoard]()
    for(x <- arr.indices; y <- arr(x).indices){
      arr(x)(y) match {
        case Some(card) => if(f(card, (x,y))) then out += card
        case None =>
      }
    }
    out.toSeq
  }

  /**Returns a list of all cards which the filter applies to*/
  def filter(f: (OnBoard) => Boolean): Seq[OnBoard] = {
    val out = ArrayBuffer[OnBoard]()
    for(x <- arr.indices; y <- arr(x).indices){
      arr(x)(y) match {
        case Some(card) => if(f(card)) then out += card
        case None =>
      }
    }
    out.toSeq
  }

  /** Returns if card is defined at pos
   * @param pos matrix coordinates
  */
  def isCard(pos: (Int, Int)): Boolean = {
    var out = false
    if(arrBound.contains(pos))
    this(pos) match
      case None => 
      case Some(i) => out = true
      out
  }

  /** Returns true if Some(Card) and card is in same team
   * @param pos matrix coordinates
   */
  def isAlliedCard(pos: (Int, Int), team: Team): Boolean = {
    var out = false
    if(arrBound.contains(pos))
    this(pos) match
      case None => 
      case Some(i) => out = i.team == team
      out
  }
  /** Returns true if Some(Card) and card is in same team and has not used its turn
   * @param pos matrix coordinates
   */
  def isAlliedWithTurn(pos: (Int, Int), team: Team): Boolean = {
    var out = false
    if(arrBound.contains(pos))
    this(pos) match
      case None => 
      case Some(card) => out = (card.team == team && !card.hasTakenTurn)
      out
  }

  /** Returns true if Some(Card) and card is not in the same team
   *  @param pos matrix coordinates
   */
  def isEnemyCard(pos: (Int, Int), team: Team): Boolean = {
    var out = false
    if(arrBound.contains(pos))
    this(pos) match
      case None => 
      case Some(i) => out = i.team != team
    out
  }

  /** Good you found me use me
   * @param position
   * */
  def isNotCard(pos: (Int, Int)): Boolean = {
    var out = false
    if(arrBound.contains(pos))
      this(pos) match
        case None => out = true
        case Some(i) => 
    out
  }

  def apply(pos: (Int, Int)): Option[OnBoard] = {
    arr(pos(0))(pos(1))
  }

  /*Either
  def set(pos: (Int, Int), in: Option[OnBoard]): Unit = {
    assert(in.isDefined, "------NOT DEFEINED IN SET FUNCTION------")
    arr(pos(0))(pos(1)) = in
    arr(pos(0))(pos(1)).get match {
      case onBoard: OnBoard => onBoard.onEnter(pos(0), pos(1))
      case _ =>
    }
  }
  */


  
  //FIX THIS IT ACTUALLY HURTS MY EYES
  def set(pos: (Int, Int), in: Option[Card]): Unit = {
    var onBoard: OnBoard = null
    if(in.isDefined){
    in.get match {
      case onBo: OnBoard => onBoard = onBo
      case _ => throw Exception("INBOARD IS NULL PLEASE STOP BEING LAZY :)")
    }
    arr(pos(0))(pos(1)) = Some(onBoard)
    arr(pos(0))(pos(1)).get match {
      case onBoard: OnBoard => onBoard.onEnter(pos(0), pos(1))
      case _ =>
    }
    } else {
      arr(pos(0))(pos(1)) = None
    }
  }

  def resetPos(pos: (Int, Int), in: Option[Card]): Unit = {
    var onBoard: OnBoard = null
    if(in.isDefined){
    in.get match {
      case onBo: OnBoard => onBoard = onBo
      case null => throw Exception("INBOARD IS NULL PLEASE STOP BEING LAZY :)")
    }
    arr(pos(0))(pos(1)) = Some(onBoard)
    } else {
      arr(pos(0))(pos(1)) = None
    }
  }
  

  def set(pos: (Int, Int), in: OnBoard): Unit = {
    arr(pos(0))(pos(1)) = Some(in)
    arr(pos(0))(pos(1)).get match {
      case onBoard: OnBoard => onBoard.onEnter(pos(0), pos(1))
      case _ =>
    }
  }
  /**Returns true if mouse is positioned over the board
   * @param pos abs pos
  */
  def contains(pos: (Int, Int)): Boolean = {
    bound.contains(pos)
  }

  def destroy(matrixPos: (Int, Int)): Unit = {
    assert(this(matrixPos).isDefined, "You called destroy incorrectly, can not destroy what does not exist")
    this(matrixPos).get.onDestroy(matrixPos)
    arr(matrixPos(0))(matrixPos(1)) = None
    
  }

  def destroyDead(): Unit = {
    for(x <- arr.indices; y <- arr(x).indices){
      arr(x)(y) match {
        case Some(card) => if(card.toDestroy) then destroy((x,y))
        case None =>
      }
    }
  }

  def foreach(f: (OnBoard) => Unit): Unit = {
    for(x <- arr.indices; y <- arr(x).indices){
      arr(x)(y) match {
        case Some(card) => f(card)
        case None =>
      }
    }
  }
  def newRound(team: Team): Unit = {
    for(x <- arr.indices; y <- arr(x).indices){
      arr(x)(y) match {
        case Some(card) => {card.onStartOfRound((x,y)); if(card.getTeam() == team) then card.onStartOfSelfRound((x,y))}
        case None =>
      }
    }
  }
}

sealed trait Filter {
    /**
     * @param card your onboard object
     * @param matrixPos position of said object
     */
    def apply(card: OnBoard, matrixPos: (Int, Int)): Boolean
    case class Contains(tags: Tag*) extends Filter {
      def apply(card: OnBoard, matrixPos: (Int, Int)): Boolean = {
        var out = false
        if(tags.forall(x => card.tags.contains(x)))
          out = true
        out
      }
    }
  }

  object Filters {
    def containsTag(card: OnBoard, matrixPos: (Int, Int), tags: Tag*): Boolean = {
      var out = false
      if(tags.forall({x => card.tags.contains(x)}))
          out = true
      out
    }
  }
