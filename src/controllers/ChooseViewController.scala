package controllers

import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.Button
import javafx.stage.Stage

class ChooseViewController {

  @FXML
  private var buttonGUI: Button = _

  @FXML
  private var buttonTUI: Button = _

  def onButtonGUIClicked(): Unit={
    val AtariGOStage: Stage = new Stage()
    val fxmlLoader = new FXMLLoader(getClass.getResource("/views/AtariGoView.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene = new Scene(mainViewRoot)
    AtariGOStage.setScene(scene)
    AtariGOStage.show()

    //Fecha a janela atual
    buttonGUI.getScene.getWindow.hide()
  }
}
