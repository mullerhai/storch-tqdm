import torch.tqdm.Tqdm.tqdm

object Demo1 {
  def main(args: Array[String]): Unit = {
    import scala.jdk.CollectionConverters.*
    for (i <- tqdm(List(1, 2, 3, 4, 5, 6), "iterating")) {
      try Thread.sleep(1000)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
    }
  }
}