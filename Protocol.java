/**
* Assignment 11
* @author Alp Deniz Senyurt
* Student ID: 100342433
* @author Greagorey Markerian
* Student ID: 100338209
* Self explanatory variables and parameters will not be documented as they are, "self-explanatory".
*/

interface Protocol
{
	// RESPONSE
	public static final int FAILED = 0;
	public static final int SUCCEED = 1;
	
	//COMMAND
	public static final int ADD_ITEM = 2;
	public static final int CHECK_ITEM = 3;
	public static final int TAKE_ITEM = 4;
	public static final int GET_THRESHOLD = 5;
	public static final int QUIT = 6;
	public static final int PORT = 35285;
}


/*
Client side:						Server side:
------------------------------------------------------------
ADD_ITEM item, n					SUCCEED or FAILED
CHECK_ITEM item						SUCCEED and (number of available units for the item_name) or FAILED
TAKE_ITEM item, n					SUCCEED and (actual number of units removed) or FAILED
GET_THRESHOLD n 					SUCCEED and (returns all items and their available units in the inventory that have equal or less than the threshold value available units "[item_name1 unitsAvailable] [item_name2 unitsAvailable]") or FAILED
QUIT								CLOSED
Invalid command						FAILED
------------------------------------------------------------
item: Stream
n: int
*/