import Stone.Stone

import scala.annotation.tailrec

object AtariGo {

  type Board = List[List[Stone]]
  type Coord2D = (Int, Int) //(row, column)
  //private var gameHistory: List[GameState] = List() !!!!!!!!!!!
  //var computerMove: Coord2D = (0, 0) !!!!!!!!!!!
  //var playerCapture = 0 !!!!!!!!!!!
  //var computerCapture = 0 !!!!!!!!!!!

  //cria um novo tabuleiro de tamanho size X size
  def createNewBoard(size:Int):Board = {
    List.fill(size,size)(Stone.Empty)
  }

  //cria a lista de coords livres a partir do tamanho (size)
  def createNewListOpenCoords(size: Int): List[(Int, Int)] = {
    @tailrec
    def generateListOpenCoords(row: Int, col: Int, lst: List[Coord2D]): List[Coord2D] = {
      if (row == size) lst //vemos linha a linha, logo quando passa da ultima, isto é, quando row > size, devolve a lista
      else if (col == size) generateListOpenCoords(row + 1, 0, lst) //quando acaba a ultima coluna, passa para a próxima linha
      else generateListOpenCoords(row, col + 1, (row, col) :: lst)
    }

    val newLstOpenCoords = generateListOpenCoords(0, 0, Nil)
    newLstOpenCoords
  }

  //T1
  def randomMove(lstOpenCoords:List[Coord2D], rand:MyRandom):(Coord2D, MyRandom) ={
    val (ind:Int, newRandom:MyRandom) = rand.nextInt(lstOpenCoords.length) //gerar um indice aleatorio dentro do vetor de coordenadas disponiveis e um novo estado do MyRandom
    val randCoord = lstOpenCoords(ind)
    val result = (randCoord,newRandom)
    result
  }

  //T2
  def play(board:Board, player: Stone, coord:Coord2D, lstOpenCoords:List[Coord2D]):(Option[Board], List[Coord2D]) = {
    //iremos validar se é possível jogar na coordenada fornecida (coord)
    if(lstOpenCoords.contains(coord)){

      //List.updated(pos a trocar, elem) -> No nosso caso acedemos à linha a trocar (coord._1) e depois percorremos as colunas (coord._2).
      //Após isso é substituido o elemento anterior por player
      val updatedBoard = board.updated(coord._1, board(coord._1).updated(coord._2, player))

      //criar uma nova lista de coordenadas livres (excluir a coordenada que foi jogada a peça)
      val newLstOpenCoords = lstOpenCoords filterNot (_ == coord)

      //devolver ambos
      (Some(updatedBoard), newLstOpenCoords)
    }
    else{
      (None, lstOpenCoords)
    }

  }

  //T3
  def playRandomly(board: Board, r: MyRandom, player: Stone, lstOpenCoords: List[Coord2D], f: (List[Coord2D], MyRandom) => (Coord2D, MyRandom)): (Board, MyRandom, List[Coord2D]) = {
    val res = f(lstOpenCoords, r)
    val up = play(board, player, res._1, lstOpenCoords) //REVER, pois tera que ter também a verificacao
    (up._1.get, res._2,up._2)
  }

  //metodo para devolver o GameState anterior e a lista de GameState alterada (lista que não tem o GameState atual, pois voltamos para trás)
  def undo(history: List[GameState]): Option[(GameState,List[GameState])] = {
    history match {
      case Nil => None //o historico está vazio, logo não poderá devolver nada para fazer undo
      case List(x) => Some((x, Nil)) //o historico tem 1 elemento, logo retira-o, devolvendo como o GameState a ser jogado e esvazia o historico
      case x::xs => Some((x, xs)) //o historico tem diversos elementos, logo retira-o da mesma forma como o anterior, e devolve a lista de GamesStates restantes
    }
    /*if (gameHistory.size == 1) { !!!!!!!!!!!!
      None
    } else {
      gameHistory = gameHistory.tail
      val state = gameHistory.head
      Some(state)
    } !!!!!!!!!!! */


  }

  def saveGameState(gameState: GameState, history: List[GameState]): List[GameState] = {
    //gameHistory = gameState :: gameHistory !!!!!!!
    gameState :: history
  }

  def startTime(): Long = {
    System.currentTimeMillis()
  }

  def timeUp(startTime: Long, timeLimit: Int): Boolean = {
    (System.currentTimeMillis() / 1000) - startTime > timeLimit
  }

}


