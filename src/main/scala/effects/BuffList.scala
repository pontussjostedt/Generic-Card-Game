

class BuffList {
    private var underlying = scala.collection.mutable.ArrayBuffer[Buff]();
    /** */
    def append(buff: Buff): Unit = {
        underlying += buff;
    }

    /** */
    def += (buff: Buff): Unit = {
        append(buff)
    }

    /**Reverses all buffs and then applies all*/
    def update(target: Creature)(using board: Board): Unit = {
        cleanUp(target, board)
        target.reset()
        underlying.foreach(buff => buff.apply(board, target))
    }

    /**Removes buffs which nolonger meat their predicate*/
    def cleanUp(target: Creature, board: Board): Unit = {
        underlying = underlying.filter(buff => buff.predicate(board, target)) 
    }
}
