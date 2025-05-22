package app

import javafx.application.Application
import model.GameState.{createNewBoard, createNewListOpenCoords}
import model.{GameState, Stone}

object FxApp {
    //criacao do gameState para o jogo na GUI
    val size = 5
    var game:GameState = GameState(createNewBoard(size), createNewListOpenCoords(size), 0,0, size, 1, 20000, 1, Stone.White, Stone.White, List())

    def main(args: Array[String]): Unit = {
      Application.launch(classOf[MainGUI], args: _*)
    }

}
