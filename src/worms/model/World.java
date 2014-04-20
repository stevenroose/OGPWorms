package worms.model;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

public class World {
	
	/**
	 * @param 	width
	 * @param 	height
	 * @param 	passableMap
	 * @param 	random
	 * @post	new.getWidth = width
	 * @post	new.getHeight = height
	 * @post	new.getPassableMap = passableMap
	 * @effect	this.isValidHeight(new.getHeight())
	 * @effect	this.isValidWidth(new.getWidth())
	 * @throws 	IllegalArgumentException("Invalid width!")
	 * 			! this.isValidWidth(width)
	 * @throws 	IllegalArgumentException("Invalid height!")
	 * 			! this.isValidHeight(height)
	 */
	public World (double width, double height, boolean[][] passableMap, Random random) throws IllegalArgumentException {
		if(!this.isValidWidth(width))
			throw new IllegalArgumentException("Invalid width!");
		if(!this.isValidHeight(height))
			throw new IllegalArgumentException("Invalid height!");
		this.width = width;
		this.height = height;
		setPassableMap(passableMap);
	}

	@Basic
	public double getWidth(){
		return this.width;
	}
	
	@Basic
	public double getHeight(){
		return this.height;
	}
	
	/**
	 * @param 	width
	 * @return 	result == ((width >= lowerBoundOfWidth) && (width <= upperBoundOfWidth))
	 */
	private boolean isValidWidth(double width){
		return ((width >= lowerBoundOfWidth) && (width <= upperBoundOfWidth));
	}
	
	/**
	 * @param 	height
	 * @return 	result == ((height >= lowerBoundOfHeight) && (height <= upperBoundOfHeight))
	 */
	private boolean isValidHeight(double height){
		return ((height >= lowerBoundOfHeight) && (height <= upperBoundOfHeight));
	}
	
	private final static double lowerBoundOfWidth = 0;	
	
	private final static double lowerBoundOfHeight = 0;	
	
	private final static double upperBoundOfWidth = Double.MAX_VALUE;
	
	private final static double upperBoundOfHeight = Double.MAX_VALUE;	
	
	private final double width;
	
	private final double height;
	
	@Basic
	public boolean[][] getPassableMap(){
		return this.passableMap;
	}
	
	public int getWidthInPixels(){
		return this.getPassableMap()[0].length;
	}
	
	public int getHeightInPixels(){
		return this.getPassableMap().length;
	}
	
	public void setPassableMap(boolean[][] map) throws IllegalArgumentException{
		if (map.length == 0)
			throw new IllegalArgumentException("Empty map!");
		else if (map[0].length == 0)
			throw new IllegalArgumentException("Empty map!");
		this.passableMap = map;
	}
	
	private boolean[][] passableMap;
	
	@Model
	protected double getPixelWidth(){
		return (this.getWidth()/this.getWidthInPixels());
	}
	
	@Model
	protected double getPixelHeight(){
		return (this.getHeight()/this.getHeightInPixels());
	}
	
	/**
	 * @param 	center
	 * @param	radius
	 * @return	result == 
	 */
	public boolean isLocatedInWorld(Position center, double radius){
		double x = center.getX();
		double y = center.getY();
		int MaxPixelCoordinates[] = this.getPixelCoordinates(new Position((x + radius),(y + radius)));
		int MinPixelCoordinates[] = this.getPixelCoordinates(new Position((x - radius),(y - radius)));
		return ((MinPixelCoordinates[0] >= 0) && (MinPixelCoordinates[1] >= 0) && (MaxPixelCoordinates[0] <= this.getWidthInPixels()) && (MaxPixelCoordinates[1] <= this.getHeightInPixels()));
	}

	/**
	 * @return	result == position.getPixelCoordinates(this.getPixelWidth(), this.getPixelHeight())
	 * @throws	IllegalArgumentException("Object is not located in this world!")
	 * 			! this.isLocatedInWorld(position, 0)	
	 */
	public int[] getPixelCoordinates(Position position) throws IllegalArgumentException{
		if (!this.isLocatedInWorld(position, 0))
			throw new IllegalArgumentException("Object is not located in this world!");
		return position.getPixelCoordinates(this.getPixelWidth(), this.getPixelHeight());
	}
	
	private boolean isImpassablePosition(Position position) throws IllegalArgumentException{
		int[] pixelCoordinates = this.getPixelCoordinates(position);
		return !this.getPassableMap()[pixelCoordinates[0]][pixelCoordinates[1]];
	}
	
	private double getPositiveYCoordinateOfCircle(Position center, double x, double radius){
		return (Math.sqrt(Math.pow(radius, 2) - Math.pow((x - center.getX()), 2)) + center.getY());
	}
	
	private boolean isPassablePartOfPixeledHollowedCircle(Position center, double radiusOfCircle, double radiusOfVoid, boolean[] quadrantsToCheck){
		boolean isPassable = true;
		try{
			if (! (radiusOfVoid < radiusOfCircle))
				radiusOfVoid = 0;
			if (quadrantsToCheck.length != 4)
				quadrantsToCheck = new boolean[] {true,true,true,true};
			double centerX = center.getX();
			double centerY = center.getY();
			double centerXDoubled = 2 * centerX;
			double centerYDoubled = 2 * centerY;
			double x = centerX + radiusOfVoid;
			double y = centerY + radiusOfVoid;
			double xMax = centerX + radiusOfCircle;
			double yMax = centerY + radiusOfCircle;
			double pixelWidth = this.getPixelWidth();
			double pixelHeight = this.getPixelHeight();
			while (isPassable){
				while (x < xMax){
					while (y < yMax){
						if ((this.isImpassablePosition(new Position(x,y)) && quadrantsToCheck[0]) || (this.isImpassablePosition(new Position(x,(centerYDoubled - y))) && quadrantsToCheck[3]) || (this.isImpassablePosition(new Position((centerXDoubled - x),y)) && quadrantsToCheck[1]) || (this.isImpassablePosition(new Position((centerXDoubled - x),(centerYDoubled - y))) && quadrantsToCheck[2]))
							isPassable = false;
						y += pixelHeight;
					}
					x += pixelWidth;
					y = this.getPositiveYCoordinateOfCircle(center, x, radiusOfVoid);
					yMax = this.getPositiveYCoordinateOfCircle(center, x, radiusOfCircle);
				}	
			}
		}
		catch (IllegalArgumentException exc){
			isPassable = false;
		}
		return isPassable;
	}
	
	private boolean isPassablePartOfPixeledRadiusOfCircle(Position center, double radiusOfCircle, boolean[] quadrantsToCheck){
		boolean isPassable = true;
		try{
			if (quadrantsToCheck.length != 4)
				quadrantsToCheck = new boolean[] {true,true,true,true};
			double centerX = center.getX();
			double centerY = center.getY();
			double centerXDoubled = 2 * centerX;
			double centerYDoubled = 2 * centerY;
			double x = centerX;
			double y = centerY + radiusOfCircle;
			double xMax = centerX + radiusOfCircle;
			double stepSize = Math.min(this.getPixelWidth(), this.getPixelHeight());
			while (x < xMax){
				if ((this.isImpassablePosition(new Position(x,y)) && quadrantsToCheck[0]) || (this.isImpassablePosition(new Position(x,(centerYDoubled - y))) && quadrantsToCheck[3]) || (this.isImpassablePosition(new Position((centerXDoubled - x),y)) && quadrantsToCheck[1]) || (this.isImpassablePosition(new Position((centerXDoubled - x),(centerYDoubled - y))) && quadrantsToCheck[2])){
					isPassable = false;
					break;
				}
				x += stepSize;
				y = this.getPositiveYCoordinateOfCircle(center, x, radiusOfCircle);
			}	
		}
		catch (IllegalArgumentException exc){
			isPassable = false;
		}
		return isPassable;
	}
	
	public boolean isPassable(Position center, double radius){
		boolean[] fullCircle = {true,true,true,true};
		return this.isPassablePartOfPixeledHollowedCircle(center, radius, 0, fullCircle);
	}
	
	private boolean isAdjacentToImpassableTerrain(Position center, double radius, boolean[] quadrants){
		boolean[] fullCircle = {true,true,true,true};
		boolean isPassable = this.isPassablePartOfPixeledHollowedCircle(center, radius, 0, fullCircle);
		boolean isAdjacent = ! this.isPassablePartOfPixeledHollowedCircle(center, (radius * 1.1), radius, quadrants);
		return (isPassable && isAdjacent);
	}
	
	public boolean isAdjacentToImpassableTerrain(Position center, double radius){
		boolean[] fullCircle = {true,true,true,true};
		return this.isAdjacentToImpassableTerrain(center, radius, fullCircle);
	}
	
	protected boolean isAdjacentToImpassableFloor(Position center, double radius){
		boolean[] bottomHalfOfCircle = {false,false,true,true};
		return this.isAdjacentToImpassableTerrain(center, radius, bottomHalfOfCircle);
	}
	
	protected boolean isAdjacentToImpassableCeiling(Position center, double radius){
		boolean[] topHalfOfCircle = {true,true,false,false};
		return this.isAdjacentToImpassableTerrain(center, radius, topHalfOfCircle);
	}

	/**
	 * @return	
	 */
	public int getNbGameObjects() {
		return objects.size();
	}
	
	private void setNbGameObjects() {
		
	}
	
	/**
	 * 
	 * @return	result == 
	 * 				for each object in GameObject
	 * 					( if (this.hasAsGameObject(object))
	 * 						then canHaveAsGameObject(object)
	 * 						&& (object.getWorld() == this) )	
	 */
	
	@Raw
	public boolean hasProperGameObjects(){
	for (GameObject object: this.objects){
		if (! canHaveAsGameObject(object)){
			return false;
		}
		if (object.getWorld() != this){
			return false;
		}
	}
	return true;
	
	}
	
	/**
	 * @param	object
	 * 			The object to check.
	 */
	@Basic @Raw
	public boolean hasAsGameObject(GameObject object){
		return this.objects.contains(object);
	}
	
	/**
	 * @param	object
	 * 			The object to be removed
	 * @post	! new.hasAsGameObject(object)
	 * @post	((new object).getWorld() == this) 				
	 * @throws	IllegalArgumentException
	 * 			(! hasAsGameObject)
	 */
	public void removeAsGameObject(GameObject object) throws IllegalArgumentException {
		if (hasAsGameObject(object)) {
			this.objects.remove(object);
		}
		else throw new IllegalArgumentException("This object does not belong to this world");
		
	}
	
	
	/**
	 * @invar	objects != null
	 * @invar	for each gameobject in objects:
	 * 			( (object == null)
	 * 			|| canHaveAsGameObject(object) )
	 */
	private final List<GameObject> objects = new ArrayList<GameObject>();
	
	public List<GameObject> getObjects() {
		return objects;
	}
	
	/**
	 * 
	 * @param 	object
	 * 			The game object to be added
	 * @post	new.hasAsGameObject(object)
	 * @post	(new object).getWorld() == this
	 * @throws	IllegalArgumentException
	 * 			(! canHaveAsGameObject(object))
	 * @throws 	IllegalArgumentException
	 * 			(object.getWorld() != null)
	 * @throws 	IllegalArgumentException
	 * 			(isStarted == true && ( object instanceof Worm || object instanceof Food ))
	 */
	public void addAsGameObject(GameObject object) throws IllegalArgumentException {
		if (! canHaveAsGameObject(object)) 
			throw new IllegalArgumentException("This is not a proper object for this world");
		if (object.getWorld() != null)
			throw new IllegalArgumentException("This object appears in another world");
		if (isStarted == true && ( object instanceof Worm || object instanceof Food )){
			throw new IllegalArgumentException("Cannot add worms or worm food during the game");
		}
		else
			this.objects.add(object);
			object.setWorld(this);
	}
	
	/**
	 * 
	 * @param 	object
	 * 			The game object to be added.
	 * @param	x
	 * 			the x coordinate at which the object is to be added.
	 * @param	y
	 * 			the y coordinate at which the object is to be added. 
	 * @post	new.hasAsGameObject(object)
	 * @post	(new object).getWorld() == this
	 * @post	(new object).getX() == x
	 * 			(new object).getY() == y
	 * @throws	IllegalArgumentException
	 * 			(! canHaveAsGameObject(object))
	 * @throws 	IllegalArgumentException
	 * 			(object.getWorld() != null)
	 * @throws 	IllegalArgumentException
	 * 			(isStarted == true && ( object instanceof Worm || object instanceof Food ))
	 * 			
	 */
	public void addAsGameObject(GameObject object, double x, double y) throws IllegalArgumentException {
		if (! canHaveAsGameObject(object)) 
			throw new IllegalArgumentException("This is not a proper object for this world");
		if (object.getWorld() != null)
			throw new IllegalArgumentException("This object appears in another world");
		if (isStarted == true && ( object instanceof Worm || object instanceof Food )){
			throw new IllegalArgumentException("Cannot add worms or worm food during the game");
		}
		else
			this.objects.add(object);
			object.setWorld(this);
			object.setPosition(new Position(x,y));
	}
	

	
	
	/**
	 * Check whether this world can have the given game object as one of the game objects attached to it.
	 * 
	 * @param 	object
	 * 			The game object to check.
	 * @return	if (object == null)
	 * 				then result == false
	 * 			if (! object.getWorld() == null)
	 * 				then result == false
	 * 			else result == 
	 * 				(! objects.equals(object))
	 */
	public boolean canHaveAsGameObject(GameObject object) {
		return 	( (object != null)
				&& (object.getWorld() == null)
				&& (! objects.equals(object)));
	}
	
	
	/**
	 * 
	 * @param 	gameObjects
	 * 			The collection of game objects to examine.
	 * @param 	method
	 * 			The method to invoke against all game objects.
	 * @return 	for each object in (Object union {null})
	 * 				result.contains(object) == 
	 * 				for some gameObject in gameObjects:
	 * 					method.invoke(object).equals(object)
	 * @throws 	IllegalArgumentException
	 * 			gameObjects == null
	 * @throws 	IllegalArgumentException
	 * 			! Arrays.AsList(GameObject.class.getMethods()).contains(method)
	 * @throws 	IllegalArgumentException
	 * 			(method.isVarArgs()) ? 
					(method.getParameterTypes().length != 1) :
					(method.getParameterTypes().length != 0) 
	 * @throws 	IllegalArgumentException
	 * 			method.getReturnType() == void.class
	 * @throws 	InvocationTargetException
	 
	public static List<GameObject> getAllObjectsFrom(List<GameObject> gameObjects, Method method) throws IllegalArgumentException, InvocationTargetException {
		List<GameObject> result = new ArrayList<GameObject>();
		if (gameObjects == null){
			throw new IllegalArgumentException();
		}
		if (! Arrays.asList(GameObject.class.getMethods()).contains(method)){
			throw new IllegalArgumentException();
		}
		if ( (method.isVarArgs()) ? 
				(method.getParameterTypes().length != 1) :
				(method.getParameterTypes().length != 0) )
			throw new IllegalArgumentException();
		if (method.getReturnType() == void.class) 
			throw new IllegalArgumentException() ;
		else {
			for (GameObject gameObject: gameObjects)
				try {
					result.add( method.invoke(gameObject));
				}	catch (IllegalAccessException exc) {
					assert false;
					}
			}	

		return result;
				
	}
	*/
	public List<GameObject> getAllObjectsFrom(String className, List<GameObject> gameObjects){
		List<GameObject> result = new ArrayList<GameObject>();
			for (GameObject gameObject: gameObjects)
				if( gameObject.getClass().getName() == className){
					result.add(gameObject);
				}
			return  result;
	}
	
	
	private final List<Team> teams = new ArrayList<Team>();
	/**
	 * Return the teams of this world.
	 */
	public List<Team> getTeams() {
		return this.teams;
	}
	
	/**
	 * Remove the given team from this world.
	 * 
	 * @param 	team
	 * 			The team to be removed.
	 * @post	! new.getTeams().contains(team)	
	 */
	public void removeAsTeam(Team team) {
		teams.remove(team);
		}
	
	/**
	 * Check whether the given team is a valid team for this world.
	 * @param 	team
	 * 			The team to be checked.
	 * @return	result == (! teams.contains(team) && teams.size() <= 9 && team.isValidName(team.getName()))
	 */
	public boolean canHaveAsTeam(Team team) {
		return (! teams.contains(team) && teams.size() <= 9 && team.isValidName(team.getName()));
	}
	
	/**
	 * Add the given team to this world.
	 * @param 	team
	 * 			The team to be added to this world.
	 * @post	new.getTeams().contains(team)
	 * @throws 	IllegalArgumentException
	 * 			(! canHaveAsTeam(team))
	 */
	public void addAsTeam(Team team) throws IllegalArgumentException {
		if (! canHaveAsTeam(team)){
			throw new IllegalArgumentException("invalid team");
		}
		else teams.add(team);
	}

	
	public void startGame(){
		isStarted = true;
	}
	
	public boolean isStarted(){
		return isStarted;
	}
	
	private boolean isStarted;
	
	protected List<Food> overlapWithFood(Position p, double radius){
		String className = Food.class.getName();
		List<GameObject> result = new ArrayList<GameObject>();
		List<Food> resultFood = new ArrayList<Food>();
		 result = this.getAllObjectsFrom(className, this.getObjects());
			for (GameObject object: result){
				try { Food food = (Food) object;
				if (food.partialOverlapWith(p, radius))
					resultFood.add(food);
				}	catch (ClassCastException exc) {
					assert false;
					}
			}
	
			return resultFood;
	}
	
	protected List<Worm> overlapWithWorm(Position p, double radius){
		String className = Worm.class.getName();
		List<GameObject> result = new ArrayList<GameObject>();
		List<Worm> resultWorm = new ArrayList<Worm>();
			result = this.getAllObjectsFrom(className, this.getObjects());
			for (GameObject object: result){
				try { Worm worm = (Worm) object;
				if (worm.partialOverlapWith(p, radius))
					resultWorm.add(worm);
				}	catch (ClassCastException exc) {
					assert false;
					}
			
			}
			return resultWorm;
	}
	
}


