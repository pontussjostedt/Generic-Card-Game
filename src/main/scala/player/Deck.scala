import scala.collection.mutable.ArrayBuffer
class Deck(team: Team) {
  var cards: ArrayBuffer[Card] = ArrayBuffer[Card]()
  for (i <- 0 to 30) {
    cards += FootSoldier(team)
  }
  cards += GoldForgePriest(team)
  cards += AngelicRecruit(team)
  cards += AngelicCommander(team)
  cards += DivineOrderProtector(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  cards += FootSoldier(team)
  //shuffle()
  def getNextCard(): Option[Card] = {
    var out = cards.last
    cards -= cards.last
    //println(cards.length)
    Some(out)
  }

  //Create a none bogo oriented sorting method
  def shuffle(): Unit = {
    for(i <- 0 to 1000)
      var r1 = (math.random*cards.length).toInt
      var r2 = (math.random*cards.length).toInt
      var temp = cards(r1)
      cards(r1) = cards(r2)
      cards(r2) = temp
    
  }
}