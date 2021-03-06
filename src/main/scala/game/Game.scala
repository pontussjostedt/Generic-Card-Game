import java.awt.Graphics2D
class Game {
  given Game = this
  val window = new Window("import steel as steal", Game.dimX, Game.dimY)
  val board = new Board("res/boards/board.png", 50, 50)
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
      activePlayerController.tick()
  }

  def draw(g2d: Graphics2D): Unit = {
      board.draw(g2d)
      activePlayerController.draw(g2d)
  }

  def checkFrameRate(): Unit = {
    fps += 1
    if(fpsTimer.resetIf()){
      println(s"average fps over the last 10s = ${fps/10}")
      fps = 0
    }
  }

  def nextRound(): Unit = {
      counter += 1
      activePlayerController = controllers(counter % controllers.length)
      board.newRound(activePlayerController.player.team)
      activePlayerController.player.mana.onStartOfRound()
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

    extension (value: Range) {
        def clamp(range: Range): Range = {
            value.head.clamp(range) to value.last.clamp(range)
        }
    }
}

/** Use me to check if coordinates are inside of a zone
 * @param {x,y} starts at
 * @param width width of bound
 * @param height height of bound
*/
class Bound(var xRange: Range, var yRange: Range) {
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

    import Game.clamp
    def clamp(bound: Bound): Bound  = {
        val newXRange = xRange.clamp(bound.xRange)
        val newYRange = yRange.clamp(bound.yRange)
        new Bound(newXRange, newYRange)
    }

    def toSet: Set[(Int, Int)] = {
        val out = scala.collection.mutable.ArrayBuffer[(Int, Int)]()
        for(x <- xRange; y <- yRange) {
            out += (x -> y)
        }
        out.toSet
    }

    override def toString: String = {
        s"($xRange,$yRange)"
    }
}

object Bound {
    def apply(x: Int, y: Int, width: Int, height: Int): Bound = {
        var xRange = x until width + x
        var yRange = y until height + y
        new Bound(xRange, yRange)
    }
    def forcedAlignmentBound(pos: (Int, Int), side: Int): Bound = {
        assert(side % 2 == 1, s"Not an allowed bound because the side is even($side)")
        val sideOffSet: Int = side/2
        val x = pos(0) - sideOffSet
        val y = pos(1) - sideOffSet
        val outSide = 2*sideOffSet + 1
        Bound(x, y, outSide, outSide)
    }
}

/**Returns true after the time is done, input is in ms and reset does what it says it does
 * @param ms time in milliseconds
 */
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