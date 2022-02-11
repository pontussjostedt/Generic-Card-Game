class Buff(
    val predicate: (Board, Creature, (Int, Int)) => Boolean,
    val buff: (Creature) => Unit,
    val reverseBuff: (Creature) => Unit,
    val toBeApplied: ((Board, Creature, (Int, Int)) => Boolean)
) {


  /** */
  def apply(board: Board, target: Creature, matrixPos: (Int, Int)): Unit = {
    if (predicate(board, target, matrixPos))
        if(toBeApplied(board, target, matrixPos))
            buff(target)
  }
}
