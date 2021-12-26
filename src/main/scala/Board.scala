import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Graphics2D
class Board(path: String, var x: Int = 0, var y: Int = 0)(using ctx: Game) {
  var arr = Array.fill[Option[Card]](5,10)(None)
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
            case Some(card) => g2d.drawImage(card.image, this.x + x*cardSize, this.y + y*cardSize, cardSize, cardSize, null) 
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

  def apply(pos: (Int, Int)): Option[Card] = {
    arr(pos(0))(pos(1))
  }

  def set(pos: (Int, Int), in: Option[Card]): Unit = {
    arr(pos(0))(pos(1)) = in
  }

  def contains(pos: (Int, Int)): Boolean = {
    bound.contains(pos)
  }
}

