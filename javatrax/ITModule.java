package javatrax;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class ITModule
{
	private File myFile;
	private RandomAccessFile RAFile;
	private Vector<ITPattern> patterns;
	private Vector<ITPattern> orders;
	private Vector<ITInstrument> instruments;
	
	private boolean arePatternsUnpacked;
	private boolean areInstrumentsUnpacked;
	
	private byte orderSequence[];
	
	private int longInstrumentOffset;
	private int longSampleOffset;
	private int longPatternOffset;
	
	private boolean isStereo;
	private boolean hasInstruments;
	private boolean hasLinearSlides;
	
	public ITModule(File file) throws IOException
	{
		try{
			myFile = file;
			RAFile = new RandomAccessFile(file, "r");
			checkModule();
			
			patterns = new Vector<ITPattern>();
			orders = new Vector<ITPattern>();
			instruments = new Vector<ITInstrument>();
			
			arePatternsUnpacked = false;
			areInstrumentsUnpacked = false;
			
			longInstrumentOffset = 192+getOrdNum();
			longSampleOffset = 192+getOrdNum()+getInsNum()*4;
			longPatternOffset = 192+getOrdNum()+getInsNum()*4+getSmpNum()*4;
			
			makeFlags();
			makeOrderSequence();
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	public Vector<ITPattern> getOrders(){
		if (!arePatternsUnpacked){ 
			System.out.println("ERROR: You must unpack this module's patterns before accessing them!");
			System.exit(0);
		}
		return orders;
	}
	
	public void makeOrders(){
		for(int i=0;i<orderSequence.length;i++){
			for(int j=0;j<patterns.size();j++){
				ITPattern pat = patterns.get(j);
				if(((int)orderSequence[i])==pat.getNumber()){
					orders.add(pat);
					break;
				}
			}
		}
	}
	
	public boolean usesLinearSlides(){
		return hasLinearSlides;
	}
	
	public boolean usesInstruments(){
		return hasInstruments;
	}
	
	public void makeFlags() throws IOException{
		RAFile.seek(44);
		int byteFlag;
		byteFlag = RAFile.read();
		if((byteFlag&1)>0) isStereo = true;
			else isStereo = false;
		if((byteFlag&4)>0) hasInstruments = true;
			else hasInstruments = false;
		if((byteFlag&8)>0) hasLinearSlides = true;
			else hasLinearSlides = false;
		
	}
	
	public byte[] getOrderSequence() throws IOException{
		if(orderSequence==null){makeOrderSequence();}
		return orderSequence;
	}
	
	public void makeOrderSequence() throws IOException{
		orderSequence = new byte[getOrdNum()-1];
		RAFile.seek(192);
		RAFile.read(orderSequence);
	}
	
	public void printOrders(){
		if (arePatternsUnpacked)
		{
			for (int i=0;i<orders.size();i++){
				ITPattern pat = orders.get(i);
				System.out.println("[pattern "+pat.getNumber()+"] ");
			}
		}
	}
	
	public void printPatterns(){
		if (arePatternsUnpacked)
		{
			for (int i=0;i<patterns.size();i++){
				ITPattern pat = patterns.get(i);
				System.out.println("[pattern "+pat.getNumber()+"] ");
			}
		}
	}
	
	public boolean getPatternStatus(){
		return arePatternsUnpacked;
	}
	
	public Vector<ITInstrument> getInstruments(){
		if (!areInstrumentsUnpacked){ 
			System.out.println("ERROR: You must unpack this module's instruments before accessing them!");
			System.exit(0);
		}
		if(usesInstruments()==false){
			System.out.println("ERROR: This module has no instruments to unpack!");
			System.exit(0);
		}
		return instruments;
	}
	
	public Vector<ITPattern> getPatterns(){
		if (!arePatternsUnpacked){ 
			System.out.println("ERROR: You must unpack this module's patterns before accessing them!");
			System.exit(0);
		}
		return patterns;
	}
	
	public int seekLongOffset(String type){
		//positions the RandomAccessFile instance where it needs to be
		short address = 0;
		int offset = 0;
		switch(type){
			case "patterns": offset = longPatternOffset; break;
			case "instruments": offset = longInstrumentOffset; break;
			case "samples": offset = longSampleOffset; break;
			default:
				System.out.println("ERROR: cannot seek for '"+type+"'");
				System.exit(0);
				break;
		}
		try
		{
			//calculate & go to offset
			System.out.println(type+" address offset: "+offset);
			RAFile.seek(offset);
			
			//address is stored as little endian; let's read it as such
			byte[] byteaddress = new byte[2];
			RAFile.read(byteaddress);
			ByteBuffer bytebuffer = ByteBuffer.wrap(byteaddress);
			bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			//FINALLY getting the real address
			while(bytebuffer.hasRemaining()){
				address = bytebuffer.getShort();
				System.out.println(type+" address: "+address);
			}
			bytebuffer.clear();
			RAFile.seek(address);
		}
		catch(IOException ex){
			ex.printStackTrace();
			System.exit(0);
		}
		return (int) address;
	}
	
	public void unpackInstruments() throws IOException{
		System.out.println("UNPACKING INSTRUMENTS...");
		int instrumentsLeft = getInsNum();
		int nextPos = seekLongOffset("instruments");
		
		for(int i=0;i<instrumentsLeft;i++){
			ITInstrument instrument = new ITInstrument(nextPos, RAFile, i);
			instrument.unpack();
			instruments.add(instrument);
			nextPos = instrument.getPosition();
		}
		areInstrumentsUnpacked = true;
		System.out.println("Instruments successfully unpacked!");
	}
	
	public void unpackPatterns() throws IOException{
			System.out.println("UNPACKING PATTERNS...");
			int patternsLeft = getPatNum();
			int nextPos = seekLongOffset("patterns");
			
			for(int i=0;i<patternsLeft;i++){
				ITPattern pattern = new ITPattern(nextPos, RAFile, i);
				pattern.unpack();
				patterns.add(pattern);
				nextPos = pattern.getPosition();
			}
			makeOrders();
			arePatternsUnpacked = true;
			System.out.println("Patterns successfully unpacked!");
	}
	
	public int getInitialTempo() throws IOException{
		int tempo = 0;
		RAFile.seek(51);
		tempo = (int)(RAFile.read());
		return tempo;
	}
	
	public int getInitialSpeed() throws IOException{
		int speed = 0;
		RAFile.seek(50);
		speed = (int)(RAFile.read());
		return speed;
	}
	
	public int getPatNum() throws IOException{
		int patnum = 0;
		RAFile.seek(38);
		patnum = (int)(RAFile.read());
		return patnum;
	}
	
	public int getSmpNum() throws IOException{
		int smpnum = 0;
		RAFile.seek(36);
		smpnum = (int)(RAFile.read());
		return smpnum;
	}
	
	public int getInsNum() throws IOException{
		int insnum = 0;
		RAFile.seek(34);
		insnum = (int)(RAFile.read());
		return insnum;
	}
	
	public int getOrdNum() throws IOException{
		int ordnum = 0;
		RAFile.seek(32);
		ordnum = (int)(RAFile.read());
		return ordnum;
	}
	
	public String getName() throws IOException{
		String name = "";
		RAFile.seek(4);
		for (int i=0;i<26;i++){
			name += (char)(RAFile.read());
		}
		return name;
	}
	
	public void checkModule() throws IOException{
		try
		{
			//is this file a valid .it file?
			String impulse = "";
			for(var i=0;i<4;i++){
				impulse += (char)(RAFile.read());
			}
			if(!impulse.equals("IMPM")){
				System.out.println("ERROR: file is not a valid .it file!");
				System.exit(0);
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
			System.exit(0);
		}
	}
		
}