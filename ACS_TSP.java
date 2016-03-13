import java.util.Random;

public class ACS_TSP{
	
	/* chosen constants */
	public static final double BETA = 2.0;
	public static final double PO = 0.1;
	public static double Q_0;
	public static int T_MAX;
	public static int cl = 15;
	public static Town [] towns;
	public static int n;
	public static final int m = 15;
	public static double TAU_0;
	public static double THRESH = 0.000001;

	public static double [][] adjacencyMatrix; 
	public static double [][] pheromone;

	public static void main(String[] args) {

		// load the town information from csv file using the given FileIO class
		FileIO reader = new FileIO(); 
		String name = "towns.csv";
		Q_0 = 0.8;
		T_MAX = 10000;
	
 		for(int i=0; i<args.length; i++){
 			try{
	 			if (i==0 && args[i].equals("0"))
	 				name = "1000airports.txt";
	 			else if (i==0 && args[i].equals("1"))
	 				name = "towns.csv";
	 			else if	(i==1)
	 				Q_0 = Double.parseDouble(args[i]);
	 			else if (i==2)
	 				T_MAX = Integer.parseInt(args[2]);
	 			else
	 				printhelp();
	 		}catch (Exception e){
	 			printhelp();
	 			System.exit(0);
	 		}	 
 		}

 		System.out.println("\n"+T_MAX+" times; Q_0="+Q_0+"; on "+name+"\n");
 		
 		String[] twns = reader.load(name);

 		n = twns.length;

 		towns = new Town [n];

 		for(int i=0; i<n; i++){
 			String[]parts = twns[i].split(",");
 			towns[i] = new Town(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
 		}

 		adjacencyMatrix = new double[n][n];
 		pheromone = new double[n][n];

 		//fill the adjacency matrix
 		for(int i=0; i<n; i++){
 			for(int j=0; j<n; j++){
 				if(i != j){
 					adjacencyMatrix[i][j] = towns[i].distanceTo(towns[j]);
 				}else
 					adjacencyMatrix[i][j] = 0.0;
 			}
 		}

 		//a pretty good tour to start with
 		Tour nn = Tour.nearestNeighbour();

 		double shortest = nn.distance;
 		double secondBest = Double.MAX_VALUE;

 		Tour globalBest = nn;
 		double globalShortest = shortest;

 		Tour best = nn;
 		Tour second = nn;
 		TAU_0 = Math.pow(n*nn.distance, -1);
 		Town [][] candidateLists = new Town [n][cl];

 		for(int i=0; i<n; i++){
 			for(int j=0; j<n; j++){
 				if(i != j)
 					pheromone[i][j] = TAU_0;
 			}
 			candidateLists[i] = towns[i].candidates();
 		}

		Random gen = new Random();

		for(int t=0; t<T_MAX; t++){
 			//all my tours
 			Tour [] t_k = new Tour[m];

 			for(int k=0; k<m; k++){
 				//all my ants

 				//build tour Tkt by applying n-1 times the following steps
 				t_k[k] = new Tour(towns[(int)(Math.random()*n)]);
 				

				for(int i=0; i<n-1; i++){

					Town [] candidates = candidateLists[t_k[k].current().id-1];
					Town next;

					if(numUnvisited(t_k[k], candidates) > 0){

						Town [] unvisited = getUnvisited(t_k[k], candidates);

						double q = gen.nextDouble();;

						try{
							if(args[3].equals("-r"))
								q = Math.random();	
						}catch (Exception e){
						
						}

						int index;

						if(q <= Q_0)
							index = maxArg(unvisited, t_k[k].current());
						else
							index = probability(unvisited, t_k[k].current()); //probability formula
						
						
						next = unvisited[index];

					}else{

						next = t_k[k].nextClosest();
					}

					applyPheromone(t_k[k].current(), next);
					t_k[k].visit(next);				

				}

				t_k[k].opt3();

				//for all my ants see if they produces a better tour
				if(shortest - t_k[k].distance >= THRESH){
 					shortest = t_k[k].distance;
 					best = t_k[k];
 					System.out.println("new best \t"+shortest+"\t"+t);
 				}else if(secondBest - t_k[k].distance >= THRESH){
 					secondBest = t_k[k].distance;
 					second = t_k[k];
 					/*
 				}else if(t_k[k].distance > longest){
 					longest = t_k[k].distance;
 					worst = t_k[k];
 					*/
 				}
 			}

 			//update the best 2 tours
 			for(int i=0; i<n; i++){
 				int from = best.visited[i].id-1;
 				int to = best.visited[i+1].id-1;
 				double old = pheromone[from][to];
 				pheromone[from][to] = (1 - PO) * old + (PO * (1.0 / shortest));
 				pheromone[to][from] = pheromone[from][to];
 				
 				from = second.visited[i].id-1;
 				to = second.visited[i+1].id-1;
 				old = pheromone[from][to];
 				pheromone[from][to] = (1 - PO) * old + (PO * (1.0 / shortest));
 				pheromone[to][from] = pheromone[from][to];
 				
 			}//end update


			}//end t max



 		System.out.println("\n"+best+"\n");

	}//end main

	public static void printhelp(){
		System.out.println("Usage:\n\tOptionally can use up to 3 arguments eg: java ACS_TSP 0 0.7 10000");
		System.out.println("\narg1=0\tuse 1000airports.txt; 1000 towns around the globe. A very big TSP problem");
		System.out.println("arg1=1\tuse towns.csv; 80 towns around the Ireland. Solutions can be visualised with my tool: http://www.cs.nuim.ie/~ahealy/tsp_checker/");
		System.out.println("\narg2=(0.0 <= num < 1.0)\tthis parameter controls the amount of exploration");
		System.out.println("\narg3=~(5000 <= num < 20000)\tthis parameter is the number of iterations the colony finds a path");
		

	}

	public static int numUnvisited(Tour tr, Town [] candidates){
		int x = 0;
		for(Town t : candidates){
			if(!tr.visited(t))
				x++;
		}
		return x;
	}

	public static Town [] getUnvisited(Tour tr, Town [] candidates){
		Town [] t = new Town[numUnvisited(tr, candidates)];
		for(int i=0, j=0; i<candidates.length; i++){
			if(!tr.visited(candidates[i])){
				t[j] = candidates[i];
				j++;
			}		
		}
		return t;
	}

	public static void applyPheromone(Town from, Town to){
		double old = pheromone[from.id-1][to.id-1];
		pheromone[from.id-1][to.id-1] = (1 - PO) * old + (PO * TAU_0);
		pheromone[to.id-1][from.id-1] = pheromone[from.id-1][to.id-1]; 
	}

	public static int maxArg(Town [] candiateList, Town i){
	
		double d = -10.0;
		int index = -1;
		for(int j=0; j<candiateList.length; j++){
			double arg = ph(i, candiateList[j]);
			if(arg > d){
				d = arg;
				index = j;
			}
		}
		return index;
	}

	public static double summation(Town [] candiateList, Town i){
		double d = 0.0;
		for(int j=0; j<candiateList.length; j++){
			d += ph(i, candiateList[j]);
		}
		return d;
	}

	public static double ph(Town i, Town u){
		return ( pheromone[i.id-1][u.id-1] * Math.pow(1.0/adjacencyMatrix[i.id-1][u.id-1], BETA) );
	}

	public static int probability(Town [] candiateList, Town i){

		int SIZE = 1000;
		int [] pot = new int[SIZE];

		double summation = summation(candiateList, i);

		int start = 0;

		//System.out.println("\n");

		for(int j=0; j<candiateList.length; j++){
			
			double p = ph(i, candiateList[j])/summation;

			int add = (int)(SIZE*p);

			/*
			if(t==T_MAX-1){
				System.out.println(j+": adding "+candiateList[j].name+" "+add+" times");
			}
			*/

			for(int k=0; k<add && start<SIZE; k++){
				pot[start] = j;
				start++;
			}
			
		}

		int r = (int)(Math.random()*SIZE);

		return pot[r];

	}

}