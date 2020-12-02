package commons.lib.test.`2d`

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


fun main() {
    val test = Map2d(6, 6)
    test.show()
    var currentPosition = Position(0, 0)
    test.setCell(currentPosition, 8)
    val path = test.getShortestPath(currentPosition, Position(5, 5))
    println("path size %d".format(path.size))
    while (path.isNotEmpty()) {
        test.setCell(currentPosition, 0)
        currentPosition = path.poll()
        test.setCell(currentPosition, 8)
        test.show()
    }
}

class Position(val row: Int, val column: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (row != other.row) return false
        if (column != other.column) return false

        return true
    }

    override fun hashCode(): Int {
        var result = row
        result = 31 * result + column
        return result
    }

    override fun toString(): String {
        return "(%d, %d)".format(row, column)
    }
}

class Map2d(rows: Int, columns: Int) {
    val data: ArrayList<ArrayList<Int>> = ArrayList()
    private val traversableValue = ArrayList<Int>()

    companion object {
        const val ROW = 0
        const val COL = 1
    }

    init {
        for (i in 0 until rows) {
            val colData = ArrayList<Int>()
            for (j in 0 until columns) {
                colData.add(0);
            }
            data.add(colData)
        }
        traversableValue.add(0)
        traversableValue.add(1)
    }

    fun getShortestPath(start: Position, end: Position): Queue<Position> {
        val path: Queue<Position> = LinkedList<Position>()
        return getShortestPath(start, end, path, 30)
    }

    // TODO better implem is required
    private fun getShortestPath(currentPosition: Position, end: Position, path: Queue<Position>, limit: Int): Queue<Position> {
        // val path: Queue<Position> = LinkedList<Position>()
        if (currentPosition == null || currentPosition.row == end.row && currentPosition.column == end.column) {
            return path
        }
        val nextPosition = getNextPosition(currentPosition, end, path,false)
        println("adding position : %s".format(nextPosition))
        path.offer(nextPosition)
        return getShortestPath(nextPosition, end, path, limit - 1)
    }

    private fun getNextPosition(position: Position, end: Position, excludedPosition : Collection<Position> , diagonal: Boolean): Position {
        // Maybe prioritize some area more than other for traversable values (between street/road, trotoire terre battues etc..)
        val possiblePositions = ArrayList<Position>()
        if (position.row - 1 > 0) {
            val hypotheticalPos = Position(position.row - 1, position.column)
            if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                if (!excludedPosition.contains(hypotheticalPos)) {
                    possiblePositions.add(hypotheticalPos)
                }
            }
        }
        if (position.row + 1 < data.size) {
            val hypotheticalPos = Position(position.row + 1, position.column)
            if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                if (!excludedPosition.contains(hypotheticalPos)) {
                    possiblePositions.add(hypotheticalPos)
                }
            }
        }
        if (position.column - 1 > 0) {
            val hypotheticalPos = Position(position.row, position.column - 1)
            if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                if (!excludedPosition.contains(hypotheticalPos)) {
                    possiblePositions.add(hypotheticalPos)
                }
            }
        }
        if (position.column + 1 < data[0].size) {
            val hypotheticalPos = Position(position.row, position.column + 1)
            if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                if (!excludedPosition.contains(hypotheticalPos)) {
                    possiblePositions.add(hypotheticalPos)
                }
            }
        }

        if (diagonal) {
            if (position.row - 1 > 0 && position.column - 1> 0) {
                val hypotheticalPos = Position(position.row - 1, position.column - 1)
                if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                    if (!excludedPosition.contains(hypotheticalPos)) {
                        possiblePositions.add(hypotheticalPos)
                    }
                }
            }
            if (position.row - 1 > 0 &&  position.column + 1 < data[0].size) {
                val hypotheticalPos = Position(position.row - 1, position.column + 1)
                if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                    if (!excludedPosition.contains(hypotheticalPos)) {
                        possiblePositions.add(hypotheticalPos)
                    }
                }
            }
            if (position.row + 1 < data.size && position.column + 1 < data[0].size) {
                val hypotheticalPos = Position(position.row + 1, position.column + 1)
                if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                    if (!excludedPosition.contains(hypotheticalPos)) {
                        possiblePositions.add(hypotheticalPos)
                    }
                }
            }
            if (position.row + 1 < data.size && position.column - 1 > 0) {
                val hypotheticalPos = Position(position.row + 1, position.column - 1)
                if (traversableValue.contains(getCellValue(hypotheticalPos))) {
                    if (!excludedPosition.contains(hypotheticalPos)) {
                        possiblePositions.add(hypotheticalPos)
                    }
                }
            }
        }
        if (possiblePositions.isEmpty()) {
            TODO("need to decide")
        }
        var bestPosition : Position = possiblePositions[0]
        if (possiblePositions.size == 1) {
            return bestPosition
        }
        for (i in 1 until possiblePositions.size) {
            if (getDistanceScoreLowIsBetter(bestPosition, end) > getDistanceScoreLowIsBetter(possiblePositions[i], end)) {
                bestPosition = possiblePositions[i]
            }
        }
        return bestPosition
    }

    private fun getDistanceScoreLowIsBetter(position1 : Position, position2 : Position) : Int {
        return abs(position1.row - position2.row + position1.column - position2.column)
    }

    fun getCellValue(position: Position): Int {
        return data[position.row][position.column]
    }

    fun setCell(position: Position, value: Int) {
        data[position.row][position.column] = value
    }

    fun setCell(row: Int, col: Int, value: Int) {
        data[row][col] = value
    }

    fun show() {
        for (i in 0 until data.size) {
            for (j in 0 until data[i].size) {
                print("%6d ".format(data[i][j]))
            }
            println()
        }
        println()
    }

}