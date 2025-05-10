import AtariGo.{args, getClass}
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

class AtariGOApp extends Application{
  //Iniciar a GUI
  override def start(primaryStage: Stage): Unit={
    val fxmlLoader = new FXMLLoader(getClass.getResource("chooseView.fxml"))
    val mainRootView : Parent = fxmlLoader.load()
    val scene = new Scene(mainRootView)
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}

object FxApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[AtariGOApp], args: _*)
  }
}