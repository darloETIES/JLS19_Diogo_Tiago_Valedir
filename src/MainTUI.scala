import AtariGo.{Board, Coord2D, createNewBoard, createNewListOpenCoords, play, playRandomly, randomMove, startTime, timeUp, undo}
import Stone.Stone

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.sys.exit

object MainTUI extends App {
  val pattern: String = "[0-8](,)[0-8]" //formato da coordenada para input por parte do utilizador (formatado em regex)

  val initialR = MyRandom(AtariGOUtils.readMyRandomStateToFile())
  val defaultSize = 5
  val defaultStonesToWin = 1
  val defaultTimeLimit = 20000 //20 segundos em milisegundos
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
          gameLoop(gameState, history, r)
        case 2 =>
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

        case 3 =>
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
          val newTimeLimit = AtariGOUtils.promptNum("Enter the time limit for each move (between 10 and 30 seconds):")
          if(newTimeLimit >= 10 && newTimeLimit <= 30) {
            AtariGOUtils.printSuccess()
            val newGameState = gameState.copy(timeLimit = newTimeLimit * 1000) //multiplica por 1000 para ficar em milisegundos
            startGame(newGameState, history, r)
          }
          else{
            AtariGOUtils.printError("Time limit should be between 10 and 30 seconds!")
            startGame(gameState, history, r)
          }

        case 5 =>
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
          exit()
     }
  }

  @tailrec
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
        val input = AtariGOUtils.promptStr("Enter one option in the console: \n - Your move (row, col) \n - Restart \n - Undo \n - Exit")

        // calcula se o tempo foi excedido
        val expired = timeUp(startTimer, gameState.timeLimit)

        //gerar um novo MyRandom
        val newR = AtariGOUtils.createNewMyRandom(r)
        //qualquer que seja a recursao onde teremos que passar um MyRandom, sera passado newR, pois assim, em qualquer circunstancia, sera sempre aleatoria a proxima jogada

        //desta forma garantimos que da próxima vez que a aplicacao for iniciada (ou seja, desligar e ligar o jogo)
        //o jogo comecara sempre aleatorio (pois estaremos a escrever a seed no ficheiro que depois sera lido, visto no inicio do programa)
        AtariGOUtils.writeMyRandomStateToFile(newR)

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
              //caso o jogador esgote o seu tempo de jogo
              if(expired){
                AtariGOUtils.printInfo("Time's up! You took too long...")
                //Passa a vez ao bot
                val newGameState = gameState.copy(currentPlayer = if (gameState.playerColor == Stone.Black) Stone.White else Stone.Black)
                //avanca com o novo estado e incrementa o historico
                gameLoop(newGameState, gameState :: hist, newR)
              }
              else{
                //verificamos se input segue o mesmo padrao regex que foi definido em pattern
                if (input.matches(pattern)) {
                  //converter o input para coordenadas
                  val coord = input.split(",").map(_.toInt)
                  val coord2D: Coord2D = (coord(0), coord(1))


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
  }
}
