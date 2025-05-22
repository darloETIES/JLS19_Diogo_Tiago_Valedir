package model

import model.AtariGo.Board
import model.Stone.Stone

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.io.Source
import scala.io.StdIn.readLine

object AtariGOUtils {




  def startMenu(size: Int, stonesToWin: Int, timeLimit: Int, difficultyLevel: Int, playerColor: Stone): Int = {
    println()
    println("= = = = = = = = = = = = ATARI GO  = = = = = = = = = = = = = = = =")
    print("=")
    println("\t1. Play\t\t\t\t\t\t\t\t\t\t\t\t\t\t=")
    print("=")
    println("\t2. Change board dimensions (current: " + size + "x" + size + ")\t\t\t\t\t=")
    print("=")
    println("\t3. Define number of pieces to capture (current: " + stonesToWin + ")\t\t\t=")
    print("=")
    println("\t4. Define maximum time for each move (current: " + timeLimit / 1000 + " seconds)\t=") //de forma a apresentar o valor em segundos
    print("=")
    println("\t5. Define difficulty level (current: " + difficultyLevel + ")\t\t\t\t\t\t=")
    print("=")
    println("\t6. Define the player color (current: " + playerColor.toString + ")\t\t\t\t\t=")
    print("=")
    println("\t7. Exit\t\t\t\t\t\t\t\t\t\t\t\t\t\t=")
    println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =")
    print("choose one option: ")
    val choice = readLine().trim
    if (choice.matches("[1-7]")) {
      choice.toInt
    } else {
      println("Invalid input! Please enter one number between 1 and 7")
      startMenu(size, stonesToWin, timeLimit, difficultyLevel, playerColor)
    }
  }

  //metodo que aceita uma string e permite leitura de um valor do user:
  // - para Int
  def promptNum(str: String):Int = {
    println(str)
    val input = readLine().trim
    input.toInt
  }

  // - para String
  def promptStr(str: String):String = {
    println(str)
    val input = readLine().trim.toLowerCase() //para não ser sensivel a letras maisculas ou minusculas (pois as respostas serao analisadas com letras minusculas)
    input
  }

  def printSuccess(): Unit = {
    println("Success!")
  }

  def printError(cause: String): Unit = {
    println("Error..")
    println(cause)
  }

  def printInfo(str: String):Unit = {
    println(str)
  }


  //T4
  private def auxBoard(lst: List[Stone]): String = lst match {
    case Nil => " "
    case _ :: tail => "|   " + auxBoard(tail)
  }

  @tailrec
  def printBoard(board: Board): Unit = board match {
    case Nil =>
    case head :: tail =>
      println(head.map {
        case Stone.Empty => "."
        case Stone.Black => "B"
        case Stone.White => "W"
      }.mkString(" - "))
      if (!tail.equals(Nil)) println(auxBoard(head))
      printBoard(tail)
  }
  private val file = "lastMyRandom"

  def writeMyRandomStateToFile(r: MyRandom): Unit = {
    val pw = new PrintWriter(new File(file))
    pw.write(r.seed.toString)
    pw.close()
  }

  def readMyRandomStateToFile(): Long = {
    val rw = Source.fromFile(file)
    val seed = rw.getLines().next().toLong
    rw.close()
    seed
  }



  def createNewMyRandom(r: MyRandom) :MyRandom = r.nextInt(Int.MaxValue)._2 //com Int.MaxValue garante a diversidade na seed


}
