/**
 * @param predicate when false the buff is removed
 * @param buff the buff
 * @param toBeApplied condition if the buff is to be applied
 * 
 */
class Buff(
    val predicate: (Board, Creature) => Boolean,
    val buff: (Creature) => Unit,
    val toBeApplied: ((Board, Creature) => Boolean)
) {


  /** */
  def apply(board: Board, target: Creature): Unit = {
    if (predicate(board, target))
        if(toBeApplied(board, target))
            buff(target)
  }
}
