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

    val up:Coord2D = (coord._1 - 1,coord._2) //sinal menos pois comecamos a gerar o board pela linha mais alta (por cima)
    val down:Coord2D=(coord._1 + 1, coord._2)
    val left:Coord2D=(coord._1,coord._2-1)
    val right:Coord2D=(coord._1,coord._2+1)
    val aux_nbrs:List[Coord2D] = List( up , down ,left ,right)

    //verificar se os vizinhos estao numa posicao que existe
    def onBoard(c:Coord2D):Boolean={
      c._1>=0 && c._1<board.length && c._2>=0 && c._2<board.length
    }

    aux_nbrs.map{
      c => if (onBoard(c)) Some(c)
      else None
    }

  }

  //BACKUPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
  /*//metodo auxiliar para verificar se uma posição é livre e tem liberdade (logo se e possivel jogar la)
  def isValid(board:Board, player:Stone,coord:Coord2D,lstOpenCoords:List[Coord2D]):Boolean ={
    //verificar primeiro se a posicao esta livre para verificar se esta apta a ser preenchida
    if(lstOpenCoords.contains(coord)){ // verificar se tem liberdade para poder ser ou nao preenchida
      val lstNeighbors:List[Option[Coord2D]] = findNeighbors(coord)
      //caso 1 - tem pelo menos um vizinho livre e por isso tem liberdade

      //verificar se os vizinhos sao posicoes livres de forma a ver existe pelo menos um vizinho livre, nao e preciso verificar se a posicao e valida pois posicoes invalidas nao existem no lstopencoords
      if(lstOpenCoords.contains(lstNeighbors(0)) || lstOpenCoords.contains(lstNeighbors(1)) || lstOpenCoords.contains(lstNeighbors(2)) || lstOpenCoords.contains(lstNeighbors(3))){
        true
      }
      else{ //caso 2 - se n houver vizinhos livres averiguar se sao todos do adversario pois se forem so e possivel jogar ai se for para capturar
        if(true/*verificar se todas as pecas vizinhas sao todas adversarias*/){
          //caso 2.1 - se forem todas adversarias ver se jogando ai a peça se captura
          false
        }else{//caso 2.2 - se nao forem todas adversarias entao nao esta encurralado pelo adversario e pode jogar
          true
        }
      }
    }
    else false //se a posicao nao estiver livre entao e impossivel jogar la
  }
  * */

  //metodo que verifica a possibilidade de jogar numa dada posicao
  def isValid(board:Board, player:Stone,coord:Coord2D,lstOpenCoords:List[Coord2D]):Boolean ={

    if(lstOpenCoords.contains(coord)){ // se a posicao nao estiver preenchida verificar se tem liberdade

      val lstPossibleNeighbors:List[Option[Coord2D]] = findNeighbors(coord) //vizinhos validos mas com option
      val lstValidNeighbors:List[Coord2D]= lstPossibleNeighbors.flatten //da uma lista com os vizinhos validos, retira os None e extrai os valores das coordenadas
      val auxNeighbors = lstValidNeighbors.filter(x => board(x._1)(x._2).equals(Empty)) //fica com os vizinhos livres

      if(auxNeighbors.nonEmpty){ //se houver pelo menos um vizinho livre existe liberdade
        true
      }
      else{ //se n houver vizinhos livres logo se o grupo esta cercado temos que verificar se
        //VER LIBERDADE DO GRUPO
        val group:List[Coord2D] = findGroup(board, coord, player)

        if(groupHasLiberty(board, group)){ //se o grupo tem liberdade pode se jogar
          true
        }else{false
          /*
          if(/*canCapture*/true){ //se o grupo nao tiver liberdade apenas pode se jogar em caso de captura
            true
          }else{
            false
          }
          */
        }
      }


    }
    else false //se a posicao nao estiver livre entao e impossivel jogar la
  }


  def findGroup(board: Board,coord: Coord2D, player:Stone):List[Coord2D] ={
        // queue - o que esta por visitar/analisar
        // visited - posicoes ja visitadas
        // group - grupo descoberto para ser retornado
        def expand(queue:List[Coord2D], visited:List[Coord2D], group:List[Coord2D]): List[Coord2D] = queue match{
          case Nil => group
          case h::t =>{/*val headColor:Stone = board(h._1)(h._2)*/
            val neighborsToVisit:List[Coord2D] = findNeighbors(h).flatten
            val stoneGroup:List[Coord2D] = (neighborsToVisit.filter(x=> board(x._1)(x._2)==player) ++ group).distinct

            val filteredNeighbors:List[Coord2D] = neighborsToVisit.filter( x => !visited.contains(x) /*&& board(x._1)(x._2)==player*/ )

            expand( (t ++ filteredNeighbors).distinct, (h::visited).distinct, stoneGroup)
          }

        }
      expand(List(coord), List(), List() ) //inicialmente so temos por explorar a propria coordenada e nao temos nenhuma posicao visitada nem ninguem pertencendo ao grupo
  }

  def groupHasLiberty(board:Board, group:List[Coord2D]):Boolean ={
    val groupStone:Stone = board(group.head._1)(group.head._2) //todos os elementos de um grupo deverao ter a mesma cor, por isso vamos buscar a cor de um certo elemento do grupo (neste caso a cabeca)
        def analyseGroup(auxGroup:List[Coord2D], stone:Stone, groupEmptyNeighbors:List[Coord2D]):Boolean= auxGroup match {
          case Nil => {
            if (groupEmptyNeighbors.length > 1) true  //se o grupo tiver mais do que uma posicao livre a sua volta pode se jogar
            else canCapture(board, coord, player) //se o grupo tiver cercado e preciso ver se e possivel capturar de forma a ter liberdade
          }
          case h::t => {
            val headNeighbors: List[Coord2D] = findNeighbors(h).flatten
            val headEmptyNeighbors:List[Coord2D] = headNeighbors.filter(x=> board(x._1)(x._2).equals(Empty))
            val emptyNeighbors:List[Coord2D] = (groupEmptyNeighbors++headEmptyNeighbors).distinct

            analyseGroup(t,stone,emptyNeighbors)

          }
        }
    analyseGroup(group,groupStone,List() )
  }

  def canCapture(board:Board,coord:Coord2D,player:Stone):Boolean ={
    val neighbors:List[Coord2D] = findNeighbors(coord).flatten
    val rivalStone:Stone = if(player.equals(Black)) White else Black

    //caso em que a posicao que se quer jogar esta rodeada por adversarios
    val rivalNeighbors:Int= neighbors.count(x => (board(x._1)(x._2).equals(rivalStone) ))
    //SO ESTA IMPLEMENTADO PARA O CASO SIMPLES DE QUERER CAPTURAR ALGUEM QUE RODEIA DIRETAMENTE A COORD PRETENDIDA

    if(rivalNeighbors == neighbors.length) { // se a posicao que se quer jogar tiver rodeada de rivais so se pode jogar se capturar
      //se estiver cercado temos de verificar se colocando a peça se pode capturar o "grupo" que a cerca
      canCaptureNeighbors(board,neighbors,player,rivalStone,coord)
    } else false


  }

    def canCaptureNeighbors(board:Board, neighbors:List[Coord2D],player:Stone,neighborStone:Stone,coord:Coord2D):Boolean ={
      def aux(auxNeighbors:List[Coord2D],auxCoord:Coord2D,numberOfCaptures:Int):Boolean = auxNeighbors match{
        case Nil => {if (numberOfCaptures>0) true else false}
        case h::t =>{
          val neighborNeighbors:List[Coord2D] = findNeighbors(h).flatten //vizinhos do vizinho h
          if(neighborNeighbors.count(x=> (board(x._1)(x._2).equals(player) || x.equals(coord)) ) == neighborNeighbors.length){
            aux(t,auxCoord,numberOfCaptures+1)
          }
          else{aux(t,auxCoord,numberOfCaptures)}

        }


        /*
        case Nil =>
        case h::t =>{
          val headNeighbors:List[Coord2D] = findNeighbors(h).flatten
          //verificamos se os vizinhos sao TODOS do jogador adversario
          if(headNeighbors.count(x => ( board(x._1)(x._2).equals(neighborStone)) || x.equals(auxCoord) ) == headNeighbors.length)
            true
          else false
          aux(t,auxCoord)}
      */
      }
      aux(neighbors,coord,0)
    }



  //T3
  def playRandomly(board: Board, r: MyRandom, player: Stone, lstOpenCoords: List[Coord2D], f: (List[Coord2D], MyRandom) => (Coord2D, MyRandom)): (Board, MyRandom, List[Coord2D]) = {
    val res = f(lstOpenCoords, r)
    val up = play(board, player, res._1, lstOpenCoords)
    (up._1.get, res._2,up._2)
  }

  //T5
  def captureGroupStones(board: Board, player: Stone, coord:Coord2D):(Board, Int) ={
        val group:List[Coord2D] = findGroup(board,coord,player)
        def canSomeoneCapture(auxGroup:List[Coord2D], toRemove:List[Coord2D], nrOfCaptures:Int):(Board, Int)= auxGroup match{
          case Nil => (board, nrOfCaptures)
          case h::t =>{
            val neighbors:List[Coord2D] = findNeighbors(h).flatten
            //ver quem eq e rival
            //para cada rival ver se e possivel capturar (por enquanto so caso simples de rodear esse rival
            //para cada rival q possa ser capturado incrementar o int e subs as pecas a remover por empty
            def neighborCapture(auxnei:List[Coord2D]):List[Coord2D]={

            }

          }

        }

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

  //testar findneighbors
  val testvizinho = findNeighbors((0,0))
  println("VIZINHOS:" + testvizinho)


}