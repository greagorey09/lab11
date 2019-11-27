/**
* Assignment 11
* @author Alp Deniz Senyurt
* Student ID: 100342433
* @author Greagorey Markerian
* Student ID: 100338209
* Self explanatory variables and parameters will not be documented as they are, "self-explanatory".
*/

import java.io.DataInputStream;
import java.lang.FunctionalInterface;

@FunctionalInterface
public interface ProtocolFunctional
{
	//CONVERT RESPONSE TO FINAL STRING
	String m_serverResponse(int command, int response, DataInputStream in);	
}