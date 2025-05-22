package controllers

import scala.jdk.CollectionConverters._
import app.FxApp
import app.FxApp.size
import com.sun.glass.ui.Application.EventHandler
import javafx.animation.{Animation, FadeTransition, PauseTransition}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Parent, Scene}
import javafx.scene.control.{Button, Label, ProgressBar, Slider}
import javafx.scene.effect.DropShadow
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, Pane, RowConstraints, StackPane}
import javafx.scene.paint.Color
import javafx.stage.{Modality, Stage}
import javafx.util.Duration
import model.{AtariGOUtils, GameState, MyRandom, Stone}
import model.GameState.{Board, Coord2D, createNewBoard, createNewListOpenCoords, randomMove, undo}

import scala.::
import scala.annotation.tailrec

class AtariGOController {

  @FXML
  private var gameborderPane:BorderPane = _

  @FXML
  private var gridPaneGame:GridPane = _

  @FXML
  private var turnTimeProgressBar:ProgressBar = _

  @FXML
  private var turnTimeLabel:Label = _

  @FXML
  private var undoBtn:Button = _

  @FXML
  private var newGameBtn:Button = _

  //gameOptions widgets
  @FXML
  private var borderPaneOptions:BorderPane = _

  @FXML
  private var optionsViewController: OptionsViewController = _

  private var currentPauseTransition: Option[PauseTransition] = None


  //começa o jogo, com o clicar do botão de Play do menu das opcoes
  def startGame(color: String):Unit = {


    updateUndoButtonState()
    //cria a interatividade com as celulas da grid, considerando a cor do jogador
    addClickListernersToGrid(gridPaneGame.getChildren, color)
    println(color)
    resetTimer()
  }

  def addClickListernersToGrid(children: java.util.List[javafx.scene.Node],playerColor:String, i: Int = 0): Unit = {
    if(i < children.size()){
      children.get(i) match{
        case stackPane: StackPane => {
          val stoneImageView = stackPane.getChildren.get(1).asInstanceOf[ImageView]
          //verifica se existe cor escolhida e seleciona o caminho para a imagem respetiva
          if(playerColor != null){
            val imagePath = playerColor match {
              case "Black" => "/resources/blackStone.png"
              case "White" => "/resources/whiteStone.png"
              case _ => "/resources/noStone.png"
            }

            //criacao de um ImageView adicional para adicionar o efeito de hover (passar o rato por cima)
            val hoverImageView = new ImageView(new Image(imagePath))
            hoverImageView.setFitWidth(100)
            hoverImageView.setFitHeight(100)
            hoverImageView.setOpacity(0.0)
            stackPane.getChildren.add(hoverImageView) //fica por cima das restantes no stackPane da celula da grid
            hoverImageView.setMouseTransparent(true) //faz com que não entre em conflito com o evento para adicionar a stone

            val noStoneURL = getClass.getResource("/resources/noStone.png").toString


            //caso a imagem seja noStone.png (não tem stone)
            if(stoneImageView.getImage.getUrl == noStoneURL){
              //event handling
              //fazer hover (passar o cursosr por cima)
              stoneImageView.setOnMouseEntered { _ =>

                val row = Option(GridPane.getRowIndex(stackPane).toInt).getOrElse(0)
                val col = Option(GridPane.getColumnIndex(stackPane).toInt).getOrElse(0)
                val coord2DtoPlay: Coord2D = (row, col)
                //após saber a coord, fazer aqui um if que cobre todo o codigo abaixo, de forma a verificar a validade da jogada
                //ex: if(isValid())
                //assim posso fazer depois o play(...) so depois de ter verificado
                if (FxApp.game.board(row)(col) == Stone.Empty && FxApp.game.currentPlayer == FxApp.game.playerColor) {
                  hoverImageView.setOpacity(0.4)
                  //clicar na celula da grid
                  stoneImageView.setOnMouseClicked { _ =>

                    //fazer a jogada
                    val (newBoard, newLstOpenCoords) = FxApp.game.play(coord2DtoPlay)
                    val newHist: List[GameState] = FxApp.game :: FxApp.game.history
                    val nextPlayer = if (FxApp.game.currentPlayer == Stone.Black) Stone.White else Stone.Black

                    FxApp.game = FxApp.game.copy(board = newBoard.get, lstOpenCoords = newLstOpenCoords, currentPlayer = nextPlayer, history = newHist)

                    val r = MyRandom(AtariGOUtils.readMyRandomStateToFile())
                    val newRand = AtariGOUtils.createNewMyRandom(r)

                    //passa para a vez do bot e atualiza o jogo
                    computerTurn()

                    println(" \nBoard:\n " + AtariGOUtils.printBoard(FxApp.game.board))
                  }
                }
              }
              //cancelar o hover (sair com o cursor da celula da grid)
              stoneImageView.setOnMouseExited { _ =>
                hoverImageView.setOpacity(0)
              }
            }
          }
        }
      }
      addClickListernersToGrid(children, playerColor, i+1)

    }
  }

  def updateGame(board: Board): Unit = {



    updateUndoButtonState()
    val children = gridPaneGame.getChildren.asScala.toList

    @tailrec
    def updateGrid(nodes: List[javafx.scene.Node]): Unit = {
      nodes match{
        case Nil => ()
        case (stackPane: StackPane) :: t =>
          val row = Option(GridPane.getRowIndex(stackPane).toInt).getOrElse(0)
          val col = Option(GridPane.getColumnIndex(stackPane).toInt).getOrElse(0)
          val stone = board(row)(col)

          val stoneImageView = stackPane.getChildren.get(1).asInstanceOf[ImageView]
          val imagePath = stone match {
            case Stone.Black => "resources/blackStone.png"
            case Stone.White => "resources/whiteStone.png"
            case Stone.Empty => "resources/noStone.png"
          }

          stoneImageView.setImage(new Image(imagePath))
          stoneImageView.setOpacity(if (stone == Stone.Empty) 0.0 else 1.0) //devido a evitar possível sombreamento, pois sem a stone tambem sera uma imagem

          updateGrid(t)

        case _ :: t => updateGrid(t) //tenho que encontrar o grid, por isso tento ir "mais abaixo"
      }
    }
    updateGrid(children)
  }

  def computerTurn(): Unit = {


    if(FxApp.game.currentPlayer != FxApp.game.playerColor){
      //de forma a ser o mais aleatorio possivel, obtem o valor a partir do tempo atual
      val newRand = MyRandom(System.currentTimeMillis())
      val (newBoard, newR, newLstOpenCoords) = FxApp.game.playRandomly(newRand, (lst, rand) => randomMove(lst, rand))
      val nextPlayer = if (FxApp.game.currentPlayer == Stone.Black) Stone.White else Stone.Black
      updateGame(newBoard)

      FxApp.game = FxApp.game.copy(board = newBoard, lstOpenCoords = newLstOpenCoords, currentPlayer = nextPlayer)
    }
    resetTimer() //pois sabemos que, ao fim de uma jogada do jogador, o bot joga, e aí o temporizador tera que dar reset

  }

  def resetTimer(): Unit = {
    currentPauseTransition match {
      case Some(x) => x.stop()
      case None => ()
    }
    val tl = FxApp.game.timeLimit/1000
    turnTimeLabel.setText(tl.toString)
    turnTimeProgressBar.setProgress(0)

    val newPauseTransition = new PauseTransition(Duration.seconds(1))
    currentPauseTransition = Some(newPauseTransition)
    moveTimer(tl, currentPauseTransition.get)
  }


  def moveTimer(turnTime:Int, pauseTransition: PauseTransition): Unit = {
    val step = 1.0 / turnTime

    def stepBar(current:Int, pauseTransition: PauseTransition): Unit = {

      println(current + " - " + turnTime)
      if(current >= turnTime) {
        pauseTransition.stop()

        FxApp.game = FxApp.game.copy(currentPlayer = if (FxApp.game.playerColor == Stone.Black) Stone.White else Stone.Black, history = FxApp.game :: FxApp.game.history)
        computerTurn()
      }
      else{

        val timeRemaining = turnTime - current
        turnTimeLabel.setText(timeRemaining.toString)

        if(timeRemaining <= 10){
          turnTimeLabel.setTextFill(Color.color(1,0,0))
          turnTimeProgressBar.setStyle("-fx-accent: red;")
        }
        else{
          turnTimeLabel.setTextFill(Color.color(0,0,0))
          turnTimeProgressBar.setStyle("-fx-accent: white;")
        }

        turnTimeProgressBar.setProgress(current * step)
        //pois considerando que será de 1 segundo em 1 segundo, o valor atual multiplicado com o avanço dará o progresso da barra
        //está fora do evento abaixo pois será necessário atualizar logo imediatamente o estado atual da barra

        pauseTransition.setOnFinished(_ => { //executa todo o código abaixo durante 1 segundo que foi definido

          stepBar(current+1, pauseTransition) //assim incrementa 1 no current, para que no inicio dê 0 e nos restantes vá subido como suposto
        })
        pauseTransition.play() //inicia o temporizador com 1 segundo
      }

    }
    stepBar(0, pauseTransition) //começar o progresso em 0
  }

  def onNewGameMouseClicked(): Unit = {
    newGameBtn.getScene.getWindow.hide()
    FxApp.game = GameState(createNewBoard(size), createNewListOpenCoords(size), 0,0, size, 1, 30000, 1, Stone.White, Stone.White, List())
    app.GameLauncher.launchNewGame() //a partir do objeto GameLauncher, cria um novo jogo
  }

  def onUndoBtnMouseClicked(): Unit = {
    resetTimer()
    undo(FxApp.game.history) match {
      case Some((oldGameState, oldHistory)) =>
        FxApp.game = oldGameState
        FxApp.game = FxApp.game.copy(history = oldHistory)
        updateGame(oldGameState.board)
      case None => updateGame(FxApp.game.board)
    }
  }

  def updateUndoButtonState(): Unit = {
    undoBtn.setDisable(FxApp.game.history.isEmpty)
  }
}
