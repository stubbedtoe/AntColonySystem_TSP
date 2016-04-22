# Ant Colony System
## A Travelling Salesman Application
### Andrew Healy, April 2015
Java Implementation of the Ant Colony System algorithm as described in Swarm Intelligence (Oxford University Press, 1999, pp.51) - as applied to the [Travelling Salesman Problem](https://en.wikipedia.org/wiki/Travelling_salesman_problem). This solution came top among students and demonstrators for the 2015 Travelling Salesman Competition (CS211 Maynooth University).  

### High-level overview
The idea behind this algorithm is to choose the edge (between towns) with the strongest level of *pheromone* left behind by ants on previous tours. The *colony* consists of a number of ants. When each ant has completed a Tour, edges of the best two Tours are updated with fresh pheromone while the pheromone level of other edges decays over time. This process is repeated a set number of times. This implementation performs [3-opt](https://en.wikipedia.org/wiki/3-opt) on each Tour after it has be completed and before it is evaluated. 

### Tunable parameters
__The following parameters can be changed via command-line arguements at runtime__:
- `name` *arg 0* (__default: `0`__ ): controls the dataset to use. Two are provided:  
 - `0` loads `1000airports.txt` which is a very big TSP instance using 1000 locations worldwide
 - `1` loads `towns.csv` which is a list of 80 towns in Ireland. Tours using this dataset can be validated using [this tool](http://www.cs.nuim.ie/~ahealy/tsp_checker/) I built.
- `Q_0` *arg 1* (0.0-1.0 __default: `0.8`__ ): controls the amount of exploration: higher values restrict the probaility that the next town will be chosen with a random probability function (as opposed to the highest level of pheromone)
- `T_MAX` *arg 2* (500-20000 __default: `10000`__): the number of times the colony chooses a *best* path.

The other most relevant parameters are defined at the top of `ACS_TSP.java`:
- `BETA (double)` (__default: `2.0`__) : a constant used to boost the signal of the pheromone
- `PO (double)` (__default: `1.0`__) : a constant governing pheromone decay
- `m (int)` (__default: `15`__) : the number of ants in the colony
- `cl (int)` (__default: `15`__): size of *candiate list* (the prefered towns to visit from any given town)
