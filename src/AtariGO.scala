import AtariGOUtils.{printBoard, readMyRandomStateToFile, writeMyRandomStateToFile}
import AtariGo.Board
import Stone._
import scala.util.Random

object AtariGo extends App {

  type Board = List[List[Stone]]
  type Coord2D = (Int, Int) //(row, column)

  //cria um novo tabuleiro de tamanho size X size
  def createNewBoard(size:Int):Board = {
    List.fill(size,size)(Stone.Empty)
  }

  //cria a lista de coords livres a partir do tamanho (size)
  def createNewListOpenCoords(size: Int): List[(Int, Int)] = {
    def generateListOpenCoords(size: Int, row: Int, col: Int, lst: List[Coord2D]): List[Coord2D] = {
      if (row == size) lst //vemos linha a linha, logo quando passa da ultima, isto é, quando row > size, devolve a lista
      else if (col == size) generateListOpenCoords(size, row + 1, 0, lst) //quando acaba a ultima coluna, passa para a próxima linha
      else generateListOpenCoords(size, row, col + 1, (row, col) :: lst)
    }

    val newLstOpenCoords = generateListOpenCoords(size, 0, 0, Nil)
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
    if(isValid(board,player,coord, lstOpenCoords)){

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

  //metodo para encontrar os vizinhos de uma coordenada
  def findNeighbors(coord:Coord2D):List[Option[Coord2D]] ={

    //val nbr = new java.util.HashMap[Coord2D]

    val up:Coord2D = (coord._1 + 1,coord._2)
    val down:Coord2D=(coord._1 - 1, coord._2)
    val left:Coord2D=(coord._1,coord._2-1)
    val right:Coord2D=(coord._1,coord._2+1)

    //[up,down,left,right]
    val aux_nbrs:List[Coord2D] = List( up , down ,left ,right)
    //aux_nbrs 


    //verificar se os vizinhos estao numa posicao que existe
    def onBoard(c:Coord2D):Boolean={
      c._1>=0 && c._1<board.length && c._2>=0 && c._2<board.length
    }

    aux_nbrs.map{
      c => if (onBoard(c)) Some(c)
      else None
    }


  }

  //metodo auxiliar para verificar se uma posição é livre e tem liberdade (logo se e possivel jogar la)
  def isValid(board:Board, player:Stone,coord:Coord2D,lstOpenCoords:List[Coord2D]):Boolean ={
    //verificar primeiro se a posicao esta livre para verificar se esta apta a ser preenchida
    if(lstOpenCoords.contains(coord)){ // verificar se tem liberdade para poder ser ou nao preenchida
      val lstNeighbors:List[Coord2D] = findNeighbors(coord)
      //caso 1 - tem pelo menos um vizinho livre e por isso tem liberdade
      //verificar se os vizinhos sao posicoes livres de forma a ver existe pelo menos um vizinho livre, nao e preciso verificar se a posicao e valida pois posicoes invalidas nao existem no lstopencoords
      if(lstOpenCoords.contains(lstNeighbors(0)) || lstOpenCoords.contains(lstNeighbors(1)) || lstOpenCoords.contains(lstNeighbors(2)) || lstOpenCoords.contains(lstNeighbors(3))){
        true
      }
      else{ //se n houver vizinhos livres averiguar se sao todos do adversario pois se forem so e possivel jogar ai se for para capturar
        if(true/*verificar se todas as pecas vizinhas sao todas adversarias*/){
          //se forem todas adversarias ver se jogando ai a peça se captura
          false
        }else{
          //se nao forem todas adversarias entao nao esta encurralado de todo e pode jogar
          false
        }

      }
    }
    else false //se a posicao nao estiver livre entao e impossivel jogar la
  }
  //T3
  def playRandomly(board: Board, r: MyRandom, player: Stone, lstOpenCoords: List[Coord2D], f: (List[Coord2D], MyRandom) => (Coord2D, MyRandom)): (Board, MyRandom, List[Coord2D]) = {
    val res = f(lstOpenCoords, r)
    val up = play(board, player, res._1, lstOpenCoords)
    (up._1.get, res._2,up._2)
  }


  //TESTE DOS MÉTODOS
  val r = MyRandom(readMyRandomStateToFile())

  val board = createNewBoard(5)
  val lstOpenCoords = createNewListOpenCoords(5)

  val player = Stone.White
  val coord = (1,3)
  println(lstOpenCoords)

  //Jogador
  val (boardAfterPlayerPlayed, lstOpenCoordAfterPlayerPlayed) = play(board, player, coord, lstOpenCoords)
  println("Player's Turn")
  printBoard(boardAfterPlayerPlayed.get)

  //CPU
  println("\nCPU's Turn")
  val (cpuBoard, r1, lstOpenCoordAfterCPUPlayed) = playRandomly(boardAfterPlayerPlayed.get, r, Stone.Black, lstOpenCoordAfterPlayerPlayed, (lst, rand) => randomMove(lst, rand))
  printBoard(cpuBoard)

  //Guardar o novo MyRandom no ficheiro, para que seja sempre aleatorio o inicio de qualquer jogo
  writeMyRandomStateToFile(r1)
}