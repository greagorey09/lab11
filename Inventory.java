/**
* Assignment 11
* @author Alp Deniz Senyurt
* Student ID: 100342433
* @author Greagorey Markerian
* Student ID: 100338209
* Self explanatory variables and parameters will not be documented as they are, "self-explanatory".
*/

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
public class Inventory 
{

	private Lock lock;
	private HashMap<String, Integer> inventory;

	/**
	*creates a Hashmap with key as a String and value as an Integer
	*/
	public Inventory() 
	{
		inventory = new HashMap<String, Integer>();
		lock = new ReentrantLock();
	}  

	/**
	* creates an inventory item with initial available units equal to n if the item in not added to the inventory, otherwise it adds n to the number of the available units.
	* @param item_name : name of the item to be added
	* @param n: number of the units
	*/
	public void addItem(String item_name, int n)
	{
		lock.lock();
		int unitsAvailable;
		if(inventory.containsKey(item_name))
		{
			unitsAvailable = inventory.get(item_name);
			inventory.replace(item_name,unitsAvailable+n);
		}
		else
			inventory.put(item_name, n);
		lock.unlock();
	} 
	/**
	* Returns number of the available units for the item specified by item_name, if the item exits in the inventory, otherwise it returns -1 indicating that there in no such an item carried in the inventory.
	* @param item_name : name of the item to be checked
	* @return number of the available units, or -1 if the item is not carried in the inventory.
	*/
	public int checkInventory(String item_name)
	{
		lock.lock();
		int unitsAvailable=-1;
		if(inventory.containsKey(item_name))
		{
			unitsAvailable = inventory.get(item_name);
		}
		lock.unlock();
		return unitsAvailable;
	}
	/**
	* removes maximum n units of the item_name from the inventory if available, and returns number of units removed, or -1 if the item is not carried in the inventory.
	* @param item_name : name of the item to be removed
	* @param n : number of the units to be removed
	* @return number of the units removed, or -1 if the item is not carried in the inventory.
	*/
	public int takeItem(String item_name, int n)
	{
		lock.lock();
		int unitsAvailable;
		int unitsRemoved=-1;
		if(inventory.containsKey(item_name))
		{
			unitsAvailable = inventory.get(item_name);
			if(n<unitsAvailable)
			{
				inventory.replace(item_name,unitsAvailable-n);
				unitsRemoved=n;
			}
			else{
				inventory.replace(item_name,0); 
				unitsRemoved=unitsAvailable; 
			}

		}
		lock.unlock();
		return unitsRemoved;
	}

	/** returns all items and number of the units available in the inventory that their available units is less than or equals to a threshold.
	* @param threshold: the number of available units that are less than or equal to threshold
	* @return list of the items and available units in following format: [item_name1 unitsAvailable] [item_name2 unitsAvailable] ...
	*/
	public String getThreshold(int threshold)
	{
		lock.lock();
		String result="";
		Iterator<String> keySetIterator = inventory.keySet().iterator();
		while(keySetIterator.hasNext())
		{
			String key = keySetIterator.next();
			Integer value = inventory.get(key);
			if(value<=threshold)
				result +="["+key+","+value+"] ";
		}
		lock.unlock();
		return result;
	}
}