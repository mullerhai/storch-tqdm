package torch.tqdm

import java.awt.{BorderLayout, Color, Font}
import javax.swing.{JFrame, JLabel, JProgressBar, WindowConstants}


object Tqdm {
  private[tqdm] val DEFAULT_NCOLS = 70

  def tqdm[T](a: List[T]) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, false)

  def tqdm(total: Int, desc: String) = new Tqdm[AnyRef](null, total, desc, DEFAULT_NCOLS, false)

  def tqdm(total: Int, desc: String, gui: Boolean) = new Tqdm[AnyRef](null, total, desc, DEFAULT_NCOLS, gui)

  def tqdm[T](a: Iterator[T], total: Int, desc: String) = new Tqdm[T](a, total, desc, DEFAULT_NCOLS, false)

  def tqdm[T](a: List[T], desc: String) = new Tqdm[T](a.iterator, a.size, desc, DEFAULT_NCOLS, false)

  def tqdm[T](a: List[T], gui: Boolean) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, gui)

  def tqdm[T](a: List[T], width: Int) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, false)

  def tqdm[T](a: Seq[T], desc: String) = new Tqdm[T](a.iterator, a.size, desc, DEFAULT_NCOLS, false)

  def tqdm[T](a: Seq[T], gui: Boolean) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, gui)

  def tqdm[T](a: Seq[T], width: Int) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, false)

  def tqdm[T](a: Set[T], desc: String) = new Tqdm[T](a.iterator, a.size, desc, DEFAULT_NCOLS, false)

  def tqdm[T](a: Set[T], gui: Boolean) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, gui)

  def tqdm[T](a: Set[T], width: Int) = new Tqdm[T](a.iterator, a.size, "", DEFAULT_NCOLS, false)

  def tqdm[T](a: Map[String,T], desc: String) = new Tqdm[T](a.valuesIterator, a.size, desc, DEFAULT_NCOLS, false)

  def tqdm[T](a: Map[String,T], gui: Boolean) = new Tqdm[T](a.valuesIterator, a.size, "", DEFAULT_NCOLS, gui)

  def tqdm[T](a: Map[String,T], width: Int) = new Tqdm[T](a.valuesIterator, a.size, "", DEFAULT_NCOLS, false)
}

class Tqdm[T]( var data: Iterator[T] ,
                            var total: Int,
                            var desc: String,
                            var ncols: Int,
                            var gui: Boolean
                           ) extends Iterable[T] {
  private[tqdm] var progress = -1
//  private[tqdm] var ncols = 0
  private[tqdm] val printIntervalInMilli = 1000
  //State
  this.ncols = if (ncols == -1) Tqdm.DEFAULT_NCOLS
  else ncols
  private[tqdm] var lastPrintTime: Long = 0
  private[tqdm] var lastProgress = 0L
  private[tqdm] var beginTime = 0L
  private[tqdm] val unitChar = '█'
  private[tqdm] var chars: Array[Char] = new Array[Char](ncols)
  private[tqdm] var guiProgress: Tqdm[T]#GuiProgress = null
  this.beginTime =  System.currentTimeMillis
  this.lastPrintTime = System.currentTimeMillis
//  this.chars = new Array[Char](ncols)
  if (gui) this.guiProgress = new GuiProgress()


  class GuiProgress  extends JFrame {
    val widths = 500
    val heights = 200
    private[tqdm] var progress: JProgressBar =  new JProgressBar
    private[tqdm] var label: JLabel =  new JLabel
    this.setSize(widths, heights)
    this.setTitle(desc)
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    progress.setMaximum(total)

    label.setBackground(Color.red)
    label.setHorizontalTextPosition(0)//JLabel.CENTER)
    label.setFont(new Font("微软雅黑", Font.ITALIC, 25))
    this.setLayout(new BorderLayout)
    this.setLocationRelativeTo(null)
    this.add(progress, BorderLayout.CENTER)
    this.add(label, BorderLayout.SOUTH)
    this.setResizable(false)
    this.setVisible(true)


    private[tqdm] def complete(): Unit = {
      this.setVisible(false)
      try Thread.sleep(2000)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
      this.dispose()
    }
  }

  def update(delta: Int): Unit = {
    progress += delta
    if (System.currentTimeMillis - lastPrintTime > printIntervalInMilli) {
      val speedString = formatSpeed(progress, lastProgress, System.currentTimeMillis - lastPrintTime)
      val percent = 1.0 * progress / total * 100
      val percentString = String.format("%2d%%", percent.toInt)
      val usedTime = System.currentTimeMillis - beginTime
      val leftTime = (usedTime * 1.0 / progress * (total - progress)).toLong
      val timeString = String.format("[%s<%s,%s]", formatTime(usedTime), formatTime(leftTime), speedString)
      if (gui) {
        this.guiProgress.progress.setValue(progress)
        this.guiProgress.label.setText(String.format("%s %d/%d %s", percentString, progress, total, timeString))
        if ((data != null && !data.hasNext) || this.progress >= this.total) this.guiProgress.complete()
      }
      else {
        val builder = new StringBuilder("\r")
        if (desc != null) builder.append(desc).append(':')
        //Percent String
        builder.append(percentString)
        //GuiProgress String
        val charCount = Math.min(progress * ncols / total, chars.length)
        java.util.Arrays.fill(chars, 0, charCount, unitChar)
        java.util.Arrays.fill(chars, charCount, ncols, ' ')
        builder.append('|').append(chars).append('|')
        //GuiProgress Desc String
        builder.append(String.format("%d/%d", progress, total))
        builder.append(timeString)
        System.out.print(builder.toString)
      }
      lastPrintTime = System.currentTimeMillis
      lastProgress = progress
    }
  }

  override def iterator: Iterator[T] = new Iterator[T]() {
    override def hasNext: Boolean = {
      if (data == null) throw new RuntimeException("tqdm need set data")
      data.hasNext
    }

    override def next: T = {
      update(1)
      data.next
    }
  }

  private[tqdm] def formatTime(durations: Long) = {
    var duration = durations
    val SECOND = 1000
    val MINUTE = 60 * SECOND
    val HOUR = 60 * MINUTE
    val DAY: Int = 24 * HOUR
    val day = duration / DAY
    duration = duration.toInt % DAY
    val hour = duration / HOUR
    duration %= HOUR
    val minute = duration / MINUTE
    duration %= MINUTE
    val second = duration / SECOND
    val builder = new StringBuilder
    if (day > 0) builder.append(String.format("%dd", day))
    if (hour > 0) builder.append(String.format("%dH", hour))
    if (minute > 0) builder.append(String.format("%dm", minute))
    if (second > 0) builder.append(String.format("%ds", second))
    builder.toString
  }

  private[tqdm] def formatSpeed(currentProgress: Long, lastProgress: Long, duration: Long) = {
    val progress = currentProgress - lastProgress
    if (progress == 1) String.format("%s/iter", formatTime(duration))
    else String.format("%s iter/s", progress)
  }
}