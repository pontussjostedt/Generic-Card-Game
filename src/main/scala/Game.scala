import java.awt.Graphics2D
import Mana.*
class Game {
  given Game = this
  var window = new Window("import steel as steal", Game.dimX, Game.dimY)
  var a = new ManaReserve()()
  var board = new Board("board.png", 50, 50)
  var testHand = new Hand(Deck(), 6*128,0)
  var player1 = new Player("Bob")
  var playerController = new PlayerController(player1)
  var testManaReserve = new ManaReserve((Red, 5), (Blue, 3))()
  var testCost = new ManaCost((Red, 5), (Blue, 2))

  var fpsTimer = new Timer(10000)
  var fps = 0
  
  while(true){
    tick()
    window.render(draw(_))
    checkFrameRate()
  }


  def tick(): Unit = {
      playerController.tick()
  }

  def draw(g2d: Graphics2D): Unit = {
      board.draw(g2d)
      testHand.draw(g2d)
      playerController.draw(g2d)
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
        javax.imageio.ImageIO.read(new java.io.File(path))
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
            this.reset()
        out
    }
}