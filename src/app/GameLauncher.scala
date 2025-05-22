package app

import controllers.{AtariGOController, OptionsViewController}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}

//objeto usado para começar um novo jogo
object GameLauncher {
  def launchNewGame(): Unit ={ //metodo para iniciar o AtariGO atraves da GUI
    //carrega a janela do jogo
    val atariGOStage: Stage = new Stage()
    val fxmlLoader = new FXMLLoader(getClass.getResource("/views/atariGoView.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()

    //obter o controller do jogo
    val atariController = fxmlLoader.getController[AtariGOController]

    val scene = new Scene(mainViewRoot)
    atariGOStage.setScene(scene)
    atariGOStage.show()
    atariGOStage.setTitle("Atari GO")

    //carrega a janela modal das opcoes
    val optionsStage: Stage = new Stage()
    val modalFxmlLoader = new FXMLLoader(getClass.getResource("/views/gameOptionsView.fxml"))
    //cria um novo controller do menu das opcoes, associado ao controller do jogo
    modalFxmlLoader.setControllerFactory(_ => new OptionsViewController(atariController))

    val modalViewRoot: Parent = modalFxmlLoader.load()


    val modalScene = new Scene(modalViewRoot)
    optionsStage.setScene(modalScene)

    optionsStage.initModality(Modality.WINDOW_MODAL)
    optionsStage.initOwner(atariGOStage.getScene.getWindow)
    optionsStage.show()

    //event handler
    //caso ocorra o fecho da janela de opcoes, fecha o jogo
    optionsStage.setOnCloseRequest( _ => {
      atariGOStage.close()
    })
  }
}
