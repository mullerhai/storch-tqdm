import torch.tqdm.Tqdm.tqdm

object Demo4 {
  def main(args: Array[String]): Unit = {
    val n = 100
    val x = tqdm(n, "haha", true)
    for (i <- 0 until n) {
      try Thread.sleep(1000)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
      x.update(1)
    }
  }
}