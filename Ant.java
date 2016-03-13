public class Ant{
	
	public Town start;
	public Tour tour;

	public Ant(){
		start = ACS_TSP.towns[(int)(Math.random()*ACS_TSP.n)];
		tour = new Tour(start);
	}

	public void visitClosest(){
		
	}

}