import AtariGo.Board

import java.io.{File, PrintWriter}
import scala.io.Source

object AtariGOUtils {

  //T4
  def printBoard(board:Board) = {
    board map (row => //percorre as linhas (List[Stone])
    {
      row map (elem => //percorre os elementos das linhas (Stone)
        if(elem == Stone.White) print(" W ")
        else if(elem == Stone.Black) print(" B ")
        else print(" . ")
        )
      println("") //a cada linha que passa, muda a linha
    }
      )
  }

  val home = System.getProperty("user.home")
  val file = "lastMyRandom"

  def writeMyRandomStateToFile(r:MyRandom) = {
    val pw = new PrintWriter(new File(file))
    pw.write(r.seed.toString)
    pw.close()
  }

  def readMyRandomStateToFile():Long = {
    val rw = Source.fromFile(file)
    val seed = rw.getLines().next().toLong
    rw.close()
    seed
  }
}
