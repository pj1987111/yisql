package org.apache.spark.sql

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-21
  *  \* Time: 23:49
  *  \* Description: 
  *  \*/
object DFVisitor {
  def showString(df: DataFrame, _numRows: Int,
                 truncate: Int = 20,
                 vertical: Boolean = false) = df.showString(_numRows, truncate, vertical)

}
