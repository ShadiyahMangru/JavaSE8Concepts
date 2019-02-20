import java.io.*;
import java.util.*;
import java.util.function.*;

class HockeyPlayer{
	//fields
	private String lastName;
	private String position;
	private int jersey;
	private String team;

	//constructors
	//initialize HockeyPlayer object without a specific team
	public HockeyPlayer(String lastName, String position, int jersey){
		this.lastName = lastName;
		this.position = position;
		this.jersey = jersey;
		setTeam("undefined");
	}
	
	//initialize HockeyPlayer object with a specific team
	public HockeyPlayer(String lastName, String position, int jersey, String team){
		this.lastName = lastName;
		this.position = position;
		this.jersey = jersey;
		this.team = team;
	}

	//setter
	public void setTeam(String team){
		this.team = team;	
	}
	
	//getters
	public String getLastName(){
		return lastName;
	}
	
	public String getPosition(){
		return position;
	}	
	
	public int getJersey(){
		return jersey;
	}
	
	public String getTeam(){
		return team;	
	}
}

class Skater extends HockeyPlayer{
	//fields
	private int goals;
	private int shots;
	private float shootingPercent;
	
	//constructor
	public Skater(HockeyPlayer hp, int goals, int shots){
		super(hp.getLastName(), hp.getPosition(), hp.getJersey());
		this.goals = goals;
		this.shots = shots;
		this.shootingPercent = (float)0;
	}
	
	//setters
	public void setGoals(int goals){
		this.goals = goals;	
	}
	
	public void setShots(int shots){
		this.shots = shots;	
	}
	
	public void setShootingPercent(float shootingPercent){
		this.shootingPercent = shootingPercent;	
	}
	
	//getters
	public int getGoals(){
		return goals;	
	}
	
	public int getShots(){
		return shots;	
	}
	
	public float getShootingPercent(){
		return shootingPercent;	
	}
}

class Roster{
	//fields
	private ArrayList<HockeyPlayer> roster;
	
	//constructor
	public Roster(){	
		setRoster();
	}
	
	//setter
	public void setRoster(){
		roster = new ArrayList<>();
		try{
			//.txt file that stores data needed for calculations
			File file = new File("C:\\Users\\593476\\Desktop\\Java Programs\\roster2018Goals_Shots_Stats2_19_2019.txt"); 
    			Scanner sc = new Scanner(file); 
    			int jersey = 0;
    			int goals = 0;
    			int shots = 0;
    			String [] gs = {"", ""};
    			while (sc.hasNextLine()) {
    				String name = sc.nextLine().trim();
    				String position  = sc.nextLine().trim();
    				jersey = Integer.parseInt(sc.nextLine().trim());
    				if(!position.equals("Goalie")){
    					gs = sc.nextLine().trim().split(":");
    					goals = Integer.parseInt(gs[0]);
    					shots = Integer.parseInt(gs[1]);
    					HockeyPlayer hp = new HockeyPlayer(name, position, jersey);
    					Skater s = new Skater(hp, goals, shots);
    					roster.add(s);
    					sc.nextLine();   //skip *
				}
				else{
					HockeyPlayer hp = new HockeyPlayer(name, position, jersey);
    					roster.add(hp);
    					sc.nextLine();	 //skip 0:0 for Goalies
					sc.nextLine();   //skip *
				}
    			}
    		}
    		catch(Exception e){
    			System.out.println("Exception: " + e);
    		}
	}
	
	//getters
	public int getRosterCount(){
		return roster.size();	
	}
	
	public HockeyPlayer getHockeyPlayer(int index){
		return roster.get(index);	
	}
}

interface SkaterComparisons{
	//this method sorts in descending order by goals scored; 
	//in the event of a tie, players are sorted in ascending order by last name.
	//parameters must accept a narrowing cast to Skater for valid results
	public static int compareByGoalsThenName(HockeyPlayer h1, HockeyPlayer h2){
		try{
			Skater s1 = (Skater)h1;
			Skater s2 = (Skater)h2;
			if(s2.getGoals() - s1.getGoals() != 0){
				return s2.getGoals() - s1.getGoals();	
			}
			return s1.getLastName().compareTo(s2.getLastName());	
		}
		catch(ClassCastException cce){
			System.out.println("Exception: " + cce);	
		}
		return 0;
	}	
}

interface Lambdas{
	//the BiFunction Functional Interface turns two parameters into a value of a potentially different type and returns it
	//this method accepts a player's goals and shots as parameters, and returns the player's shooting percentage
	public static BiFunction<Integer, Integer, Float> shootPer = (g, s) -> {
		if(s == 0){
			return (float)0;
		}
		return ((float)g / (float)s)*100;		
	};
	
	//the BiConsumer Functional Interface accepts/manipulates two parameters, but does not return anything
	//this method accepts a shooting percentage and a skater, and sets that skater object's shooting percentage field
	public static BiConsumer<Float, Skater> setShootPer = (sp, sk) -> {
		sk.setShootingPercent(sp);	
	};
	
	//the Predicate Functional Interface may be used to test a condition (often used when filtering or matching)
	//this method accepts a HockeyPlayer parameter and returns true if the HockeyPlayer is not a Goalie
	public static Predicate<HockeyPlayer> filterOutGoalies = hp -> {
		if(!hp.getPosition().equals("Goalie")){
			return true;	
		}
		return false;	
	};
	
	//this method accepts a HockeyPlayer parameter and returns true if the HockeyPlayer wears an even-numbered jersey
	public static Predicate<HockeyPlayer> evenNumberPlayers = hp -> {
		if(hp.getJersey() % 2 == 0){
			return true;	
		}
		return false;	
	};
	
	//the Supplier Functional Interface may be used to generate or supply values without taking any input
	//this method takes no parameters and returns the String "WSH".
	public static Supplier<String> assignTeam = () -> {
		return "WSH";
	};
}

public class Stream{
	//Comparator that sorts HockeyPlayers (who are Not Goalies) by goals then lastname, written with a Java SE 8 method reference
	static Comparator<HockeyPlayer> byGoalsThenName = SkaterComparisons :: compareByGoalsThenName;
	
	//this method relies on Supplier F.I. to set team field of hockeyplayer object, Predicate F.I. to filter out goalies, 
	//BiFunction F.I. to calculate skater's shooting percentage, and BiConsumer F.I. 
	//to set the shootingPercent field of the skater object (in case this value needed later).
	private static void printSKShootPercent(HockeyPlayer hp){
		hp.setTeam(Lambdas.assignTeam.get()); //calls Supplier
		if(Lambdas.filterOutGoalies.test(hp)){ //calls Predicate
			Skater s = (Skater)hp; //narrowing cast of HockeyPlayer object to a Skater object
			Float calcSP = Lambdas.shootPer.apply(s.getGoals(), s.getShots()); //calls BiFunction
			Lambdas.setShootPer.accept(calcSP, s); //calls BiConsumer
			System.out.println(String.format("| %-4s | %-15s | %-4s | %-7s | %-15s |", s.getTeam(), s.getLastName(), s.getJersey(), s.getGoals() , s.getShootingPercent()));
		}
	}
	
	//main method that prints (to console) the sorted stat chart of the current roster
	public static void main(String... args){
		Roster r = new Roster();
		ArrayList<HockeyPlayer> team = new ArrayList<>(); //initialize an ArrayList 
		for(int i = 0; i< r.getRosterCount(); i++){				//with HockeyPlayer objects of current roster
			team.add(r.getHockeyPlayer(i));
		}
		
		System.out.println("\n******* Goals Scored By WSH Non-Goalies Who Wear Even-Numbered Jerseys (since 2/19/2019) *******\n");
		System.out.println(String.format("| %-4s | %-15s | %-4s | %-7s | %-15s |", "Team", "Player", "#", "Goals", "Shooting %"));
		System.out.println("---------------------------------------------------------------");
		team.stream()
		.filter(Lambdas.filterOutGoalies :: test) //calls Predicate w/a method reference
		.filter(Lambdas.evenNumberPlayers :: test) //calls Predicate w/a method reference
		.sorted(byGoalsThenName) //sort stream with a Comparator!
		.forEach(Stream :: printSKShootPercent); //calls printSKShootPercent with a method reference
		System.out.println("\n****************************************************************");		
	}
}
