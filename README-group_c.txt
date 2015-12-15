The code was executed successfully on Windows 10 Command Line cmd.
All of the files have been compiled.
MVC - the main executable file which also contains Local Search Hill Climbing algorithm
Edge - the file the class for object Edge
APPR - the approximation algorithm
LSSA - the Local Search simulated annealing
BBEdge - Edge class used in branch-and-bound algorithm
BranchAndBound - contains branch-and-bound algorithm for MVC  
Graph - Graph class used in branch-and-bound algorithm
Utilities - helper class for branch-and-bound algorithm
Vertex - Vertex class used in branch-and-bound algorithm


The files are comiled by using commands
	javac Edge.java
	javac MVC.java
	etc
The code can be executed by using Command
	java MVC -<graphfilename.graph> -<method> -<Cutoff> -<Random seed> for two local search algorithms
	java MVC -<graphfilename.graph> -<method> -<Cutoff> for Approximation or Branch and Bound

-<graphfilename.graph> is any graph file name
-<method> is algorithm name which can be -LS1, -LS2, -Approx, -BnB
	-LS1 is Local search hill climbing
	--LS2 is Local search simulated annealing
	-Approx is Approximation 
	-BnB is Branch and bound
-<Cutoff> is the cutoff time, which can be any positive integer
-<Random seed> is the random seed, which can be any positive integer. This param is only appliable to local search.
	
For example
	java MVC -jazz.graph -LS1 -3600 -0
	java MVC -karate.graph -Approx -300
The command must have 3 or 4 proper arguments.
