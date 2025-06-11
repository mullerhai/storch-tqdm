import torch.tqdm.Tqdm.tqdm

object Demo4 {
  def main(args: Array[String]): Unit = {
    val n = 100
    val x = tqdm(n, "Storch_Training", true,color =None,sleepSpeed = Some(50),colorRandom = false)
    for (i <- 0 until n) {
      try Thread.sleep(100)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
      x.update(1)
    }
  }
}