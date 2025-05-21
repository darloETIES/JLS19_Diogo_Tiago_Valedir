import AtariGo.{Board, Coord2D}
import Stone.Stone

case class GameState(
                      board: Board,
                      lstOpenCoords: List[Coord2D],
                      playerCapture: Int,
                      computerCapture: Int,
                      size: Int,
                      stonesToWin: Int,
                      timeLimit: Int,
                      difficultyLevel: Int,
                      playerColor: Stone, //identifica a cor do jogador humano
                      currentPlayer: Stone //identifica o jogador atual (humano ou "bot")
                    )

