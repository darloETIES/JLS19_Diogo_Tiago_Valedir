import AtariGo.{Board, Coord2D, createNewBoard, createNewListOpenCoords, play, playRandomly, randomMove, startTime, timeUp, undo}
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

  val initialGameState = new GameState(initialBoard, initialLstOpenCoords, 0 , 0, defaultSize, defaultStonesToWin, defaultTimeLimit, defaultDifficulty, defaultPlayerColor, defaultPlayerColor)
  val initialHistory = List() //ainda nao ocorreram jogadas, logo nao havera historico


  //para comecar o jogo (main)
  startGame(initialGameState, initialHistory, initialR)


  @tailrec
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
          gameLoop(gameState, history, r)
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
/* !!!!!!!!!!!!!!!!!
  @tailrec
  def userInput(): Unit = {
    //print("Enter your move (row,col), restart, undo or exit: ") !!!!!!!!!!!!!!
    //val input = readLine().trim
    val input = AtariGOUtils.promptStr("Enter your move (row, col), menu, undo or exit: ")
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
!!!!!!!!!!!!!!!!! */

  def gameLoop(gameState: GameState, hist:List[GameState], r: MyRandom): Unit = {



    //fazer pattern matching do gameState.currentPlayer
    gameState.currentPlayer match {

      //caso gameState.currentPlayer == gameState.playerColor, entao sera o jogador
      case player if player == gameState.playerColor =>

        //mostrar o tabuleiro
        AtariGOUtils.printBoard(gameState.board)

        //inicia o temporizador de jogada antes do input pelo utilizador
        val startTimer = startTime()

        //ler input do utilizador
        val input = AtariGOUtils.promptStr("Enter in the console: \n - Your move (row, col) \n - Restart \n - Undo \n - Exit")

        //gerar um novo MyRandom
        val newR = AtariGOUtils.createNewMyRandom(r)
        //qualquer que seja a recursao onde teremos que passar um MyRandom, sera passado newR, pois assim, em qualquer circunstancia, sera sempre aleatoria a proxima jogada

        //caso o jogador esgote o seu tempo de jogo
        if (timeUp(startTimer, gameState.timeLimit)) {
          AtariGOUtils.printInfo("Time's up! You took too long...")
          //Passa a vez ao bot
          val newGameState = gameState.copy(currentPlayer = if (gameState.playerColor == Stone.Black) Stone.White else Stone.Black)
          //avanca com o novo estado e incrementa o historico
          gameLoop(newGameState, gameState :: hist, newR)
        } else {

          //fazer pattern matching do input
          input match {
            //"exit"
            case "exit" =>
              //sair do programa
              exit()

            //"restart"
            case "restart" =>
              //consideramos os valores por default ja definidos
              //os valores das opcoes do menu mantem
              val newGameState = gameState.copy(createNewBoard(gameState.size), createNewListOpenCoords(gameState.size), 0, 0)
              startGame(newGameState, initialHistory, newR)

            //"undo"
            case "undo" =>
              //devolve o valor obtido com undo
              val newUndo = undo(hist)
              //com o valor obtido, verificar (pattern matching):
              newUndo match {
                //no caso de Some((gameState, hist))
                case Some((oldGameState, oldHistory)) =>
                  // atualiza o estado atual do jogo (criar um novo estado, mas na verdade estamos a buscar o anterior)
                  gameLoop(oldGameState, oldHistory, newR)
                //no caso de None
                case None =>
                  // nao atualiza (ou seja, mantem o estado), mostrando uma msg de erro a informar que nao existe historico (pois nao existem jogadas, caso seja a primeira)
                  AtariGOUtils.printError("No history!")
                  gameLoop(gameState, hist, newR)
              }

            // "row,col"
            //verificamos se nao vai ser nenhuma das opcoes acima
            case _ =>
              //verificamos se input segue o mesmo padrao regex que foi definido em pattern
              if (input.matches(pattern)) {
                //converter o input para coordenadas
                val coord = input.split(",").map(_.toInt)
                val coord2D: Coord2D = (coord(0), coord(1))

                //verifica a cor do jogador e atribui a contraria ao "bot"
                val computerPlayerColor = if (gameState.playerColor == Stone.Black) Stone.White else Stone.Black

                //vez do jogador humano
                //FALTA VERIFICACOES DE LIBERDADES, CAPTURAS,..
                play(gameState.board, gameState.currentPlayer, coord2D, gameState.lstOpenCoords) match {
                  case (Some(newBoard), newLstOpenCoords) =>
                    val nextPlayer = if (gameState.currentPlayer == Stone.Black) Stone.White else Stone.Black
                    val newGameState = gameState.copy(
                      board = newBoard,
                      lstOpenCoords = newLstOpenCoords,
                      currentPlayer = nextPlayer
                    )
                    gameLoop(newGameState, gameState :: hist, newR)

                  case _ =>
                    AtariGOUtils.printError("Invalid move!")
                    gameLoop(gameState, hist, newR)
                }
              } else {
                AtariGOUtils.printError("Invalid Input!")
                gameLoop(gameState, hist, newR)
              }
          }
        }

      //caso contrario, entao sera o "bot"
      case _ =>
        //vez do "bot"
        val (newBoard, newRand, newLstOpenCoords) = playRandomly(
          gameState.board,
          r,
          gameState.currentPlayer,
          gameState.lstOpenCoords,
          (lst, rand) => randomMove(lst, rand)
        )
        val nextPlayer = if (gameState.currentPlayer == Stone.Black) Stone.White else Stone.Black
        val newGameState = gameState.copy(
          board = newBoard,
          lstOpenCoords = newLstOpenCoords,
          currentPlayer = nextPlayer
        )
        // historico nao muda porque nao sera o jogador a jogar
        gameLoop(newGameState, hist, newRand)
    }







    /*player match {
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
    } */
  }


}
