package worms.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import worms.model.programs.Program;
import be.kuleuven.cs.som.annotate.*;

/**
 * 
 * A class of worms involving a position, a radius, a lower bound of that radius, a direction, a name, a current number of action points and a current number of hit points.
 * 
 * @Invar	The name of each worm is a valid name.
 * 		|	isValidName(getName())
 * @Invar	Each worm has a valid number of action points.
 * 		|	0 <= getNumberOfActionPoints() <= getMaxNumberOfActionPoints()
 * @Invar	Each worm can has a valid number of hit points.
 * 		|	0 <= getNumberOfHitPoints() <= getMaxNumberOfHitPoints()
 * @Invar	Each worm has a proper team
 * 		|	hasProperTeam()
 * @Invar	Each worm has proper weapons
 * 		|	hasProperWeapons()
 * 
 * @version 2.0
 * @author Jonas Thys & Jeroen Reinenbergh
 * 
 */

public class Worm extends MovableGameObject {

	/**
	 * Initialize this new worm with given position, given radius, given direction and given name.
	 * 
	 * @param	position
	 * 			The position of this worm, which consists of an x-coordinate and a y-coordinate
	 * @param	radius
	 * 			The radius of the spherical body of the worm expressed in meters.
	 * @param	direction
	 * 			The direction towards which the worm faces expressed in radians.
	 * @param	name
	 * 			The name of this worm
	 * @Pre		The given direction is a valid direction for any worm
	 * 		|	this.isValidDirection(direction)
	 * @post	The new worm is initialized as a new movable game object with given position, given direction, given radius and 0.25 as lower bound of this radius.
	 * 		|	new.getPosition = position
	 * 		|	new.getRadius() = radius
	 * 		|	new.getLowerBoundOfRadius() = 0.25
	 * 		|	new.getDirection() = direction
	 * @post 	The new name of this worm is equal to the given name.
	 * 		|	new.getName() = name
	 * @post	The new current number of action points of this worm is equal to the maximum number of action points of this worm.
	 * 		|	new.getNumberOfActionPoints() = new.getMaxNumberOfActionPoints()
	 * @post	The new current number of hit points of this worm is equal to the maximum number of hit points of this worm.
	 * 		|	new.getNumberOfHitPoints() = new.getMaxNumberOfHitPoints()
	 * @effect	This worm is alive
	 * 		|	new.isAlive() = true
	 * @effect	The weapons yielded by this worm are a Bazooka and a Rifle
	 * 		|	for (Weapon weapon : new.getAllWeapons())
	 * 		|		weapon.getName().equals("Bazooka") || weapon.getName().equals("Rifle")
	 * 		|	new.getAllWeapons().size() = 2
	 * @effect	The new name of this worm is a valid name for any worm.
	 * 		|	isValidName(new.getName())
	 * @effect	This worm can have the new number of action points as its current number of action points.
	 * 		|	0 <= new.getNumberOfActionPoints <= new.getMaxNumberOfActionPoints
	 * @effect	This worm can have the new number of hit points as its current number of hit points.
	 * 		|	0 <= new.getNumberOfActionPoints <= new.getMaxNumberOfActionPoints
	 * @effect	The new position of this worm is a valid position for any worm
	 * 		|	isValidPosition(new.getPosition())
	 * @effect	This worm can have the new radius as its radius.
	 * 		|	canHaveAsRadius(new.getRadius())
	 * @effect	The new lower bound of the radius of this worm is a valid lower bound for any worm.
	 * 		|	isValidLowerBoundOfRadius(new.getLowerBoundOfRadius())
	 * @effect	The new x-coordinate of this worm is a valid x-coordinate for any worm.
	 * 		|	getPosition.isValidCoordinate(new.getX())
	 * @effect	The new y-coordinate of this worm is a valid y-coordinate for any worm.
	 * 		|	getPosition.isValidCoordinate(new.getY())
	 * @effect	The new direction of this worm is a valid direction for any worm.
	 * 		|	this.isValidDirection(new.getDirection())
	 * @throws 	IllegalArgumentException("Name is not valid!")
	 * 			The given name is not a valid name for any worm.
	 * 		|	! isValidName(name)
	 * @throws 	IllegalArgumentException("Invalid radius!")
	 * 			This worm cannot have the given radius as its radius.
	 * 		|	! canHaveAsRadius(radius)
	 * @throws	IllegalArgumentException("Invalid lower bound of radius!")
	 * 			The given lower bound is not a valid lower bound for the radius of any worm.
	 * 		|	! isValidLowerBoundOfRadius(lowerBound)
	 * @throws 	IllegalArgumentException("Invalid x-coordinate!")
	 * 			The given x-coordinate is not a valid coordinate for any worm.
	 * 		|	! getPosition().isvalidCoordinate(x)
	 * @throws 	IllegalArgumentException("Invalid y-coordinate!")
	 * 			The given y-coordinate is not a valid coordinate for any worm.
	 * 		|	! getPosition().isValidCoordinate(y)
	 * @throws	IllegalArgumentException("Invalid position!")
	 * 			The given position is not a valid position for any worm.
	 * 		|	! isValidPosition(new.getPosition())
	 */
	
	public Worm (Position position, double radius, double direction, String name, Program program) throws IllegalArgumentException {
		super(position ,radius ,0.25, direction);
		this.setRadius(radius);
		this.setName(name);
		this.setNumberOfActionPoints(this.getMaxNumberOfActionPoints());
		this.setNumberOfHitPoints(this.getMaxNumberOfHitPoints());
		this.setAlive(true);
		this.distributeWeapons();
		this.setProgram(program);
	}
	/**
	 * Return the selected weapon of this worm.
	 * @return	this.getSelectedWeapon().getName()
	 */
	public String getSelectedWeaponsName(){
		return this.getSelectedWeapon().getName();
	}
	
	/**
	 * 
	 * @param 	propulsionYield
	 * 			The propulsion yield of the shot.
	 * @effect	new.getWeapon().getAmmo = this.getWeapon.getAmmo - 1
	 * @effect	new.getWorm().getNumberOfActionPoints() = this.getWorm().decreaseNumberOfActionPointsBy(this.getCostInActionPoints())
	 * @effect	(new Projectile(new Position(this.getWorm().getX() + ((1 + relativeDistanceFromWorm) * this.getWorm().getRadius() * Math.cos(this.getWorm().getDirection())),this.getWorm().getY() + ((1 + relativeDistanceFromWorm) * this.getWorm().getRadius() * Math.sin(this.getWorm().getDirection()))), this.getWorm().getDirection(), this.getRadiusOfProjectile(), this.getInitialForceOfProjectile(propulsionYield))
	 * 
	 */
	public void shoot(int propulsionYield){
		this.getSelectedWeapon().shoot(propulsionYield);
	}
	
	/**
	 * Return all weapons attached to this worm.
	 */
	protected List<Weapon> getAllWeapons(){
		return weapons;
	}

	/**
	 * Check whether the given weapon is a valid weapon for this worm.
	 * @param 	weapon
	 * 			The weapon to be checked.
	 * @return	True if and only if the weapon is not equal to null.
	 * 			result == (weapon != null)
	 */
	protected boolean canHaveAsWeapon(Weapon weapon){
		return (weapon != null);
	}

	/**
	 * Check whether this worm has proper weapons.
	 * @return	result == (canHaveAsWeapon(weapon)|| weapon.getWorm() == this)
	 */
	protected boolean hasProperWeapons(){
		for (Weapon weapon: this.weapons){
			if (! canHaveAsWeapon(weapon))
				return false;
			if (weapon.getWorm() != this)
				return false;
			else
				return true;
		}
		
		return true;
	}
	
	/**
	 * Selects the next weapon of this worm.
	 *@post new.getAllWeapons().indexOf(new.getSelectedWeapon()) == this.getAllWeapons().indexOf(this.getSelectedWeapon()) + 1
	 */
	protected void selectNextWeapon(){
		List<Weapon> allWeapons = this.getAllWeapons();
		int IndexOfNextWeapon = allWeapons.indexOf(this.getSelectedWeapon()) + 1;
		if (IndexOfNextWeapon == allWeapons.size())
			allWeapons.get(0).select();
		else allWeapons.get(IndexOfNextWeapon).select();
	}

	/**
	 * Set the given weapon as one of the weapons to which this worm is attached to.
	 * @param	weapon
	 * 			The weapon to be attached.
	 * @post	new.getAllWeapons().contains(weapon)
	 * @throws	IllegalArgumentException
	 * 			(! canHaveAsWeapon(weapon) || ! weapon.hasAsWorm(this))
	 */
	protected void setWeapon(Weapon weapon) throws IllegalArgumentException {
		if (! canHaveAsWeapon(weapon) || ! weapon.hasAsWorm(this))
			throw new IllegalArgumentException("not a valid weapon");
		else weapons.add(weapon);
	}
	
	/**
	 * Add a new rifle and bazooka to this worm.
	 * @post	new.getAllWeapons().contains(rifle)
	 * @post	new.getAllWeapons().contains(bazooka)
	 */
	@Raw
	private void distributeWeapons(){
		Weapon rifle = new Weapon("Rifle", -1, 20, 10, getRadius(10, 7800));
		rifle.addAsWorm(this);
		Weapon bazooka = new Weapon("Bazooka", -1, 80, 50, getRadius(300, 7800));
		bazooka.addAsWorm(this);
	}
	
	/**
	 * Returns the currently selected weapon of the worm.
	 * @return	for each weapon in getAllWeapons()
	 * 				if(weapon.isSelected())
	 * 					return weapon
	 */
	private Weapon getSelectedWeapon(){
		Weapon selectedWeapon = null;
		List<Weapon> allWeapons = this.getAllWeapons();
		for(Weapon weapon : allWeapons)
			if(weapon.isSelected()){
				selectedWeapon = weapon;
				break;
			}
		return selectedWeapon;
	}

	/**
	 * A variable registering the weapons of this worm.
	 */
	private final List<Weapon> weapons = new ArrayList<Weapon>();

	/**
	 * Checks whether the given worm is alive or not.
	 * @return	this.alive
	 */
	protected boolean isAlive(){
		return this.alive;
	}
	
	/**
	 * Kill this worm.
	 * @post	(! new.isAlive())
	 */
	protected void kill(){
		this.setAlive(false);
	}
	
	/**
	 * Decide upon the given flag whether this worm stays alive or not.
	 * @param 	flag
	 * 			The flag be set.
	 * @post	new.isAlive() == flag
	 */
	private void setAlive(boolean flag){
		this.alive = flag;
	}

	/**
	 * Variable registering whether this worm is alive or not.
	 */
	private boolean alive;
	
	/**
	 * Check whether this worm is active.
	 * @return	this.active
	 */
	protected boolean isActive(){
		return this.active;
	}
	
	/**
	 * Activate this worm
	 * @post	new.isActive()
	 * @post	new.getNumberOfActionPoints() = new.getMaxNumberOfActionPoints()
	 * @post	this.getNumberOfHitPoints + 10 = new.getNumberOfHitPoints()
	 * @post	if(! allWeapons.isEmpty())
					allWeapons.get(0).select()
	 * @effect	for each worm in new.getWorld()
	 * 				if (worm != new)
	 * 					worm.isActive() == false
	 */
	protected void activate(){
		List<Worm> allWorms = this.getWorld().getAllWorms();
		for(Worm eachWorm : allWorms)
			eachWorm.deactivate();
		this.setActive(true);
		this.restoreNumberOfActionPoints();
		this.increaseNumberOfHitPointsBy(10);
		List<Weapon> allWeapons = this.getAllWeapons();
		if(!allWeapons.isEmpty())
			allWeapons.get(0).select();
	}
	
	/**
	 * Decide upon the given flag whether this worm will be active or not.
	 * @param 	flag
	 * 			The flag to be set.
	 * @post	new.isActive() == flag
	 */
	private void setActive(boolean flag){
		this.active = flag;
	}

	/**
	 * Deactivate this worm.
	 * @post	new.isActive() == false
	 */
	private void deactivate(){
		this.setActive(false);
	}
	
	/**
	 * Variable registering wheter this worm is active or not.
	 */
	private boolean active;

	/**
	 * Return the name of the worm.
	 * 	The name expresses the alphabetic identification of the worm.
	 */
	@Basic
	public String getName() {
		return name;
	}	
	
	/**
	 * Set the name of this worm to the given name

	 * @param	name
	 * 			The new name of this worm.
	 * @post	The new name of this worm is equal to the given name.
	 * 		|	new.getName() == name
	 * @effect	The new name of this worm is a valid name for any worm.
	 * 		|	this.isValidName(new.getName())
	 * @throws 	IllegalArgumentException("Name is not valid!")
	 * 			The name of this worm is not a valid name for any worm.
	 * 		|	! this.isValidName(name)
	 */	
	public void setName(String name) throws IllegalArgumentException {
		if (!this.isValidName(name))
			throw new IllegalArgumentException("Name is not valid!");
		else this.name = name;
	}
	
	/**
	 * Check whether the given name is a valid name for any worm.
	 * 
	 * @param	name
	 * 			The name to check.
	 * @return	True if and only if the given name contains at least 2 charachters,
	 * 			starts with an uppercase letter and only contains letters, quotes and spaces.
	 * 		|	result == (name.matches("[A-Z]"+"[A-Za-z\"\' ]+"))
	 */
	@Model
	private boolean isValidName(String name) {
		return name.matches("[A-Z]"+"[A-Za-z\"\' ]+");
	}

	/**
	 * Variable registering the name of this worm.
	 */
	private String name;

	/**
	 * Set the radius of this worm to the given radius
	
	 * @param	radius
	 * 			The new radius of this worm.
	 * @post	The new radius of this worm is equal to the given radius.
	 * 		|	new.getRadius() == radius
	 * @post	This worm can have its new radius as its radius.
	 * 		|	this.canHaveAsRadius(new.getRadius())
	 * @effect	The new mass of this worm is equal to the newly calculated mass.
	 * 			new.getMass() == this.getMass(new.getRadius())
	 * @effect	The new maximum number of action points of this worm is equal to the newly calculated maximum number of action points.
	 * 		|	new.getMaxNumberOfActionPoints() == this.getMaxNumberOfActionPoints(new.getRadius())
	 * @effect	The new maximum number of hit points of this worm is equal to the newly calculated maximum number of hit points.
	 * 		|	new.getMaxNumberOfHitPoints() == this.getMaxNumberOfHitPoints(new.getRadius())
	 * @effect	The new number of action points of this worm complies to its invariant (boundaries defined in the setter).
	 * 		|	0 <= new.getNumberOfActionPoints() <= new.getMaxNumberOfActionPoints()
	 * @effect	The new number of hit points of this worm complies to its invariant (boundaries defined in the setter).
	 * 		|	0 <= new.getNumberOfHitPoints() <= new.getMaxNumberOfHitPoints()
	 * @throws 	IllegalArgumentException("Invalid radius!")
	 * 			This worm cannot have the given radius as its radius.
	 * 		|	! isValidRadius(radius)
	 */
	@Override
	public void setRadius(double radius) throws IllegalArgumentException {
		if (!canHaveAsRadius(radius))
			throw new IllegalArgumentException("Invalid radius!");
		super.setRadius(radius);
		setNumberOfActionPoints(this.getNumberOfActionPoints());
		setNumberOfHitPoints(this.getNumberOfHitPoints());
	}

	/**
	 * Check whether this worm can have the given radius as its radius.
	 * 
	 * @param	radius
	 * 			The radius to check.
	 * @return	True if and only if this movable game object can have the given radius as its radius
	 * 			and if the newly calculated maximum number of action points and hit points of this worm are positive.
	 * 		|	result == (super.canHaveAsRadius(radius) && (getMaxNumberOfActionPoints(radius) >= 0) && (getMaxNumberOfHitPoints(radius) >= 0))
	 */
	@Model @Override
	protected boolean canHaveAsRadius(double radius){
		return (super.canHaveAsRadius(radius) && (getMaxNumberOfActionPoints(radius) >= 0) && (getMaxNumberOfHitPoints(radius) >= 0));
	}
	
	/**
	 * Calculate the radius of this worm with the given mass and given density.
	 * @param 	mass
	 * 			The mass of projectiles of weapons for this worm.
	 * @param 	p
	 * 			The density of projectiles of weapons for this worm.
	 * @return	Math.cbrt((mass * 3.0) / (p * 4.0 * Math.PI))
	 */
	private static double getRadius(double mass, double p){
		return Math.cbrt((mass * 3.0) / (p * 4.0 * Math.PI));
	}

	/**
	 * Return the maximum number of action points of the worm.
	 * @return	Maximum number of action points of the worm based on calculations involving the mass of the worm.
	 * 		|	result == getMaxNumberOfActionPoints(this.getMass())
	 */
	@Raw
	public int getMaxNumberOfActionPoints(){
		return getMaxNumberOfActionPoints(this.getMass());
	}
	
	/**
	 * Return the maximum number of hit points of the worm.
	 * @return	Maximum number of hit points of the worm based on calculations involving the mass of the worm.
	 * 		|	result == getMaxNumberOfHitPoints(this.getMass())
	 */
	@Raw
	public int getMaxNumberOfHitPoints(){
		return getMaxNumberOfHitPoints(this.getMass());
	}

	/**
	 * Return the current number of action points of the worm.
	 * 	The current number of action points expresses the number of action points this worm has left.
	 */	
	@Basic
	public int getNumberOfActionPoints() {
		return numberOfActionPoints;
	}

	/**
	 * Return the current number of hit points of the worm.
	 * 	The current number of hit points expresses the number of hit points this worm has left.
	 */	
	@Basic
	public int getNumberOfHitPoints() {
		return numberOfHitPoints;
	}

	/**
	 * Decrease the number of action points by the given decrementing factor.
	 * @param 	decrement
	 * 			The amount by which to decrease the amount of action points.
	 * @post	if (decrement < 0)
	 * 				return new.getNumberOfActionPoints() == this.getNumberOfActionPoints()
	 * 			else 
	 * 				return new.getNumberOfActionPoints() == this.getNumberOfActionPoints() - decrement
	 */
	protected void decreaseNumberOfActionPointsBy(int decrement){
		if (decrement < 0)
			decrement = 0;
		this.setNumberOfActionPoints(this.getNumberOfActionPoints() - decrement);
	}

	/**
	 * Decrease the current number of hit points of this worm by the given number.
	 * 
	 * @param 	decrement
	 * 			The given number to decrement the current number of hit points with.
	 * @post	The new number of hit points of this worm is equal to the old number of hit points, decremented with the given number.
	 * 		|	new.getNumberOfHitPoints() == this.getNumberOfHitPoints() - decrement
	 * @effect	The new number of hit points of this worm complies to its invariant (boundaries defined in the setter).
	 * 		|	0 <= new.getNumberOfHitPoints() <= new.getMaxNumberOfHitPoints()
	 */
	protected void decreaseNumberOfHitPointsBy(int decrement){
		if (decrement < 0)
			decrement = 0;
		this.setNumberOfHitPoints(this.getNumberOfHitPoints() - decrement);
	}
	
	/**
	 * Restore the current number of action points of this worm to its maximum.
	 * @post	The new number of action points of this worm is equal to the maximum number of action points of this worm.
	 * 		|	new.getNumberOfActionPoints() == this.getMaxNumberOfActionPoints()
	 * @effect	The new number of action points of this worm complies to its invariant (boundaries defined in the setter).
	 * 		|	0 <= new.getNumberOfActionPoints() <= new.getMaxNumberOfActionPoints()
	 */
	protected void restoreNumberOfActionPoints(){
		this.setNumberOfActionPoints(this.getMaxNumberOfActionPoints());
	}

	/**
	 * Increase the number of hit points by the given increment.
	 * @param 	increment
	 * 			The amount by which the amount of hit points has to be increased.
	 * @post	if (increment < 0)
	 * 				return new.getNumberOfActionPoints() == this.getNumberOfActionPoints()
	 * 			else
	 * 				return new.getNumberOfActionPoints() == this.getNumberOfActionPoints() + increment
	 */
	protected void increaseNumberOfHitPointsBy(int increment){
		if (increment < 0)
			increment = 0;
		this.setNumberOfHitPoints(this.getNumberOfHitPoints() + increment);
	}
	
	/**
	 * Return the given double precision number, rounded to the nearest integer.
	 * @param	number
	 * 			The given double precision number.
	 * @return	The given double precision number, rounded to the nearest integer.
	 * 		|	if (Math.round(number) > Integer.MAX_VALUE) result == Integer.MAX_VALUE
	 * 		|	else result = Math.round(number)
	 */
	@Model
	private int roundedToNearestInteger(double number){
		long longNumber = Math.round(number);
		int intNumber;
		if(longNumber > Integer.MAX_VALUE){
			intNumber = Integer.MAX_VALUE;
		}
		else intNumber = (int) longNumber;
		return intNumber;
	}

	/**
	 * Set the number of action points of this worm to the given number of action points.
	 * 
	 * @param	numberOfActionPoints
	 * 			The new number of action points for this worm.
	 * @post	If the given number of action points is not below zero and not above the maximum number of action points,
	 * 			the new number of action points of this worm is equal to the given number of action points.
	 * 			If the given number of action points is negative, the new number of action points is equal to zero.
	 * 			If the given number of action points is greater than the maximum number of action points, the new number
	 * 			of action points is equal to the maximum number of action points.
	 * 		|	if ((numberOfActionPoints >= 0) && (numberOfActionPoints <= this.getMaxNumberOfActionPoints())) new.getNumberOfActionPoints() == numberOfActionPoints
	 * 		|	else if (numberOfActionPoints < 0) new.getNumberOfActionPoints() == 0
	 * 		|	else if (numberOfActionPoints > this.getMaxNumberOfActionPoints()) new.getNumberOfActionPoints() == this.getMaxNumberOfActionPoints()
	 */
	private void setNumberOfActionPoints(int numberOfActionPoints){
		if(numberOfActionPoints <= 0){
			numberOfActionPoints = 0;
			if(this.getWorld() != null)
				this.getWorld().startNextTurn();
		}
		else if(numberOfActionPoints > this.getMaxNumberOfActionPoints())
			numberOfActionPoints = this.getMaxNumberOfActionPoints();
		this.numberOfActionPoints = numberOfActionPoints;
	}

	/**
	 * Set the number of action points of this worm to the given number of action points.
	 * 
	 * @param	numberOfHitPoints
	 * 			The new number of hit points for this worm.
	 * @post	If the given number of hit points is not below zero and not above the maximum number of hit points,
	 * 			the new number of hit points of this worm is equal to the given number of hit points.
	 * 			If the given number of hit points is negative, the new number of hit points is equal to zero.
	 * 			If the given number of hit points is greater than the maximum number of hit points, the new number
	 * 			of hit points is equal to the maximum number of hit points.
	 * 		|	if ((numberOfHitPoints >= 0) && (numberOfHitPoints <= this.getMaxNumberOfHitPoints())) new.getNumberOfHitPoints() == numberOfHitPoints
	 * 		|	else if (numberOfHitPoints < 0) new.getNumberOfHitPoints() == 0
	 * 		|	else if (numberOfHitPoints > this.getMaxNumberOfHitPoints()) new.getNumberOfHitPoints() == this.getMaxNumberOfHitPoints()
	 */
	private void setNumberOfHitPoints(int numberOfHitPoints){
		if(numberOfHitPoints <= 0){
			numberOfHitPoints = 0;
			this.kill();
			if(this.getWorld() != null)
				this.getWorld().startNextTurn();
		}
		else if(numberOfHitPoints > this.getMaxNumberOfHitPoints())
			numberOfHitPoints = this.getMaxNumberOfHitPoints();
		this.numberOfHitPoints = numberOfHitPoints;
	}

	/**
	 * Return the maximum number of action points for a worm with given mass.
	 * @param	mass
	 * 			The given mass
	 * @return	Maximum number of action points for a worm based on calculations involving the given mass.
	 * 		|	result == roundedToNearestInteger(mass)
	 */
	private int getMaxNumberOfActionPoints(double mass){
		return roundedToNearestInteger(mass);
	}

	/**
	 * Return the maximum number of hit points for a worm with given mass.
	 * @param	mass
	 * 			The given mass
	 * @return	Maximum number of hit points for a worm based on calculations involving the given mass.
	 * 		|	result == roundedToNearestInteger(mass)
	 */
	private int getMaxNumberOfHitPoints(double mass){
		return roundedToNearestInteger(mass);
	}

	/**
	 * Variable registering the current number of action points of this worm.
	 */	
	private int numberOfActionPoints;
	
	/**
	 * Variable registering the current number of hit points of this worm.
	 */	
	private int numberOfHitPoints;
	
	/**
	 * Eat a piece of food.
	 * @param 	snack
	 * 			The piece of food to be eaten.
	 * @post	new.getRadius() = this.getRadius()* (1 + snack.getPercentualIncreaseOfRadius())
	 * @effect	(! new.getWorld().getObjects().contains(snack))
	 */
	private void eat(Food snack){
		this.setRadius(this.getRadius() * (1 + snack.getPercentualIncreaseOfRadius()));
		this.getWorld().removeAsGameObject(snack);
	}

	//TODO eatAllFood
	
	/**
	 * Eat all food.
	 * @effect	for each food in this.getWorld().overlapWithFood(this.getPosition(), this.getRadius())
	 * 				new.getRadius() = this.getRadius()** (1 + food.getPercentualIncreaseOfRadius())
	 * 				(! new.getWorld().getObjects().contains(food))
	 */
	private void eatAllFood(){
		List<Food> snacks = this.getWorld().overlapWithFood(this.getPosition(), this.getRadius());
		for (Food snack : snacks)
			this.eat(snack);
	}

	/**
	 * Check whether the worm can turn by the given angle.
	 * 
	 * @param	turnByAngle
	 * 			The angle by which this worm will be turned.
	 * @Pre		The sum of the worm's current direction and the given angle to turn by is a valid direction for any worm.
	 * 		|	this.isValidDirection(this.getDirection() + turnByAngle)
	 * @return	True if and only if the current number of action points is not smaller than the amount of action points required to turn the worm by the given angle.
	 * 		|	result == (this.getNumberOfActionPoints() >= this.amountOfActionPointsForTurning(turnByAngle))
	 */
	public boolean canTurn(double turnByAngle){
		return (this.getNumberOfActionPoints() >= this.amountOfActionPointsForTurning(turnByAngle));
	}

	/**
	 * Turn this worm while paying the appropriate amount of action points.
	 * 
	 * @param 	turnByAngle
	 * 			The angle by which this worm will be turned.
	 * @Pre		This worm can turn by the given angle.
	 * 		|	this.canTurn(turnByAngle)
	 * @post	The new number of action points equals the old number of action points reduced with the amount of action points to be paid for turning by the given angle.
	 * 		|	new.getNumberOfActionPoints() == this.getNumberOfActionPoints - this.amountOfActionPointsForTurning(turnByAngle)
	 * @post	The new direction of this worm is equal to the old direction incremented with the given angle.
	 * 		|	new.getDirection() == this.getDirection() + turnByAngle
	 */
	@Override
	public void turn(double turnByAngle){
		assert this.canTurn(turnByAngle) :
			"Precondition: The worm can turn by the given angle";
		super.turn(turnByAngle);
		this.decreaseNumberOfActionPointsBy(this.amountOfActionPointsForTurning(turnByAngle));
	}

	/**
	 * Convert the given angle to a representative angle that is equal to or greater than zero and smaller than two times pi radians.
	
	 * @param	angle
	 * 			The angle to be converted.
	 * @return	The converted angle is a geometrically identical angle that lies between zero and two times pi, excluding the latter.
	 * 		|	if ((angle % (2 * pi)) < 0) result == (angle % (2 * pi)) + (2 * pi)
	 * 		|	else result == angle % (2 * pi)
	 */	
	@Model
	protected static double convertToRepresentativeAngle(double angle){
		double representativeAngle = angle % (2 * Math.PI);
		if (representativeAngle < 0)
			representativeAngle += 2 * Math.PI;
		return representativeAngle;
	}

	/**
	 * Return the amount of action points this worm has to pay to turn by the given angle.
	 * 
	 * @param 	turnByAngle
	 * 			The angle by which this worm will be turned.
	 * @return	If the converted representative angle to turn by is equal to zero, the resulting amount of action points to be paid is also equal to zero.
	 * 		|	if (this.convertToRepresentativeAngle(turnByAngle) == 0) result == 0
	 * @return	If the converted representative angle to turn by is not equal to zero, the resulting amount of action points to be paid is equal to the quotient of 60 and a factor that is calculated by dividing 2 times pi by the converted representative angle to turn by.
	 * 		|	if (this.convertToRepresentativeAngle(turnByAngle) != 0) result == ceil(60 / ((2*pi)/effectiveAngle))
	 * 		|	where
	 * 		|		if (this.convertToRepresentativeAngle(turnByAngle) > (2 * pi)) effectiveAngle == (2 * Math.PI) - this.convertToRepresentativeAngle(turnByAngle)
	 * 		|		else effectiveAngle == this.convertToRepresentativeAngle(turnByAngle)
	 */
	@Model
	private int amountOfActionPointsForTurning(double turnByAngle){
		double effectiveAngle = convertToRepresentativeAngle(turnByAngle);
		int decrement;
		if(effectiveAngle == 0)
			decrement = 0;
		else{
			if(effectiveAngle > Math.PI)
				effectiveAngle = (2 * Math.PI) - effectiveAngle;
			double factor = (2 * Math.PI) / effectiveAngle;
			decrement = (int) Math.ceil(60 / factor);
		}
		return decrement;
	}
	
	//TODO: Fall formeel
	
	/**
	 *
	 * Jump the worm with a given timeStep.
	 * @param	timeStep
	 * @post	new.getPosition = new Position(jumpStepOnXAxis(jumpTime(timeStep)), jumpStepOnYAxis(jumpTime(timeStep)))
	 * @post	if (this.canFall())
	 * 				this.fall()
	 * @effect	new.getX() == jumpStepOnXAxis(jumpTime(timeStep))
	 * @effect	new.getY() == jumpStepOnYAxis(jumpTime(timeStep))
	 * @throws 	UnsupportedOperationException("Cannot jump!")
	 * 		|	! canJump(timeStep)
	 */	
	@Override
	public void jump(double timeStep) throws UnsupportedOperationException{
		if(!this.canJump(timeStep))
			throw new UnsupportedOperationException("Cannot jump!");
		super.jump(timeStep);
		if (this.canFall())
			this.fall();
		this.setNumberOfActionPoints(0);
		this.eatAllFood();
	}
	
	

	/**
	 * Checks whether this worm can jump with the given timeStep.
	 *  return (super.canJump(timeStep) && (this.getNumberOfActionPoints() > 0) && !(this.getPosition().distanceFromPositionWithCoordinates(jumpStep[0], jumpStep[1]) < this.getRadius()));
	 */
	@Model @ Override
	protected boolean canJump(double timeStep){
		double jumpTime = this.jumpTime(timeStep);
		double[] jumpStep = this.jumpStep(jumpTime);
		boolean boolean1 = super.canJump(timeStep);
		boolean boolean2 = (this.getNumberOfActionPoints() > 0);
		boolean boolean3 = !(this.getPosition().distanceFromPositionWithCoordinates(jumpStep[0], jumpStep[1]) < this.getRadius());
		return (super.canJump(timeStep) && (this.getNumberOfActionPoints() > 0) && !(this.getPosition().distanceFromPositionWithCoordinates(jumpStep[0], jumpStep[1]) < this.getRadius()));
	}
	
	
	@Override
	protected String getCustomText(){
		return "jump";
	}
	
	/**
	 * Return the initial force to be exerted on the worm.
	 * @return 	Initial force to be exerted on the worm.
	 * 		|	result == (5 * this.getNumberOfActionPoints()) + (this.getMass() * EARTHS_STANDARD_ACCELERATION)
	 */
	@Model @Override
	protected double getInitialForce(){
		return ((5 * this.getNumberOfActionPoints()) + (this.getMass() * EARTHS_STANDARD_ACCELERATION));
	}
	
	/**
	 * Checks whether this worm can fall.
	 * @return	! this.getWorld().isAdjacentToImpassableFloor(this.getPosition(), this.getRadius())
	 */
	public boolean canFall(){
		return !this.getWorld().isAdjacentToImpassableFloor(this.getPosition(), this.getRadius());
	}
	
	//TODO: fall formeel
	
	/**
	 * Let a worm fall.
	 * 
	 * @throws 	UnsupportedOperationException("Cannot fall!")
	 * 			(!this.canFall())
	 * @post	
	 * for each i in this.getWorld().getPassableMap()[this.getX()][i]
	 * 				if ((this.getWorld().isPassable(new Position(x,i),radius) && (!this.canFall()))
	 * 					new.getNumberOfHitPoints() == this.getNumberOfHitpoints - (int) Math.round(3 * this.getPosition().distanceFromPosition(new Position(this.getX(),i))
	 *				for each food in this.getWorld().overlapWithFood((new Position(x,i),radius), this.getRadius())
	 *					 == this.getRadius() * (1 + food.getPercentualIncreaseOfRadius())
	 * 					
	 */
	public void fall() throws UnsupportedOperationException{
		if(!this.canFall())
			throw new UnsupportedOperationException("Cannot fall!");
		double decrementY = this.getWorld().getPixelHeight();
		double newY = this.getY() - decrementY;
		double x = this.getX();
		double radius = this.getRadius();
		boolean landed = false;
		Position newPosition = new Position(x,newY);
		while (this.getWorld().isPassable(newPosition,radius)){
			if (!this.canFall()){
				landed = true;
				break;
			}
			newY -= decrementY;
			newPosition = new Position(x,newY);
		}
		if (landed){
			double decrement = 3 * this.getPosition().distanceFromPosition(newPosition);
			int intDecrement;
			if (decrement > Integer.MAX_VALUE)
				intDecrement = Integer.MAX_VALUE;
			else intDecrement = (int) Math.round(decrement);
			this.decreaseNumberOfHitPointsBy(intDecrement);
			this.eatAllFood();
		}
		else this.kill();
	}
	
	/**
	 * Checks whether this worm can move.
	 * @return	this.positionAfterMove() != null && (this.getNumberOfActionPoints() >= this.amountOfActionPointsForMoving(this.positionAfterMove()))
	 */
	public boolean canMove(){
		Position newPosition = this.positionAfterMove();
		return (newPosition != null && (this.getNumberOfActionPoints() >= this.amountOfActionPointsForMoving(newPosition)));
	}

	//TODO: Move formeel
	
	/**
	 * 
	 * @throws UnsupportedOperationException
	 */
	public void move() throws UnsupportedOperationException{
		if (!this.canMove())
			throw new UnsupportedOperationException("Cannot move!");
		Position newPosition = this.positionAfterMove();
		this.setPosition(newPosition);
		if (this.canFall())
			this.fall();
		this.decreaseNumberOfActionPointsBy(this.amountOfActionPointsForMoving(newPosition));
		this.eatAllFood();
	}

	//TODO: checkCirclePieceForOptimalMove formeel
	
	/**
	 * 
	 * @param 	onlyPassable
	 * @return	
	 */
	
	private Position checkCirclePieceForOptimalMove(boolean onlyPassable){
		double direction = this.getDirection();
		double divergedDirection = direction;
		double limitForDivergedDirection = divergedDirection + 0.7875;
		double radius = this.getRadius();
		double distance = radius;
		double distanceStep = 10 * Math.min(this.getWorld().getPixelWidth(), this.getWorld().getPixelHeight());
		Position testPosition = null;
		boolean candidateFound = false;
		outerloop:
		while (divergedDirection <= limitForDivergedDirection){
			while (distance >= 0.1){
				testPosition = new Position((distance * Math.cos(divergedDirection)), (distance * Math.sin(divergedDirection)));
				if (onlyPassable && this.getWorld().isPassable(testPosition, radius)){
					candidateFound = true;
					break outerloop;
				}
				testPosition = new Position((distance * Math.cos((2 * direction) - divergedDirection)), (distance * Math.sin((2 * direction) - divergedDirection)));
				if (!onlyPassable && this.getWorld().isAdjacentToImpassableFloor(testPosition, radius)){
					candidateFound = true;
					break outerloop;
				}
				distance -= distanceStep;
			}
			distance = radius;
			divergedDirection += 0.0175;
		}
		if (candidateFound)
			return testPosition;
		else return null;
	}

	/**
	 * Return the slope of the given position.
	 * @param 	other
	 * 			The position of which the slope will be returned.
	 * @return	((other.getY() - this.getY())/(other.getX() - this.getX()))
	 */
	private double getSlope(Position other){
		return ((other.getY() - this.getY())/(other.getX() - this.getX()));
	}

	/**
	 * Return the amount of action points for moving.
	 * @param 	newPosition
	 * 			The position of this worm.
	 * @return	if (Math.round(Math.ceil(Math.abs(Math.cos(this.getSlope(newPosition))) + Math.abs(4 * Math.sin(this.getSlope(newPosition))))) > Integer.MAX_VALUE)
					return Integer.MAX_VALUE;
				else 
					return Math.round(Math.ceil(Math.abs(Math.cos(this.getSlope(newPosition))) + Math.abs(4 * Math.sin(this.getSlope(newPosition)))))
	 */
	private int amountOfActionPointsForMoving(Position newPosition){
		double slopeAfterMove = this.getSlope(newPosition);
		long longResult = Math.round(Math.ceil(Math.abs(Math.cos(slopeAfterMove)) + Math.abs(4 * Math.sin(slopeAfterMove))));
		if (longResult > Integer.MAX_VALUE)
			longResult = Integer.MAX_VALUE;
		return (int) longResult;
	}

	/**
	 * Returns the position of this worm after a move.
	 * @return	if (this.checkCirclePieceForOptimalMove(false) != null)
	 * 				return this.checkCirclePieceForOptimalMove(false)
	 * 			else if	(this.checkCirclePieceForOptimalMove(true) != null)
	 * 					return this.checkCirclePieceForOptimalMove(true)
	 * 				else return null
	 */
	private Position positionAfterMove(){
		Position newPosition1 = this.checkCirclePieceForOptimalMove(false);
		Position newPosition2 = this.checkCirclePieceForOptimalMove(true);
		if (newPosition1 != null)
			return newPosition1;
		else if (newPosition2 != null)
			return newPosition2;
		else return null;
	}

	/**
	 * Return the team to which this worm belongs.
	 */
	public Team getTeam(){
		return this.team;
	}
	/**
	 * Check whether this worm can have the given team as its worm
	 * @param 	team
	 * 			The team to be added.
	 * @return	(team == null || team.canHaveAsWorm(this))
	 */
	protected boolean canHaveAsTeam(Team team){
		return (team == null || team.canHaveAsWorm(this));
	}
	/**
	 * Check whether this worm has a proper team as its team.
	 * @return	(canHaveAsTeam(getTeam()) && getTeam().hasAsWorm(this))
	 */
	protected boolean hasProperTeam() {
		return (canHaveAsTeam(getTeam()) && getTeam().hasAsWorm(this));
	}
	/**
	 * Set the given team to the new team of this worm.
	 * @param 	team
	 * 			The team to be set.
	 * @post	new.getTeam() == team
	 * @throws 	IllegalArgumentException
	 * 			(! canHaveAsTeam(team) || ! team.hasAsWorm(this))
	 */
	protected void setTeam(Team team) throws IllegalArgumentException {
		if (! canHaveAsTeam(team) || ! team.hasAsWorm(this))
			throw new IllegalArgumentException("not a valid team");
		else this.team = team;
	}
	
	/**
	 * Variable registering  the team of this worm.
	 */
	Team team;

	public void setProgram(Program program){
		this.program = program;
	}
	
	public Program getProgram(){
		return this.program;
	}
	
	Program program;
}
