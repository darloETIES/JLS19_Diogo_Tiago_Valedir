trait RandomWithState {
  def nextInt(n:Int): (Int, RandomWithState)
}
