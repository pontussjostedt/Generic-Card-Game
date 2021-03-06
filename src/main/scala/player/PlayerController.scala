import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Font
import Highlight.*
import scala.collection.mutable.ArrayBuffer
import scala.util.hashing.Hashing.Default
class PlayerController(val player: Player)(using ctx: Game) {
    println("---------CREATING NEW PLAYERCONTROLLER----------")
    given Board = board
    var cardContainer: Option[Card] = None
    var currentState: ControllerState = new DefaultState
    val board = ctx.board
    val hand = player.hand
    val mana = player.mana
    var absPos = ctx.window.mouseManager.pos
    var mousePos = ((absPos(0)-board.x)/board.cardSize, (absPos(1)-board.y)/board.cardSize)
    
    
    val dragTimer = new Timer(250)
    val testTimer = new Timer(500)
    var toHighlight = ArrayBuffer[(Graphics2D) => Unit]()

    def tick(): Unit = {
        updateControlls()
        currentState.tick()
    }

   
    def draw(g2d: java.awt.Graphics2D): Unit = {
        hand.draw(g2d)
        player.draw(g2d)
        if(board.contains(absPos)) then g2d.highlightCardOverBoard(RedHL, absPos)
        if(hand.contains(absPos)) then g2d.highlightCardOverHand(WhiteHL, absPos)
        currentState.draw(g2d)
        drawMana(g2d)

        

    }

    def drawMana(g2d: java.awt.Graphics2D): Unit =  {
        g2d.setFont(new Font("Courier New", 1, 50))
        g2d.drawString(s"${mana.curMana}/${mana.curMaxMana}", 1500, 700)
    }


    def updateControlls(): Unit = {
        absPos = ctx.window.mouseManager.pos
        mousePos = board.getMouseQuadrant()
    }

    abstract class ControllerState {
        def tick(): Unit
        def draw(g2d: Graphics2D): Unit
    }

    case class DefaultState() extends ControllerState {
       assert(!cardContainer.isDefined, "You didnt exit your previous state correctly")
        def tick(): Unit = {
            handDebug()
            if(isValidBoardDrag)
                enterBoardDragState()
            else if(isValidHandDrag)
                println("HAND DRAG A CLOCK")
                println(hand.canAfford(absPos, player))
                enterHandDragState()
         
        }

        def draw(g2d: Graphics2D): Unit = {
            hand.highlightCanNotAfford(g2d, Highlight.WhiteHL, player)
        }


            def isValidBoardDrag: Boolean = {ctx.window.mouseManager.leftPressed && board.isAlliedWithTurn(mousePos, player.team) && cardContainer == None && dragTimer()}
            def isValidHandDrag: Boolean = {ctx.window.mouseManager.leftPressed && hand.bound.contains(absPos) && cardContainer == None && dragTimer() && hand.canAfford(absPos, player)}
    }

    case class DraggingState(var origin: (Int, Int)) extends ControllerState {
        var isAllowedToRelease = false
        def tick(): Unit = {
            //Lock to make everything smoother
            if(!ctx.window.mouseManager.leftPressed)
                isAllowedToRelease = true
            
            if(isOkRelease && board.isNotCard(mousePos)){
                board.resetPos(mousePos, cardContainer)
                cardContainer = None
                currentState = DefaultState()
                dragTimer.reset()
            }
            else if(isOkRelease && board.isEnemyCard(mousePos, player.team)){
                board.resetPos(origin, cardContainer)
                println(origin)
                println("FIGHT FIGHT FIGHT")
                OnBoard.fight(cardContainer.get.asInstanceOf[Creature], board(mousePos).get.asInstanceOf[Creature])
                board.destroyDead()
                
                cardContainer = None
                currentState = DefaultState()
                dragTimer.reset()
            }
            

            def isOkRelease: Boolean = ctx.window.mouseManager.leftPressed && isAllowedToRelease
        }

        def draw(g2d: Graphics2D): Unit = {
            cardContainer.get.drawCard(g2d, absPos(0), absPos(1), 100, 100)
        }
    }

    case class HandDraggingState() extends ControllerState {
        require(cardContainer.isDefined, "CARD CONTAINER NOT DEFINED")
        //require(cardContainer.get match {case a: OnBoard => false; case _ => true}, "Not an onboard unit")
        var insertBound = if(hand.cards.indices.length > 0) then (0 to hand.cards.indices.last+1) else 0 to 0
        var isAllowedToRelease = false
        
        def tick(): Unit = {
            if(!ctx.window.mouseManager.leftPressed)
                isAllowedToRelease = true
            if(isValidBoardInsert)
                board.set(mousePos, cardContainer)
                mana -= cardContainer.get.manaCost
                cardContainer = None
                currentState = DefaultState()
                dragTimer.reset()
            else if(isValidHandInsert){
                hand.insert(hand.getInsertIndex(absPos), cardContainer.get)
                cardContainer = None
                currentState = DefaultState()
                dragTimer.reset()
            }
        }

        def draw(g2d: Graphics2D): Unit = {
            if(isValidInsertPos)
                hand.highlightInsert(g2d, absPos)

            cardContainer.get match {
                case a: OnBoard => a.getPossibleSpawnLocation.foreach{x => board.highlightSquare(g2d, Highlight.WhiteHL, x)}
                case _ =>
            }
            g2d.drawImage(cardContainer.get.image, absPos(0), absPos(1), 100, 100, null)
        }

        def isValidInsertPos: Boolean = insertBound.contains(hand.getInsertIndex(absPos)) && (0 until hand.cardY).contains(absPos(1))
        def isValidHandInsert: Boolean = isValidInsertPos && isAllowedToRelease && ctx.window.mouseManager.leftPressed
        def isValidBoardInsert: Boolean = {
        cardContainer.get match {
            case onboard: OnBoard => ctx.window.mouseManager.leftPressed && isAllowedToRelease && onboard.getPossibleSpawnLocation.contains(mousePos) && board.isNotCard(mousePos)
            case _ => true
        }
        
    
    }
    }

    def handDebug(): Unit = {
        if(ctx.window.keyManager.isKeyPressed(65) && testTimer.resetIf())
            hand.drawCard()
        if(ctx.window.keyManager.isKeyPressed(81) && ctx.turntimer.resetIf())
            ctx.nextRound()
        /*
            ctx.counter += 1
            board.newRound(ctx.controllers(ctx.counter % ctx.controllers.length).player.team)
            println(board.filter(Filters.containsTag(_, _, Tag.FirstStrike, Tag.Human)))
        */
    }

    def enterBoardDragState(): Unit = {
        cardContainer = board(mousePos)
        board.set(mousePos, None)
        currentState = DraggingState(mousePos)
    }

    def enterHandDragState(): Unit = {
        cardContainer = Some(hand.cards(hand.getMouseQuadrant(absPos)))
        hand.cards -= hand.cards(hand.getMouseQuadrant(absPos))
        currentState = HandDraggingState()
    }

    extension (g2d: Graphics2D){
        /** highlights cards
         * @param hl highlight which contains an image
         * @param pos position in absolute cooridnates
        */
        def highlightCardOverBoard(hl: Highlight, pos: (Int, Int)): Unit = {
            board.highlightSquare(g2d, hl, board.getMouseQuadrant())
        }
        /** takes in absolute position
         * @param hl highlight which contains an image
         * @param pos position in absolute cooridnates
        */
        def highlightCardOverHand(hl: Highlight, pos: (Int, Int)): Unit = {
            hand.highlightCard(g2d, WhiteHL, hand.getMouseQuadrant(pos))
        }
    }

}
enum Highlight(path: String){
    var img = Game.loadImage(path)
    def apply: BufferedImage = img
    def draw(x: Int = 0, y: Int = 0, width: Int = img.getWidth, height: Int = img.getHeight)(using g2d: Graphics2D): Unit = {
        g2d.drawImage(img, x, y, width, height, null)
    }
    case WhiteHL extends Highlight("res/highlights/highlight.png")
    case RedHL extends Highlight("res/highlights/redHighlight.png")
    case SleepHL extends Highlight("res/highlights/sleepHighlight.png")
}

enum Team(path: String, val spawnRegion: Set[(Int, Int)]) {
    val flag = Game.loadImage(path)
    def apply: BufferedImage = flag
    case Red extends Team("res/teamFlags/redTeamFlag.png", Bound.forcedAlignmentBound((2,1), 3).toSet)
    case Blue extends Team("res/teamFlags/blueTeamFlag.png", Bound.forcedAlignmentBound((2,6), 3).toSet)
}
