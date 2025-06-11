import torch.tqdm.Tqdm.tqdm

object Demo1 {
  def main(args: Array[String]): Unit = {
    import scala.jdk.CollectionConverters.*
    for (i <- tqdm(List(1, 2, 3, 4, 5, 6,7,8,9,10,11,12), "iterating",color =None,sleepSpeed = Some(50),colorRandom = true)) {
      try{
        Thread.sleep(1000)
        println(i)
      }
      catch {
        case e: InterruptedException =>
          println(e.printStackTrace())
      }
    }
  }
}