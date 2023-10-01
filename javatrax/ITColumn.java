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
	
	private boolean isChannel;
	private boolean isNote;
	private boolean isInstrument;
	private boolean isEffect;
	private boolean isVolPan;
	
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
		return isEffect;
	}
	
	public boolean hasVolPan(){
		return isVolPan;
	}
	
	public boolean hasInstrument(){
		return isInstrument;
	}
	
	public boolean hasNote(){
		return isNote;
	}
	
	public boolean hasChannel(){
		return isChannel;
	}
	
	private void getColumnData(){
		if(dataMap.containsKey("channel")){isChannel = true; channel = dataMap.get("channel");}
		if(dataMap.containsKey("note")) {isNote = true; note = dataMap.get("note");}
		if(dataMap.containsKey("instrument")) {isInstrument = true; instrument = dataMap.get("instrument");}
		if(dataMap.containsKey("vol/pan")) {isVolPan = true; volpan = dataMap.get("vol/pan");}
		if(dataMap.containsKey("effect1")) {isEffect = true; effect1 = dataMap.get("effect1");}
		if(dataMap.containsKey("effect2")) {isEffect = true; effect2 = dataMap.get("effect2");}
	}
	
	public String toString(){
		return dataMap.toString();
	}
}