package controllers

import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider}
import javafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, Pane, RowConstraints, StackPane}

class AtariGOController {

  @FXML
  private var gameborderPane:BorderPane = _

  @FXML
  private var gridPaneGame:GridPane = _


  //gameOptions widgets
  @FXML
  private var borderPaneOptions:BorderPane = _

  @FXML
  private var playButton:Button = _

  //reinicia o jogo, limpando o tabuleiro
  /*def resetGame(): Unit={
    gridPaneGame.getChildren.clear()
    gridPaneGame.getColumnConstraints.clear()
    gridPaneGame.getRowConstraints.clear()
  }*/


  def onPlayButtonClicked(): Unit = {
    //if(gridPaneGame != null) resetGame()

    //ativa o painel do jogo e remove o efeito colocado anteriormente
    //gameborderPane.setDisable(false)
    //gameborderPane.setEffect(null)

    //fecha a janela modal das opcoes
    val parent = borderPaneOptions.getParent //irá obter o StackPane
    if(parent != null && parent.isInstanceOf[Pane]){
      val pane = parent.asInstanceOf[Pane]
      pane.getChildren.remove(borderPaneOptions)
    }
  }
}
