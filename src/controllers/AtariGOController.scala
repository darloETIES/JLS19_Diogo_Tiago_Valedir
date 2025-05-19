package controllers

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


  //começa o jogo, com o clicar do botão de Play do menu das opcoes
  def startGameWithColor(color: String):Unit = {

    //cria a interatividade com as celulas da grid, considerando a cor do jogador
    addClickListernersToGrid(gridPaneGame.getChildren, color)
    println(color)
    moveTimer(30)
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
              //clicar na celula da grid
              stoneImageView.setOnMouseClicked { _ =>
                stoneImageView.setImage(new Image(imagePath))
                stoneImageView.setOpacity(1)

              }

              //fazer hover (passar o cursosr por cima)
              stoneImageView.setOnMouseEntered { _ =>
                hoverImageView.setOpacity(0.4)
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

  def moveTimer(turnTime:Int): Unit = {
    val step = 1.0 / turnTime

    def stepBar(current:Int): Unit = {
      if(current > turnTime) return //mudar para depois passar a vez ao próximo jogador

      val timeRemaining = turnTime - current
      turnTimeLabel.setText(timeRemaining.toString)

      if(timeRemaining <= 10){
        turnTimeLabel.setTextFill(Color.color(1,0,0))
        turnTimeProgressBar.setStyle("-fx-accent: red;")
      }

      turnTimeProgressBar.setProgress(current * step)
      //pois considerando que será de 1 segundo em 1 segundo, o valor atual multiplicado com o avanço dará o progresso da barra
      //está fora do evento abaixo pois será necessário atualizar logo imediatamente o estado atual da barra

      val pauseTransition = new PauseTransition(Duration.seconds(1)) //cria o temporizador que vai esperar 1 segundo
      pauseTransition.setOnFinished(_ => { //executa todo o código abaixo durante 1 segundo que foi definido

        stepBar(current+1) //assim incrementa 1 no current, para que no inicio dê 0 e nos restantes vá subido como suposto
      })
      pauseTransition.play() //inicia o temporizador com 1 segundo
    }
    stepBar(0) //começar o progresso em 0
  }

  def onNewGameMouseClicked(): Unit = {
    newGameBtn.getScene.getWindow.hide()
    guiutils.GameLauncher.launchNewGame() //a partir do objeto GameLauncher, cria um novo jogo
  }


}
