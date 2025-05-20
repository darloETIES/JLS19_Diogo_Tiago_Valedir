import AtariGo.{Board, Coord2D, createNewBoard, createNewListOpenCoords, startTime}
import Stone.Stone

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.sys.exit

object MainTUI extends App { //POR ATUALIZAR

/*val atari = new AtariGo
  var size = 5 !!!!!!
  private var coord2D: atari.Coord2D = (0, 0)
  var lstOpenCoords: List[(Int, Int)] = atari.createNewListOpenCoords(size)
  var board: atari.Board = atari.createNewBoard(size)
  var player: Stone.Value = Stone.Black
  private var pattern: String = "[0-9](,)[0-9]"
  val r: MyRandom = MyRandom(AtariGOUtils.readMyRandomStateToFile())
  private var stonesToWin = 1
  private var timeLimit = 20000
  private var difficultyLevel = 1
  !!!!!!!!!!!!!! */

  //TODOS (ou quase todos) OS VALORES ACIMA SERAO DE FULLSTATE

  val pattern: String = "[0-8](,)[0-8]" //formato da coordenada para input por parte do utilizador (formatado em regex)

  val initialR = MyRandom(AtariGOUtils.readMyRandomStateToFile())
  val defaultSize = 5
  val defaultStonesToWin = 1
  val defaultTimeLimit = 20
  val defaultDifficulty = 1
  val defaultPlayerColor:Stone = Stone.Black
  val initialBoard = AtariGo.createNewBoard(defaultSize)
  val initialLstOpenCoords = AtariGo.createNewListOpenCoords(defaultSize)

  val initialGameState = new GameState(initialBoard, initialLstOpenCoords, 0 , 0, defaultSize, defaultStonesToWin, defaultTimeLimit, defaultDifficulty, defaultPlayerColor, initialR)
  val initialHistory = List() //ainda nao ocorreram jogadas, logo nao havera historico


  //para comecar o jogo (main)
  startGame(initialGameState, initialHistory, initialR)



  def startGame(gameState: GameState, history: List[GameState], r: MyRandom): Unit = {
    val input = AtariGOUtils.startMenu(gameState.size, gameState.stonesToWin, gameState.timeLimit, gameState.difficultyLevel, gameState.playerColor)
      input match {
        case 1 =>
          //println() !!!!!!
          //AtariGOUtils.printBoard(board)
          //println("Player capture:   "+atari.playerCapture) !!!!!!
          //println("Computer capture: "+atari.computerCapture) !!!!!!
          //atari.saveGameState(GameState(board, lstOpenCoords, atari.playerCapture, atari.computerCapture))
          //AS LINHAS DE CODIGO ACIMA PRECISAM DE ESTAR NO GAMELOOP
          //gameLoop(gameState, )
        case 2 =>
          /*size = setBoardDimension() !!!!!!!!!!!!!!
          pattern = size match {
            case s if s <= 10 => "[0-9](,)[0-9]"
            case s if s > 10 => "[0-9]{1,2}(,)[0-9]{1,2}"
          !!!!!!!!!!!!!! */

          val newSize = AtariGOUtils.promptNum("Enter board dimensions: (between 5 and 9)")
          if(newSize >= 5 && newSize <= 9){
            AtariGOUtils.printSuccess()
            val newBoard = createNewBoard(newSize)
            val newLstOpenCoords = createNewListOpenCoords(newSize)
            val newGameState = gameState.copy(board = newBoard, lstOpenCoords = newLstOpenCoords, size = newSize)
            startGame(newGameState, history, r)
          }
          else{
            AtariGOUtils.printError("Board size should be between 5 and 9!")
            startGame(gameState, history, r)
          }



          /*board = atari.createNewBoard(size) !!!!!!
          lstOpenCoords = atari.createNewListOpenCoords(size) !!!!!!!
          println("board dimensions changed successfully...")  !!!!!!! */
        case 3 =>
          /*stonesToWin = setStonesToWin() !!!!!!!!!!
          println("number of pieces to capture changed successfully...") !!!!!!!!!
          !!!!!!!!!!!!!! */

          val newStonesToWin = AtariGOUtils.promptNum("Enter number of stones to win (between 1 and 10):")
          if(newStonesToWin >= 1 && newStonesToWin <= 10){
            AtariGOUtils.printSuccess()
            val newGameState = gameState.copy(stonesToWin = newStonesToWin)
            startGame(newGameState, history, r)
          }
          else{
            AtariGOUtils.printError("The number should be between 1 and 10!")
            startGame(gameState, history, r)
          }

        case 4 =>
          //timeLimit = setTimeLimit()
          //println("Time limit for each move changed successfully...")
          //startGame(makeOptionMenu())

          val newTimeLimit = AtariGOUtils.promptNum("Enter the time limit for each move (between 10 and 30 seconds):")
          if(newTimeLimit >= 10 && newTimeLimit <= 30) {
            AtariGOUtils.printSuccess()
            val newGameState = gameState.copy(timeLimit = newTimeLimit)
            startGame(newGameState, history, r)
          }
          else{
            AtariGOUtils.printError("Time limit should be between 10 and 30 seconds!")
            startGame(gameState, history, r)
          }

        case 5 =>
          /* difficultyLevel = setDifficultyLevel() !!!!!!!!!
          println("Difficulty level move changed successfully...") !!!!!!!!!
          startGame(makeOptionMenu())
          !!!!!!!!! */

          val newDiffLevel = AtariGOUtils.promptNum("Enter the difficulty level (between 1 and 3):")
          if(newDiffLevel >= 1 && newDiffLevel <= 3) {
            AtariGOUtils.printSuccess()
            val newGameState = gameState.copy(difficultyLevel = newDiffLevel)
            startGame(newGameState, history, r)
          }
          else{
            AtariGOUtils.printError("Difficulty level should be between 1 and 3!")
            startGame(gameState, history, r)
          }


        case 6 =>
          val newPlayerColorStr = AtariGOUtils.promptStr("Enter your stone color (black or white):")
          if(!newPlayerColorStr.equals("black") && !newPlayerColorStr.equals("white")) {
            AtariGOUtils.printError("Player color should be black or white!")
            startGame(gameState, history, r)
          }
          else{
            AtariGOUtils.printSuccess()
            if(newPlayerColorStr.equals("black")){
              val newGameState = gameState.copy(playerColor = Stone.Black)
              startGame(newGameState, history, r)
            }
            else if(newPlayerColorStr.equals("white")){
              val newGameState = gameState.copy(playerColor = Stone.White)
              startGame(newGameState, history, r)
            }
          }

        case 7 =>
          //println("Exit...") !!!!!!!
          exit()
     }
  }

  /* !!!!!!!!!!!!!!!!!!!!!!
  @tailrec
  private def setBoardDimension(): Int = {
    print("Enter board dimensions: ")
    val input = readLine().trim
    if (input.matches("[0-9]{1,2}")) {
      input.toInt
    } else {
      println("Invalid input! Please Try again...")
      setBoardDimension()
    }
  }

  @tailrec
  private def setStonesToWin(): Int = {
    print("Enter number of stones to win: ")
    val input = readLine().trim
    if (input.matches("[0-9]")) {
      input.toInt
    } else {
      println("Invalid input! Please Try again...")
      setStonesToWin()
    }
  }

  @tailrec
  private def setTimeLimit(): Int = {
    print("Enter maximum time for each move: ")
    val input = readLine().trim
    if (input.matches("[0-9]{1,2}")) {
      input.toInt * 1000
    } else {
      println("Invalid input! Please Try again...")
      setTimeLimit()
    }
  }

  @tailrec
  private def setDifficultyLevel(): Int = {
    print("Enter difficulty level (1-easy, 2-medium, 3-difficult): ")
    val input = readLine().trim
    if (input.matches("[1-3]")) {
      input.toInt
    } else {
      println("Invalid input! Please Try again...")
      setDifficultyLevel()
    }
  }

  !!!!!!!!!!!!!!!!!!!!!! */
/*
  @tailrec
  def userInput(): Unit = {
    //print("Enter your move (row,col), restart, undo or exit: ") !!!!!!!!!!!!!!
    //val input = readLine().trim
    val input = AtariGOUtils.promptNum("Enter your move (row, col), menu, undo or exit: ")
    input match {
      case "exit" =>
        //println("Exit...") !!!!!!!!!!!
        exit()
      case "menu" =>
        //lstOpenCoords = atari.createNewListOpenCoords(size) !!!!!!!!!
        //board = atari.createNewBoard(size) !!!!!!!!
        //startGame(makeOptionMenu())
        val newLstOpenCoords = createNewListOpenCoords()
      case "undo" =>
        val undo = atari.undo()
        if (undo.isEmpty) {
          println("No move to undo...")
          gameLoop(board, player, lstOpenCoords)
        } else {
          board = undo.get.board
          lstOpenCoords = undo.get.lstOpenCoords
          atari.playerCapture = undo.get.playerCapture
          atari.computerCapture = undo.get.computerCapture
          AtariGOUtils.printBoard(board)
          println("Player capture:   "+atari.playerCapture)
          println("Computer capture: "+atari.computerCapture)
          gameLoop(board, player, lstOpenCoords)
        }
      case _ =>
        if (input.matches(pattern)) {
          val cord = input.split(",").map(_.toInt)
          coord2D = (cord(0), cord(1))
        } else {
          println("Invalid input! Please Try again...")
          userInput()
        }
    }
  }

  @tailrec
  def gameLoop(gameState: GameState): Unit = {
    player match {
    case Stone.Black =>
      println()
      val time = atari.startTime()
      userInput()
      if (atari.timeUp(time, timeLimit)) {
        println("Time up...")
        val playerWhite = Stone.White
        gameLoop(board, playerWhite, listCoord2D)
      } else {
        val update = atari.play(board, player, coord2D, listCoord2D)
        if (update._1.isEmpty) {
          println("Invalid move!!! Try again...")
          gameLoop(board, player, listCoord2D)
        } else {
          AtariGOUtils.printBoard(update._1.get)
          println("Player capture:   "+atari.playerCapture)
          println("Computer capture: "+atari.computerCapture)
          val playerWhite = Stone.White
          gameLoop(update._1.get, playerWhite, update._2)
        }
      }
    case Stone.White =>
      println()
      val update = atari.playRandomly(board, r, player, listCoord2D, (lst, rand) => atari.randomMove(lst, rand))
      println("Computer Move: "+ atari.computerMove)
      AtariGOUtils.printBoard(update._1)
      println("Player capture:   "+atari.playerCapture)
      println("Computer capture: "+atari.computerCapture)
      AtariGOUtils.writeMyRandomStateToFile(update._2)
      atari.saveGameState(GameState(update._1, update._3, atari.playerCapture, atari.computerCapture))
      val playerBlack = Stone.Black
      gameLoop(update._1, playerBlack, update._3)
  }
  }

*/
}
