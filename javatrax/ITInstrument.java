package javatrax;

import java.io.RandomAccessFile;
import java.io.IOException;

public class ITInstrument
{
	private int startingAddress;
	private int position;
	private RandomAccessFile RAFile;
	private int number;
	private boolean isUnpacked;
	
	private String NNA;
	private String DCT;
	private String DCA;
	private int fadeOut;
	private int PPS;
	private int PPC;
	private int globalVolume;
	private int defaultPan;
	private int volumeVariation;
	private int panningVariation;
	private int numSamples;
	private String name;
	private String filename;
	
	public ITInstrument(int address, RandomAccessFile file, int num)
	{
		startingAddress = address;
		position = address;
		RAFile = file;
		number = num;
		isUnpacked = false;
	}
	
	public int getNumber(){
		return number;
	}
	
	public int getPosition(){
		unpackWarning();
		return position;
	}
	
	public int getPanningVariation(){
		unpackWarning();
		return panningVariation;
	}
	
	public int getVolumeVariation(){
		unpackWarning();
		return volumeVariation;
	}
	
	public int getDefaultPan(){
		unpackWarning();
		return defaultPan;
	}
	
	public int getGlobalVolume(){
		unpackWarning();
		return globalVolume;
	}
	
	public int getPPC(){
		unpackWarning();
		return PPC;
	}
	
	public int getPPS(){
		unpackWarning();
		return PPS;
	}
	
	public int getFadeOut(){
		unpackWarning();
		return fadeOut;
	}
	
	public String getDCA(){
		unpackWarning();
		return DCA;
	}
	
	public String getDCT(){
		unpackWarning();
		return DCT;
	}
	
	public String getNNA(){
		unpackWarning();
		return NNA;
	}
	
	public String getFilename(){
		unpackWarning();
		return filename;
	}
	
	public String getName(){
		unpackWarning();
		return name;
	}
	
	public void unpack() throws IOException{
		RAFile.skipBytes(4); //'IMPI' instrument header
		byte[] filenameChars = new byte[12];
		RAFile.read(filenameChars);
		String _temp = "";
		for(int i=0;i<12;i++){
			_temp += (char) filenameChars[i];
		}
		filename = _temp.substring(0, _temp.length());
		
		RAFile.skipBytes(1);
		int byteNNA = RAFile.read();
		switch(byteNNA){
			case 0: NNA = "cut"; break;
			case 1: NNA = "continue"; break;
			case 2: NNA = "note off"; break;
			case 3: NNA = "note fade"; break;
			default: NNA = "cut"; break;
		}
		int byteDCT = RAFile.read();
		switch(byteDCT){
			case 0: DCT = "off"; break;
			case 1: DCT = "note"; break;
			case 2: DCT = "sample"; break;
			case 3: DCT = "instrument"; break;
			default: DCT = "off"; break;
		}
		int byteDCA = RAFile.read();
		switch(byteDCA){
			case 0: DCA = "off"; break;
			case 1: DCA = "note off"; break;
			case 2: DCA = "note fade"; break;
			default: DCA  = "off"; break;
		}
		fadeOut = RAFile.read();
		PPS = RAFile.read();
		PPC = RAFile.read();
		globalVolume = RAFile.read();
		defaultPan = RAFile.read();
		volumeVariation = RAFile.read();
		panningVariation = RAFile.read();
		RAFile.skipBytes(2); //tracker version
		numSamples = RAFile.read();
		RAFile.skipBytes(1); //empty byte
		byte[] nameChars = new byte[26];
		RAFile.read(nameChars);
		String temp = "";
		for(int i=0;i<26;i++){
			temp += (char) nameChars[i];
		}
		name = temp.substring(1,temp.length());
		RAFile.skipBytes(6);
		RAFile.skipBytes(240); //note-sample keyboard table... i dont really feel like adding this lol
		RAFile.skipBytes(16); //envelopes... i dont really feel like adding this either
		//pos: 319
		RAFile.skipBytes(235); //skip to the end of the instrument from here
		isUnpacked = true;
		position = (int)(RAFile.getChannel().position());
	}
	
	public void unpackWarning(){
		if(!isUnpacked){
			System.out.println("ERROR: You must unpack this instrument before accessing its contents!");
			System.exit(0);
		}
	}
}