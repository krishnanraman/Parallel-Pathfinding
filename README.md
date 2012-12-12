<pre>
Concurrent Pathfinding in a maze
=================================

Assume you are stuck in a 10x10 maze. 
The coordinates run from (0,0) to (9,9), and there are 100 nodes.

Say you start out at (0,2).
Your destination ? (9,8)

How do you find the shortest way out ?
And lets say, you can clone yourself!

Sweet, so you can make infinite copies of yourself.

Algorithm: Naive Parallel Pathfinder
---------
100:
So long as you haven't arrived at your destination...

At each node, make atmost 8 copies of yourself.
( you can convince yourself that there are 3 nodes to your left, 3 to your right, 1 above & 1 below you. )

Some of these nodes cannot be visited ( because they are outside the maze boundary)
Some of these nodes should not be revisited. ( because you've already been there )
Some of these nodes are not at the shortest distance from you ( for some definition of shortest )
So filter those out, and get a set of valid candidates.

What do you do with these candidates ?
For each one of them, GOTO 100

Fin!
---------

Performance: Not rigorous, but 500,000 - 800,000 clones of yourself seems to be enough to get out of the maze.

Results look like this ---
1:List((0,2))
2:List((0,2), (0,1))
4:List((0,2), (0,3))
3:List((0,2), (1,2))
5:List((0,2), (0,1), (1,1))
6:List((0,2), (0,1), (0,0))
7:List((0,2), (0,3), (1,3))
8:List((0,2), (0,3), (0,4))
9:List((0,2), (1,2), (1,1))
11:List((0,2), (1,2), (2,2))
10:List((0,2), (1,2), (1,3))
12:List((0,2), (0,1), (1,1), (2,1))
14:List((0,2), (0,1), (1,1), (1,2))
15:List((0,2), (0,1), (0,0), (1,0))
13:List((0,2), (0,1), (1,1), (1,0))
16:List((0,2), (0,3), (1,3), (2,3))
17:List((0,2), (0,3), (1,3), (1,4))
18:List((0,2), (0,3), (1,3), (1,2))
19:List((0,2), (0,3), (0,4), (1,4))
20:List((0,2), (0,3), (0,4), (0,5))
21:List((0,2), (1,2), (1,1), (0,1))
23:List((0,2), (1,2), (1,1), (1,0))
22:List((0,2), (1,2), (1,1), (2,1))
24:List((0,2), (1,2), (2,2), (3,2))
26:List((0,2), (1,2), (2,2), (2,1))
25:List((0,2), (1,2), (2,2), (2,3))
27:List((0,2), (1,2), (1,3), (2,3))
29:List((0,2), (1,2), (1,3), (1,4))
28:List((0,2), (1,2), (1,3), (0,3))
30:List((0,2), (0,1), (1,1), (2,1), (2,2))
32:List((0,2), (0,1), (1,1), (2,1), (3,1))
32:List((0,2), (0,1), (1,1), (2,1), (2,0))
33:List((0,2), (0,1), (1,1), (1,2), (2,2))
34:List((0,2), (0,1), (1,1), (1,2), (1,3))
36:List((0,2), (0,1), (0,0), (1,0), (1,1))
35:List((0,2), (0,1), (0,0), (1,0), (2,0))
37:List((0,2), (0,1), (1,1), (1,0), (0,0))
......
729176:List((0,2), (0,3), (1,3), (2,3), (2,2), (2,1), (2,0), (3,0), (4,0), (5,0), (6,0), (7,0), (7,1), (7,2), (6,2), (5,2))
729171:List((0,2), (0,3), (1,3), (2,3), (2,2), (2,1), (2,0), (3,0), (4,0), (5,0), (6,0), (7,0), (7,1), (7,2), (8,2), (9,2))
729167:List((0,2), (0,3), (1,3), (2,3), (2,2), (2,1), (2,0), (3,0), (4,0), (5,0), (6,0), (7,0), (7,1), (6,1), (6,2), (7,2))
729177:List((0,2), (0,3), (1,3), (2,3), (2,2), (2,1), (2,0), (3,0), (4,0), (5,0), (6,0), (7,0), (7,1), (7,2), (6,2), (6,3))
729175:List((0,2), (0,3), (1,3), (2,3), (2,2), (2,1), (2,0), (3,0), (4,0), (5,0), (6,0), (7,0), (7,1), (7,2), (6,2), (6,1))
Out of maze with 729177 clones!  p:20.0, List((0,2), (0,3), (1,3), (2,3), (2,4), (3,4), (3,5), (4,5), (4,6), (5,6), (5,7), (6,7), (7,7), (8,7), (9,7), (9,8))


Implementation in Scala:
U: A singleton Actor that computes destinations, given a source node.
PathFinder: Given a source node, this Actor creates as many PathFinders as there are destinations.

So 729,177 PathFinders ie. clones were created to get you out of the maze.

Note: Definition of Shortest
We use the parametrized Minkowski distance.
So if you prefer as-the-crow-flies, use Euclidean ( Minkowski 2 )
If you prefer Shortest = parallel-perpendicular ie. Manhattan metric, use Minkowski 1
You can get other definitions of "shortest" for different Minkowski params.

Each definition of shortest brings some points closer to you than others.
For more, see: http://en.wikipedia.org/wiki/Minkowski_distance
</pre>












