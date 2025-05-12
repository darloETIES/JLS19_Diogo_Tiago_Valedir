package controllers

import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class ChooseViewController {

  @FXML
  private var buttonGUI: Button = _

  @FXML
  private var buttonTUI: Button = _

  def onButtonGUIClicked(): Unit={

    //carrega a Scene com uma Scene modal embutida (2 Scenes numa Stage)
    val AtariGOStage: Stage = new Stage()
    val fxmlLoader = new FXMLLoader(getClass.getResource("/views/atariGoView.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene = new Scene(mainViewRoot)
    AtariGOStage.setScene(scene)
    AtariGOStage.show()

    //Fecha a janela atual (chooseView)
    buttonGUI.getScene.getWindow.hide()

    //carrega a janela modal das opcoes do jogo
    val gameOptionsModalLoader = new FXMLLoader(getClass.getResource("/views/gameOptionsView.fxml"))
    val modalContent:Parent = gameOptionsModalLoader.load()

    val rootPane = mainViewRoot.asInstanceOf[StackPane]
    rootPane.getChildren.add(modalContent)
  }
}
