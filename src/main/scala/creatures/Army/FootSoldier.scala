class FootSoldier(team: Team) extends Creature(2,2,1, 2, "res/army/footSoldier/footSoldierBoard.png", "res/army/footSoldier/footSoldierInfo.png", team){
  override def getExtraSpawn(board: Board): Set[(Int, Int)] = {
      val out = scala.collection.mutable.Set.empty[(Int, Int)]
      board.positionFilter({ case (card) =>
          card match {
              case footSoldier: FootSoldier => footSoldier.getTeam() == team
              case _ => false
          }
      }).foreach { matrixPos =>
        out ++= board.getAdjacentPos(matrixPos).filter(matrixPos => !board(matrixPos).isDefined)
      }
      out.toSet
  }

  def pred(board: Board, target: Creature): Boolean = {
      println("CHECK THAT PRED")
      true;
  }
  def pred2(board: Board, target: Creature): Boolean = {
      println("is this to be applied?")
      true;
  }
}
