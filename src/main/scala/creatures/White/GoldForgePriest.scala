class GoldForgePriest(team: Team) extends Creature(2,4,0,3, "res/white/goldForgeHealer/goldForgeHealerBoard.png","res/white/goldForgeHealer/goldForgeHealerInfo.png", team){
  onStartOfRound += {case(board, card, pos) => heal(board, card, pos)}
  tags += Tag.Human
  tags += Tag.Pierce
  def heal(board: Board, card: OnBoard, pos: (Int, Int)): Unit = {
      val xs = board.getCardsInSquare(pos, 3, {_.getTeam() == team})
      xs.foreach{x => 
        x match {
            case creature: Creature => creature.heal(2)
            case _ =>
        }
      }
  }
}
