import torch.tqdm.Tqdm.tqdm
object Demo2 {
  def main(args: Array[String]): Unit = {
    import scala.jdk.CollectionConverters.*
    for (i <- tqdm(List(1, 2, 3, 4, 5, 6), true)) {
      try {
        Thread.sleep(1000)
        println(i)
      }
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
    }
  }
}