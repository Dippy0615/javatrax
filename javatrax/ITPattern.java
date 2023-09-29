package javatrax;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class ITPattern
{
	private int startingAddress;
	private int position;
	private int number;
	private RandomAccessFile RAFile;
	private Vector<ITRow> rows;
	private boolean hasUnpacked;
	
	public ITPattern(int address, RandomAccessFile file, int num)
	{
		startingAddress = address;
		position = address;
		RAFile = file;
		rows = new Vector<ITRow>();
		number = num;
		hasUnpacked = false;
	}
	
	public int getNumber(){
		return number;
	}
	
	public boolean isUnpacked(){
		return hasUnpacked;
	}
	
	public Vector<ITRow> getRows(){
		if (!hasUnpacked){ 
			System.out.println("ERROR: You must unpack this pattern before accessing its rows!");
			System.exit(0);
		}
		return rows;
	}
	
	public int getPosition(){
		return position;
	}
	
	//unpack this pattern
	//sets the position variable where the RandomAcessFile left off
	public void unpack() throws IOException{
		long filechanpos = 0;
		RAFile.seek(startingAddress);
		RAFile.skipBytes(2);
		int totalRows = RAFile.read();
		RAFile.skipBytes(5);
		
		int[] masks = new int[64];
		int[] lastnote = new int[64];
		int[] lastinstrument = new int[64];
		int[] lastvolpan = new int[64];
		int[] lasteffect1 = new int[64];
		int[] lasteffect2 = new int[64];
		int row = 0;
		int channel = 0;
		
		HashMap<String, Integer> map = new HashMap<>();
		ITRow rowOBJ;
		ITColumn column;
		Vector<ITColumn> columnQueue = new Vector<ITColumn>();
		while(true)
		{
			int channel_data = RAFile.read();

			if (channel_data==0){ //end of row
				rowOBJ = new ITRow(columnQueue);
				rows.add(rowOBJ);
				columnQueue.clear();
				row++;
			}
			
			if(row==totalRows){
				filechanpos = RAFile.getChannel().position();
				hasUnpacked = true;
				break;
			}
			
			channel = (channel_data-1) & 63;
			if(channel_data>0)
			{
				map.put("channel", channel);
				
				if ((channel_data&128)>0) masks[channel] = RAFile.read();
				
				if((masks[channel]&1)>0){
					int note = RAFile.read();
					lastnote[channel] = note;
					map.put("note", note);
				}
				
				if((masks[channel]&2)>0){
					int ins = RAFile.read();
					lastinstrument[channel] = ins;
					map.put("instrument", ins);
				}
				
				if((masks[channel]&4)>0){
					int volpan = RAFile.read();
					lastvolpan[channel] = volpan;
					map.put("vol/pan", volpan);
				}
				
				if((masks[channel]&8)>0){
					int effect1 = RAFile.read();
					int effect2 = RAFile.read();
					lasteffect1[channel] = effect1;
					lasteffect2[channel] = effect2;
					map.put("effect1", effect1);
					map.put("effect2", effect2);
				}
				
				if((masks[channel]&16)>0){
					map.put("note", lastnote[channel]);
				}
				if((masks[channel]&32)>0){
					map.put("instrument", lastinstrument[channel]);
				}
				if((masks[channel]&64)>0){
					map.put("vol/pan", lastvolpan[channel]);
				}
				if((masks[channel]&128)>0){
					map.put("effect1", lasteffect1[channel]);
					map.put("effect2", lasteffect2[channel]);
				}
				column = new ITColumn(map);
				columnQueue.add(column);
				map.clear();
			}
		}
		position = (int) filechanpos;
	}
}