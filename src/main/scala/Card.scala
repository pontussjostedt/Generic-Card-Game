import scala.collection.mutable
import java.awt.image.BufferedImage
import Mana.*
//Test
abstract class Card(path: String, infoPath: String = "placeHolderInfo.png", var team: Team) {
  protected val infoImageSource = Game.loadImage(infoPath)
  val manaCost = new ManaCost((Red, 3), (Blue, 2), (White, 1), (Green, 4))
  var image = Game.loadImage(path)
  var infoImage = Game.loadImage(infoPath)
  var tags = scala.collection.mutable.Set.empty[Tag]
  updateCardImage()

  /**Draws the on board Image(image) of the card
   * @param x,y absolute coordinates
   * @param width,height absolute values
  */
  def drawCard(g2d: java.awt.Graphics2D, x: Int, y: Int, width: Int, height: Int): Unit = 
    g2d.drawImage(image,x,y, width, height, null)

  /** updates infoImage */
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

/**Used to flag different types of creatures */
enum Tag {
  case FirstStrike extends Tag
  case Warrior extends Tag
  case Human extends Tag
  case Priest extends Tag
  case Stunned extends Tag
  case Pierce extends Tag
  case Angel extends Tag
  case Flying extends Tag
}