import java.awt.Font
import java.awt.image.BufferedImage
class Creature(path: String, infoPath: String = "placeHolderInfo.png")
    extends OnBoard(path, infoPath) {
  var maxHealth = 20
  var hp = maxHealth

  override def updateCardImage(): Unit = {
    var img = new BufferedImage(
      infoImage.getWidth,
      infoImage.getHeight,
      BufferedImage.TYPE_4BYTE_ABGR
    )
    val manaList = manaCost.cost.toVector.sortBy { case (manaType, value) =>
      value < value
    }
    var g2d = img.createGraphics()
    g2d.drawImage(infoImage, 0, 0, img.getWidth, img.getHeight, null)
    val iconWidth = 20
    val iconHeight = 20
    val xIcon = 290
    val yIcon = 4
    var counter = 0
    for (i <- manaList.indices) {
      var icon = manaList(i)(0).icon
      for (j <- 0 until manaList(i)(1)) {
        g2d.drawImage(
          icon,
          xIcon - counter * iconWidth,
          yIcon,
          iconWidth,
          iconHeight,
          null
        )
        counter += 1
      }
    }
    g2d.setFont(new Font("Courier New", 1, 22))
    g2d.setColor(java.awt.Color(0,0,0))
    g2d.drawString(s"$hp/$maxHealth", 238, 415)

    g2d.dispose
    infoImage = img

  }
}
abstract class OnBoard(path: String, infoPath: String)
    extends Card(path: String, infoPath: String) {}
