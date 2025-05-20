
import controllers.{AtariGOController, OptionsViewController}
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}

class MainGUI extends Application{
  //Iniciar a GUI
  override def start(primaryStage: Stage): Unit={
    guiutils.GameLauncher.launchNewGame()
  }
}

object FxApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MainGUI], args: _*)
  }
}