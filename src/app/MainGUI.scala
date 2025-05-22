package app


import javafx.application.Application
import javafx.stage.Stage

class MainGUI extends Application{
  //Iniciar a GUI
  override def start(primaryStage: Stage): Unit={
    app.GameLauncher.launchNewGame()
  }
}