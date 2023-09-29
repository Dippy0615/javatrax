package javatrax;

import java.util.HashMap;

public class ITColumn
{
	//private ITRow row;
	private HashMap<String, Integer> dataMap;
	
	private int channel;
	private int note;
	private int instrument;
	private int volpan;
	private int effect1;
	private int effect2;
	
	public ITColumn (HashMap<String, Integer> _dataMap)
	{
		dataMap = _dataMap;
		getColumnData();
	}

	public int getEffectParameters(){
		return effect2;
	}
	
	public int getEffectType(){
		return effect1;
	}
	
	public int getVolPan(){
		return volpan;
	}
	
	public int getInstrument(){
		return instrument;
	}
	
	public int getNote(){
		return note;
	}
	
	public int getChannel(){
		return channel;
	}
	
	public boolean hasEffect(){
		return dataMap.containsKey("effect1");
	}
	
	public boolean hasVolPan(){
		return dataMap.containsKey("vol/pan");
	}
	
	public boolean hasInstrument(){
		return dataMap.containsKey("instrument");
	}
	
	public boolean hasNote(){
		return dataMap.containsKey("note");
	}
	
	public boolean hasChannel(){
		return dataMap.containsKey("channel");
	}
	
	private void getColumnData(){
		if(dataMap.containsKey("channel")) channel = dataMap.get("channel");
		if(dataMap.containsKey("note")) note = dataMap.get("note");
		if(dataMap.containsKey("instrument")) instrument = dataMap.get("instrument");
		if(dataMap.containsKey("vol/pan")) volpan = dataMap.get("vol/pan");
		if(dataMap.containsKey("effect1")) effect1 = dataMap.get("effect1");
		if(dataMap.containsKey("effect2")) effect2 = dataMap.get("effect2");
	}
	
	public String toString(){
		return dataMap.toString();
	}
}