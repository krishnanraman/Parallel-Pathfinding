import scala.actors._
import scala.actors.Actor._
import scala.math.{pow,abs}
import scala.util.Random

object U extends Actor {

  val rnd = new Random()
  var ctr = 0
  var notDone = true
  val eps = 1e-10

  def shuffle(x:Seq[(Int,Int)]) = rnd.shuffle( x )

  def fin = {
    notDone = false
    ctr
  }

  def progress(str:String) = {
    ctr += 1
    println( ctr + ":" + str )
  }

  def minkowski( ab:(Int,Int), cd:(Int,Int), p:Double) = {
    val (a,b) = ab
    val (c,d) = cd
    pow(pow(abs(a-c),p) + pow(abs(b-d),p), 1.0/p)
  }

  def act = {
    loop {
      react {
        case( p:Double, xy:(Int,Int), leftTop:(Int,Int), rightBottom:(Int,Int), path:List[(Int,Int)], caller:Actor) => {

          // find 8 points around xy - the 3 points to the left, 3 to the right, & one above & below.
          val (x,y) = xy
          val res = Seq(x-1,x,x+1).map( a => Seq(y-1,y,y+1).map( b=> (a,b)))
          .flatten
          .filterNot( ab => ab._1 == x && ab._2 == y)  // don't include me
          .filterNot( ab=> ab._1 < leftTop._1 || ab._1 > rightBottom._1) // don't include points outside the maze
          .filterNot( ab=> ab._2 < leftTop._2 || ab._2 > rightBottom._2)
          .filterNot( ab=> path.contains(ab)) // don't revisit points along your path

          // find the candidates closest to source
          if( res.size > 0 ) {
            val mink = res.map( dest => (U.minkowski( xy, dest, p),dest ))
            val minmink = mink.minBy( _._1)._1
            val candidates = mink.filter( xy => abs(xy._1 - minmink) < eps ).map( xy=> xy._2 )

            caller ! U.shuffle(candidates)
          }
        }
      } // end react
    } // end loop
  } // end act

  def main(args:Array[String]) = {
      // want to go from start coord to finish in the shortest possible way as per p-minkowski
      val leftTop = (0,0)
      val rightBottom = (9,9)
      val startCoord = (0,2)
      val finish = (9,8)
      val p = 20
      U.start
      new PathFinder( p, leftTop, rightBottom, startCoord, finish, List(startCoord) ).start
  }
}

class PathFinder(p:Double, leftTop:(Int,Int), rightBottom:(Int,Int),src:(Int,Int), finish:(Int,Int), path:List[(Int,Int)]) extends Actor {

  def progress = U.progress( path.reverse.toString )

  def act = {

    if( U.notDone ) {
      progress
      U ! (p, src, leftTop, rightBottom, path, this) // get candidate nodes to visit
    }

    react {
      case (candidates:List[(Int,Int)]) => {
          if( U.notDone ) {
            if (candidates.contains( finish ))
              println( "Out of maze with "+ U.fin + " clones!  p:" + p + ", " + (finish::path).reverse )
            else {
              if( U.notDone ) candidates.foreach( c => new PathFinder( p,leftTop,rightBottom, c, finish, c::path ).start )
            }
          }
      }
    }
  }
}
