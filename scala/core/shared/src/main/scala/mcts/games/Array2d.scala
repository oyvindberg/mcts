package mcts.games

import mcts.games.Array2d.{Column, Index, Row}

import scala.reflect.ClassTag

/**
  * Squeeze a 2D array into a 1D array. Provide an immutable interface.
  */
class Array2d[T](array: Array[T], val numCols: Int, val numRows: Int) {

  def index(col: Column, row: Row): Index =
    col * numRows + row

  def apply(col: Column, row: Row): T =
    apply(index(col, row))

  def apply(index: Index): T =
    array(index)

  def updated(col: Column, row: Row, value: T): Array2d[T] =
    updated(index(col, row), value)

  def map[U: ClassTag](f: (Column, Row, T) => U): Array2d[U] = {
    val newArray = Array.ofDim[U](array.length)

    var col = 0
    while (col < numCols) {

      var row = 0
      while (row < numRows) {
        val idx = index(col, row)
        newArray(idx) = f(col, row, array(idx))
        row += 1
      }
      col += 1
    }

    new Array2d[U](newArray, numCols, numRows)
  }

  def updated(idx: Index, value: T): Array2d[T] = {
    val cloned = array.clone()
    cloned(idx) = value
    new Array2d(cloned, numCols, numRows)
  }
}

object Array2d {
  def apply[T: ClassTag](numCols: Int, numRows: Int, content: T): Array2d[T] =
    new Array2d[T](Array.fill(numCols * numRows)(content), numCols, numRows)

  type Column = Int
  type Row    = Int
  type Index  = Int
}
