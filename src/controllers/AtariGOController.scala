package controllers

import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider}
import javafx.scene.effect.DropShadow
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, Pane, RowConstraints, StackPane}
import javafx.scene.paint.Color
import javafx.stage.Stage

class AtariGOController {

  @FXML
  private var gameborderPane:BorderPane = _

  @FXML
  private var gridPaneGame:GridPane = _

  @FXML
  private var undoBtn:Button = _

  @FXML
  private var newGameBtn:Button = _

  //gameOptions widgets
  @FXML
  private var borderPaneOptions:BorderPane = _

  @FXML
  private var optionsViewController: OptionsViewController = _


  def startGameWithColor(color: String):Unit = {

    //cria a interatividade com as celulas da grid, considerando a cor do jogador
    addClickListernersToGrid(gridPaneGame.getChildren, color)
    println(color)
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

            //event handling
            //clicar na celula da grid
            stoneImageView.setOnMouseClicked { _ =>
              stoneImageView.setImage(new Image(imagePath))
              stoneImageView.setOpacity(1)

            }
          }

        }
      }
      addClickListernersToGrid(children, playerColor, i+1)

    }
  }

  //reinicia o jogo, limpando o tabuleiro
  /*def resetGame(): Unit={
    gridPaneGame.getChildren.clear()
    gridPaneGame.getColumnConstraints.clear()
    gridPaneGame.getRowConstraints.clear()
  }*/



}
