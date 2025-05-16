package controllers

import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.Button
import javafx.scene.effect.GaussianBlur
import javafx.scene.layout.StackPane
import javafx.stage.{Modality, Stage}

class ChooseViewController {

  @FXML
  private var buttonGUI: Button = _

  @FXML
  private var buttonTUI: Button = _


  def onButtonGUIClicked(): Unit={

    //Fecha a janela atual (chooseView)
    buttonGUI.getScene.getWindow.hide()

    //carrega a janela do jogo
    val atariGOStage: Stage = new Stage()
    val fxmlLoader = new FXMLLoader(getClass.getResource("/views/atariGoView.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()

    //obter o controller do jogo
    val atariController = fxmlLoader.getController[AtariGOController]

    val scene = new Scene(mainViewRoot)
    atariGOStage.setScene(scene)
    atariGOStage.show()



    //carrega a janela modal das opcoes
    val optionsStage: Stage = new Stage()
    val modalFxmlLoader = new FXMLLoader(getClass.getResource("/views/gameOptionsView.fxml"))
    val modalViewRoot: Parent = modalFxmlLoader.load()

    //obter o controller do jogo
    val optionsController = modalFxmlLoader.getController[OptionsViewController]

    val modalScene = new Scene(modalViewRoot)
    optionsStage.setScene(modalScene)

    optionsStage.initModality(Modality.WINDOW_MODAL)
    optionsStage.initOwner(atariGOStage.getScene.getWindow)
    optionsStage.show()


    optionsController.setAtariGOController(atariController)

  }
}
