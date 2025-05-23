package controllers

import app.FxApp
import javafx.fxml.FXML
import javafx.scene.control.{Button, RadioButton, Slider, ToggleGroup}
import javafx.stage.Stage
import model.{GameState, Stone}

class OptionsViewController(val atariGOController: AtariGOController) {

  @FXML
  private var playButton:Button = _

  @FXML
  private var stonesToWinSlider:Slider = _

  @FXML
  private var difficultySlider:Slider = _

  @FXML
  private var radioPlayerColorBtns:ToggleGroup = _

  @FXML
  private var blackRadio:RadioButton = _

  @FXML
  private var whiteRadio:RadioButton = _

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
    FxApp.game = FxApp.game.copy(
      stonesToWin = stonesToWinSlider.getValue.toInt,
      playerColor = if(getSelectedColor == "White") Stone.White else Stone.Black,
      currentPlayer = if(getSelectedColor == "White") Stone.White else Stone.Black,
      difficultyLevel = difficultySlider.getValue.toInt
    )

    //obtem a cor escolhida para jogar dos botoes Radio
    atariGOController.startGame(getSelectedColor)

    //fecha a janela modal das opcoes
    val stage = playButton.getScene.getWindow.asInstanceOf[Stage]
    stage.close()

  }

}
