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
                      playerColor: Stone,
                      r: MyRandom
                    )

