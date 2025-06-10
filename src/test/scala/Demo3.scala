
import torch.tqdm.Tqdm
object Demo3 {
  @throws[InterruptedException]
  def main(args: Array[String]): Unit = {
    val x = Tqdm.tqdm(100, "iterating")
    for (i <- 0 until 100) {
      x.update(1)
      Thread.sleep(10000)
    }
  }
}