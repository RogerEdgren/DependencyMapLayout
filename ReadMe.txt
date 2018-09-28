Dependency Map Layout

* Parses Paipe xml input files and generates paths between nodes using A*, that gets drawn on the screen.
* Uses Processing for access to drawing primitives. Get from here: https://processing.org/
* The main class of interest is EdgePathFinder. The central function is void findPath(Edge edge).
* The path finding algorithm used is described here: https://www.redblobgames.com/pathfinding/a-star/introduction.html  


* TODOs:
- right now the performance is crap for all but trivial cases. It might be made better by having a variable mapping between the size of the grid that gets drawn, and the search grid. Right now they are always 1:1 and it's way too slow. Try making a 5x5, or a 10x10 size drawn grid a single path finding node and check performance.
- optimize the resulting path with some sort of line-of-sight test that removes points that are not needed. For all but trivial cases, the result is now a useless jumble of lines that the brain cannot comfortably process.
- make the path finding parameters user-editable.
- allow the user to choose which file to input. Right now it's hard coded in the main function.
- add safety checks to the xml file parser, if needed.


