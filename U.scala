import scala.actors._
import scala.actors.Actor._
import scala.math.{pow,abs}
import collection.mutable.HashMap
import scala.util.Random

object U extends Actor {
  var notDone = new HashMap[Double,Boolean]()
  val rnd = new Random()
  var ctr = 0

  def shuffle(x:Seq[(Int,Int)]) = rnd.shuffle( x )

  def init( p:List[Double]) = p.foreach( x=> notDone += (x->true))

  def fin( p:Double) = {
    notDone(p) = false
    if (!notDone.values.toList.contains(true)) sys.exit(1)
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
        case( caller:Actor, xy:(Int,Int), leftTop:(Int,Int), rightBottom:(Int,Int), path:List[(Int,Int)]) => {

          // find 8 points around xy - the 3 points to the left, 3 to the right, & one above & below.
          val (x,y) = xy
          val res = Seq(x-1,x,x+1).map( a => Seq(y-1,y,y+1).map( b=> (a,b)))
          .flatten
          .filterNot( ab => ab._1 == x && ab._2 == y)
          .filterNot( ab=> ab._1 < leftTop._1 || ab._1 > rightBottom._1)
          .filterNot( ab=> ab._2 < leftTop._2 || ab._2 > rightBottom._2)
          .filterNot( ab=> path.contains(ab))

          if( res.size > 0) caller ! U.shuffle(res)
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

      // 1. given a start, end, and a src,
      // you find all dest points you can get to from that src
      // find the shortest dest points by minkowski
      // if you've reached end, report success and bail!
      // make n actors, n = # of shortest dest
      // src = each member of shortest dest
      // GOTO 1
      val p = List(2.0)
      U.init(p)
      U.start
      p.foreach( pp => new PathFinder( pp, leftTop, rightBottom, startCoord, finish, List(startCoord) ).start)
  }
}


class PathFinder(p:Double, leftTop:(Int,Int), rightBottom:(Int,Int),src:(Int,Int), finish:(Int,Int), path:List[(Int,Int)]) extends Actor {

    def progress = U.progress( path.reverse.toString )

    def act = {

      if( U.notDone(p) ) {
        progress
        U ! (this, src, leftTop, rightBottom, path)
      }

      react {
        case (dest:List[(Int,Int)]) => {

            if( U.notDone(p)) {

              val mink = dest.map( d => (U.minkowski( src, d, p),d ))
              val minmink = mink.minBy( _._1)._1
              val candidates = U.shuffle( mink.filter( xy => abs(xy._1 - minmink) < 1e-10).map( xy=> xy._2 ) )

              if (candidates.contains( finish )) {
                println( "DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!   p:" + p + ", " + (finish::path).reverse )
                U.fin(p)
              } else {
                if( U.notDone(p) ) {
                  candidates.foreach( c => new PathFinder( p,leftTop,rightBottom, c, finish, c::path ).start )
                }
              }

            }
        }
      }
    }
}