import AtariGOUtils.{printBoard, readMyRandomStateToFile, writeMyRandomStateToFile}
import AtariGo.Board
import Stone._
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

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