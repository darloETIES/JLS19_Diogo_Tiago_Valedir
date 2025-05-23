package model

import model.GameState.{Board, Coord2D}
import model.Stone.Stone

import scala.annotation.tailrec

case class GameState(
                      board: Board,
                      lstOpenCoords: List[Coord2D],
                      playerCapture: Int,
                      computerCapture: Int,
                      size: Int,
                      stonesToWin: Int,
                      timeLimit: Int,
                      difficultyLevel: Int,
                      playerColor: Stone, //identifica a cor do jogador humano
                      currentPlayer: Stone, //identifica o jogador atual (humano ou "bot")
                      history: List[GameState]
                    )
{
  def createNewBoard(): Board = GameState.createNewBoard(size)
  def createNewListOpenCoords(): List[(Int, Int)] = GameState.createNewListOpenCoords(size)
  def randomMove(rand:MyRandom):(Coord2D, MyRandom) = GameState.randomMove(lstOpenCoords, rand)
  def play(coord2D:Coord2D):(Option[Board], List[Coord2D]) = GameState.play(board, currentPlayer, coord2D, lstOpenCoords)
  def playRandomly(r: MyRandom, f: (List[Coord2D], MyRandom) => (Coord2D, MyRandom)): (Board, MyRandom, List[Coord2D]) = GameState.playRandomly(board, r, currentPlayer, lstOpenCoords, f)
  def getCaptures(board: Board ,coord2D: Coord2D): Int = GameState.getCaptures(currentPlayer, board, coord2D)
  def captureGroupStones(board: Board, coord: Coord2D): (Board, Int) = GameState.captureGroupStones(board, currentPlayer, coord)
  def checkWinner(capture: Int):Boolean = GameState.checkWinner(capture, stonesToWin)
  def undo(): Option[(GameState,List[GameState])] = GameState.undo(history)
  def startTime():Long = GameState.startTime()
  def timeUp(startTime: Long):Boolean = GameState.timeUp(startTime, timeLimit)

}

object GameState {

  type Board = List[List[Stone]]
  type Coord2D = (Int, Int) //(row, column)

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

  //devolve a cor de uma posicao
  def findStone(board:Board,coord:Coord2D):Stone ={
    board(coord._1)(coord._2)
  }

  //T1
  def randomMove(lstOpenCoords:List[Coord2D], rand:MyRandom):(Coord2D, MyRandom) ={
    if (lstOpenCoords.isEmpty)
      throw new IllegalArgumentException("A lista com coordenadas livres está vazia!")
    val (ind:Int, newRandom:MyRandom) = rand.nextInt(lstOpenCoords.length) //gerar um indice aleatorio dentro do vetor de coordenadas disponiveis e um novo estado do MyRandom
    val randCoord = lstOpenCoords(ind)
    val result = (randCoord,newRandom)
    result
  }

  //T2
  def play(board:Board, player: Stone, coord:Coord2D, lstOpenCoords:List[Coord2D]):(Option[Board], List[Coord2D]) = {
    //iremos validar se é possível jogar na coordenada fornecida (coord)
    val neighbors = getNeighbors(board, coord)
    if(isValidMove(board, coord, player, neighbors) && lstOpenCoords.contains(coord)){

      //List.updated(pos a trocar, elem) -> No nosso caso acedemos à linha a trocar (coord._1) e depois percorremos as colunas (coord._2).
      //Após isso é substituido o elemento anterior por player
      val updatedBoard = board.updated(coord._1, board(coord._1).updated(coord._2, player))

      //corre o metodo da T5 onde obtem um tabuleiro apos as capturas e o nr de capturas feitas
      //este board sera sempre usado, apenas tera diferenca caso haja capturas
      val (finalBoard, nCaptures) = captureGroupStones(updatedBoard, player, coord)

      //criar uma nova lista de coordenadas livres (excluir a coordenada que foi jogada a peça)
      val newLstOpenCoords = lstOpenCoords.filterNot(_ == coord) ++ { //e também ira conter:
        if(nCaptures > 0){
          //inclui coords onde pecas foram capturadas
          getNeighbors(board, coord).filter(n => finalBoard(n._1)(n._2) == Stone.Empty && !lstOpenCoords.contains(n))
        }
        else Nil //caso nao haja capturas, ignora esta parte
      }

      //devolver ambos
      (Some(finalBoard), newLstOpenCoords)
    }
    else{
      (None, lstOpenCoords)
    }

  }

  //T3
  def playRandomly(board: Board, r: MyRandom, player: Stone, lstOpenCoords: List[Coord2D], f: (List[Coord2D], MyRandom) => (Coord2D, MyRandom)): (Board, MyRandom, List[Coord2D]) = {
    @tailrec
    def tryPlay(coords: List[Coord2D], r: MyRandom): (Board, MyRandom, List[Coord2D]) = {
      if (coords.isEmpty)
        throw new IllegalStateException("Nenhuma jogada válida encontrada para o bot.")

      val (coord, newRand) = f(coords, r)
      play(board, player, coord, lstOpenCoords) match {
        case (Some(newBoard), newLstOpenCoords) =>
          (newBoard, newRand, newLstOpenCoords)
        case (None, _) =>
          // jogada inválida – tente novamente com o restante da lista (excluindo a coordenada testada)
          tryPlay(coords.filterNot(_ == coord), newRand)
      }
    }

    tryPlay(lstOpenCoords, r)
  }



  def getNeighbors(b: Board, pos: Coord2D): List[Coord2D] = {
    val isValidPosition = (pos: Coord2D) => pos._1 >= 0 && pos._1 < b.length && pos._2 >= 0 && pos._2 < b.length
    val list = List((pos._1 - 1, pos._2), (pos._1 + 1, pos._2), (pos._1, pos._2 - 1), (pos._1, pos._2 + 1))
    list.filter(x => isValidPosition(x))
  }

  def hasLiberty(board: Board, group: List[Coord2D]): Boolean = {
    group.exists { coord =>
      getNeighbors(board, coord).exists(n => board(n._1)(n._2) == Stone.Empty)
    }
  }

  def findGroup(board: Board, coord: Coord2D, stone: Stone): List[Coord2D] = {

    //pending - lista de coords ainda por visitar
    //visited - lista de coords ja visitadas
    @tailrec
    def helper(pending: List[Coord2D], visited: List[Coord2D]): List[Coord2D] = {
      pending match {
        case Nil => visited //caso de paragem, caso ja tenhamos visitado todas as coords do grupo
        case current :: rest => //caso onde existe casas a visitar (logo, pending tem elementos ainda)
          if (visited.contains(current)) helper(rest, visited) //caso a coord atual ja tenha sido visitada (ou seja se a mesma se encontra em visited)
          else {
            val neighbors = getNeighbors(board, current) //obtem-se os vizinhos de current (coord atual)
            val sameColorNeighbors = neighbors.filter(n => board(n._1)(n._2) == stone) //dos vizinhos obtidos filtra de modo a obter apenas os da sua cor
            //pending ficara com todos os vizinhos da mesma cor que a peca atual
            //visited ficara com a peca atual (current) com a restante lista que la estava
            helper(rest ++ sameColorNeighbors.filterNot(visited.contains), current :: visited)
          }
      }
    }
    //comeca com a coord da peca atual numa lista para ainda analisar (pending) e outra lista nenhuma peca visitada (visited)
    helper(List(coord), Nil)
  }

  def isValidMove(board: Board, coord: Coord2D, player: Stone, neighbors: List[Coord2D]): Boolean = {
    if (board(coord._1)(coord._2) != Stone.Empty) false //se a posicao atual tiver alguma peca, não sera possivel jogar
    else { //caso esteja livre:

      //simulacao de colocar a pedra, de forma a criar um tabuleiro de teste, sem alterar o original
      //sera util para testar determinado caso que possa acontecer
      val testBoard = board.updated(coord._1, board(coord._1).updated(coord._2, player))

      //encontra o grupo que esta nova peca ira pertencer (ou seja, todas as pedras conectadas diretamente, um grupo dessa peca)
      val group = findGroup(testBoard, coord, player)

      //se este novo grupo tem pelo menos uma liberdade
      if (hasLiberty(testBoard, group)) true //a jogada sera valida
      else { //caso contrario
        val opponent = if(player == Stone.Black) Stone.White else Stone.Black //a peca do inimigo

        //em cada vizinho, verifica:
        neighbors.exists { n =>
          board(n._1)(n._2) == opponent && /*se é oponente*/ {
            //se o grupo de oponentes não tem liberdades
            val oppGroup = findGroup(board, n, opponent)
            !hasLiberty(board, oppGroup)
          }
        }
      }
    }
  }

  //T5
  def captureGroupStones(board: Board, player: Stone, coord: Coord2D): (Board, Int) = {
    val opponent = if (player == Stone.Black) Stone.White else Stone.Black //peca oponente
    val neighbors = getNeighbors(board, coord) //vizinhos em relacao a jogada feita

    val capturedGroups: List[List[Coord2D]] = neighbors
      .filter(n => board(n._1)(n._2) == opponent) //filtra os vizinhos que sao oponentes
      .map(n => findGroup(board, n, opponent)) //encontra os grupos dessas mesmas pecas (as anteriormente filtradas)
      .filter(group => !hasLiberty(board, group)) //mantem apenas os grupos de oponentes que não tenham liberdades
    //no fim teremos um grupo de oponentes sem liberdades que são vizinhos da posicao atual, logo, um grupo capturado

    //convertemos esse mesmo grupo apenas em uma lista de coordenadas simples (pois capturedGroups é um List[List[Coord2D]] )
    val capturedCoords: List[Coord2D] = capturedGroups.flatten.distinct

    //ira percorrer cada posicao capturada, removendo-a do tabuleiro
    val newBoard = capturedCoords.foldLeft(board) { (accBoard, pos) =>
      accBoard.updated(pos._1, accBoard(pos._1).updated(pos._2, Stone.Empty))
    }

    //devolve um tuplo com o tabuleiro atualizado sem as pecas capturadas e o nr de pecas capturadas
    (newBoard, capturedCoords.length)
  }

  def getCaptures(player:Stone, board: Board, coord2D: Coord2D): Int = {
    captureGroupStones(board, player, coord2D)._2
  }

  //T6
  def checkWinner(capture: Int, target: Int): Boolean = capture >= target

  //metodo para devolver o GameState anterior e a lista de GameState alterada (lista que não tem o GameState atual, pois voltamos para trás)
  def undo(history: List[GameState]): Option[(GameState,List[GameState])] = {
    history match {
      case Nil => None //o historico está vazio, logo não poderá devolver nada para fazer undo
      case List(x) => Some((x, Nil)) //o historico tem 1 elemento, logo retira-o, devolvendo como o GameState a ser jogado e esvazia o historico
      case x::xs => Some((x, xs)) //o historico tem diversos elementos, logo retira-o da mesma forma como o anterior, e devolve a lista de GamesStates restantes
    }
  }

  def startTime(): Long = {
    System.currentTimeMillis()
  }

  def timeUp(startTime: Long, timeLimit: Int): Boolean = {
    val elapsedMillis = System.currentTimeMillis() - startTime
    elapsedMillis > timeLimit
  }
}
