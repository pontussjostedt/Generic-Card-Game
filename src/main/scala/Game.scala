import java.awt.Graphics2D
import Mana.*
class Game {
  given Game = this
  val window = new Window("import steel as steal", Game.dimX, Game.dimY)
  var board = new Board("board.png", 50, 50)
  //------------------------------------------------------------------------------
  val player1 = new Player("player 1", Team.Red)
  val player2 = new Player("player 2", Team.Blue)
  //------------------------------------------------------------------------------
  val controllers = Vector(PlayerController(player1), PlayerController(player2))
  var activePlayerController = controllers(0)
  var counter = 0
  var fpsTimer = new Timer(10000)
  val turntimer = new Timer(1000)
  var fps = 0
  
  while(true){
    tick()
    window.render(draw(_))
    checkFrameRate()
  }


  def tick(): Unit = {
      activePlayerController = controllers(counter % controllers.length)
      activePlayerController.tick()
  }

  def draw(g2d: Graphics2D): Unit = {
      board.draw(g2d)
      activePlayerController.draw(g2d)
      player1.draw(g2d)
  }

  def checkFrameRate(): Unit  ={
    fps += 1
    if(fpsTimer.resetIf()){
      println(s"average fps over the last 10s = ${fps/10}")
      fps = 0
    }
  }
 
  
}

object Game {
    val dimX = 1920
    val dimY = 1000
    val highlightPos = (800,400)
    def loadImage(path: String): java.awt.image.BufferedImage = {
        try {
        javax.imageio.ImageIO.read(new java.io.File(path))
        }
        catch {
            case _ => throw Exception(s"file could not be found($path)")
        }
    }

    extension (value: Int) {
        def clamp(range: Range): Int = {
            math.max(range.head, math.min(range.last, value))
        }
    }
}

/** Use me to check if coordinates are inside of a zone
 * @param {x,y} starts at
 * @param width width of bound
 * @param height height of bound
*/
class Bound(val x: Int, val y: Int, val width: Int, val height: Int) {
    var xRange = x until width + x
    var yRange = y until height + y
    def contains(x: Int, y: Int): Boolean = {
        var out = false
        if(xRange.contains(x) && yRange.contains(y)){
            out = true
        }
        out
    }
    def contains(pos: (Int, Int)): Boolean = {
        var out = false
        if(xRange.contains(pos(0)) && yRange.contains(pos(1))){
            out = true
        }
        out
    }
}

object Bound {
    def forcedAlignmentBound(pos: (Int, Int), side: Int): Bound = {
        assert(side % 2 == 1, s"Not an allowed bound because the side is even($side)")
        val sideOffSet: Int = side/2
        val x = pos(0) - sideOffSet
        val y = pos(1) - sideOffSet
        val outSide = 2*sideOffSet + 1
        Bound(x, y, outSide, outSide)
    }
}

/**Returns true after the time is done, input is in ms and reset does what it says it does */
class Timer(ms: Int) {
    var t1 = System.currentTimeMillis
    def reset(): Unit = {
        t1 = System.currentTimeMillis
    }
    def apply(): Boolean = {System.currentTimeMillis - t1 > ms}
    /** check if timer is done and if it is resets it*/
    def resetIf(): Boolean = {
        var out = System.currentTimeMillis - t1 > ms
        if(out)
            reset()
        out
    }
}