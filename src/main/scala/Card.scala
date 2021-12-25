import scala.collection.mutable
import java.awt.image.BufferedImage
import Mana.*
abstract class Card(path: String, infoPath: String = "placeHolderInfo.png") {
  val infoImageSource = Game.loadImage(infoPath)
  val manaCost = new ManaCost((Red, 3), (Blue, 2), (White, 1), (Green, 4))
  val image = Game.loadImage(path)
  var infoImage = Game.loadImage(infoPath)
  var tags = scala.collection.mutable.Set.empty[Tag]
  updateCardImage()

  /**Draws the on board Image(image) of the card
   * @param x,y absolute coordinates
   * @param width,height absolute values
  */
  def drawCard(g2d: java.awt.Graphics2D, x: Int, y: Int, width: Int, height: Int): Unit = 
    g2d.drawImage(image,x,y, width, height, null)

    /** called by board at start of each round */
  def onStartOfRound(): Unit = {

  }

  def updateCardImage(): Unit = {
      //----------UpdateInfoImage---------------
      var img = new BufferedImage(infoImage.getWidth, infoImage.getHeight, BufferedImage.TYPE_4BYTE_ABGR)
      val manaList = manaCost.cost.toVector.sortBy {case (manaType, value) => value < value}
      var g2d = img.createGraphics()
      g2d.drawImage(infoImageSource, 0, 0, img.getWidth, img.getHeight, null)
      val iconWidth = 20
      val iconHeight = 20
      val xIcon = 290
      val yIcon = 4
      var counter = 0
      for(i <- manaList.indices) {
        var icon = manaList(i)(0).icon
        for(j <- 0 until manaList(i)(1)) {
            g2d.drawImage(icon, xIcon - counter*iconWidth, yIcon, iconWidth, iconHeight, null)
            counter += 1
            }
    }
    g2d.dispose
    //--------UpdateBoardImage---------
    infoImage = img

}
  /**Draws the info image of the card
   * @param x,y absolute coordinates
   * @param width,height absolute values
   * If there evere is a problem with performance remake me so that I create a bufferedImage at the start of the game
  */
  def drawInfo(g2d: java.awt.Graphics2D, x: Int, y: Int, width: Int, height: Int): Unit = {
    g2d.drawImage(infoImage, x, y, width, height, null)
  }
}

class TestCard(path: String, infoPath: String = "placeHolderInfo.png") extends Card(path, infoPath) {

}

case class Tag(var tagType: TagType, var info: String = "") {
    
}

enum TagType {
    case Warrior extends TagType
}