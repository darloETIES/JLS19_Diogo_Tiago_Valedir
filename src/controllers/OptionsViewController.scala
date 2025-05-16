package controllers

import javafx.fxml.FXML
import javafx.scene.control.{Button, RadioButton, ToggleGroup}
import javafx.stage.Stage

class OptionsViewController {

  @FXML
  private var playButton:Button = _

  @FXML
  private var radioPlayerColorBtns:ToggleGroup = _

  @FXML
  private var blackRadio:RadioButton = _

  @FXML
  private var whiteRadio:RadioButton = _

  @FXML
  private var atariGOController:AtariGOController = _

  def setAtariGOController(controller: AtariGOController): Unit = {
    this.atariGOController = controller
  }

  def getSelectedColor: String = {
    val selectedToggle = radioPlayerColorBtns.getSelectedToggle
    if(selectedToggle != null){
      selectedToggle.asInstanceOf[RadioButton].getText
    }
    else{
      ""
    }
  }

  def onPlayButtonClicked(): Unit = {

    //obtem a cor escolhida para jogar dos botoes Radio
    atariGOController.startGameWithColor(getSelectedColor)

    //fecha a janela modal das opcoes
    val stage = playButton.getScene.getWindow.asInstanceOf[Stage]
    stage.close()

  }

}
