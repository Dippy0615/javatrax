package javatrax;

import java.util.Vector;

public class ITRow
{
	private Vector<ITColumn> columns;
	private int length;
	
	public ITRow (Vector<ITColumn> _columns)
	{
		columns = _columns;
		length = columns.size();
	}
	
	public int getLength(){
		return length;
	}
	
	public Vector<ITColumn> getColumns(){
		return columns;
	}
	
	public String toString(){
		return columns.toString();
	}
}