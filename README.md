# javatrax
Small bundle of classes for handling Impulse Tracker (.IT) music files in Java.

DISCLAIMER: I started this little project for fun. I'm not incredibly experienced with Java, so i'm sure that I haven't done things in the best or most effecient way, so beware of that.

# usage
To get started, create an instance of the ITModule class with your .it module of choice passed in as an argument (as a File object):
```java
File myFile = new File("ITfilename.it");
ITModule module = new ITModule(myFile);
```
At this point, you can already get some basic info from the module:
```java
String songName = module.getName(); //the name of the song
int songTempo = module.getInitialTempo(); //the inital tempo of the song
int songSpeed = module.getInitialSpeed(); //the inital speed of the song
int totalPatterns = module.getPatNum(); //number of patterns in the song
int totalInstruments = module.getInsNum(); //number of patterns in the song
```
However, if you want to access things like patterns and instruments, you have to unpack the data in order to access them:
```java
module.unpackPatterns();
module.unpackInstruments();
```
If you do not run these methods before trying to access patterns or instruments, you will get an error.

Patterns and Instruments have their own classes, and their instances are stored within modules using Vectors. You can use these methods to retrieve said Vectors:
```java
//the getPatterns() and getInstruments() methods return a Vector (array of objects) of ITPattern and ITInstrument respectively.
Vector<ITPattern> patterns = module.getPatterns();
ITPattern pattern = patterns.get(2); //getting a specific pattern from the vector. in this case, we're getting pattern 2.
Vector<ITInstrument> instruments = module.getInstruments();
ITInstrument instrument = instruments.get(2); //getting a specific instrument from the vector. in this case, we're getting instrument 2.

//the getOrders() method also returns an ITPattern Vector, but the patterns are returned in sequence order.
Vector<ITPattern> orders = module.getOrders();
```
Patterns are broken up into Rows and Columns (channels). You can retrieve them like so:
```java
Vector<ITRows> rows = pattern.getRows(); //getting the ITRow vector
ITRow row = rows.get(5); //getting a row from the vector. in this case, we're getting row 5.
Vector<ITColumn> columns = row.getColumns(); //getting the ITColumn vector.
```
Columns (channels) are the lowest level of data in the hierarchy. Columns contain note data, volume/panning data, instrument data, etc:
```java
ITColumn column = columns.get(2); //Get the 2nd channel stored in the vector (not necessarily channel 2: see below).
/*
Because of how Impulse Tracker stores its modules' data, trying to get the data directly can be risky, since it might not always exist.
For example, if channel 1 has no new data on row X, but channel 2 does, channel 1 is essentially skipped over, so channel 1 would not appear in the columns vector.
Because of this, you must use certain methods to safely get data.
*/
if(column.hasChannel()){ //if this channel has any new data at the current row
  int channel = column.getChannel(); //get the channel that this column corresponds to
  if(column.hasNote()) int note = column.getNote(); //get new note data if it exists
  if(column.hasVolPan()) int volpan = column.getVolPan(); //get new volume/panning data if it exists
  if(column.hasInstrument()) int instrument = column.getInstrument(); //get new instrument data if it exists
  if(column.hasEffect()){ //if this channel has a new effect at the current row
    int effectType = column.getEffectType(); //get the type of effect (volume fade, portamento, etc)
    int effectParams = column.getEffectParameters(); //get the parameters for the effect
  }
}
```
Here's some code that demonstrates how one would loop through a pattern, getting its data along the way:
```java
Vector<ITPattern> patterns = module.getPatterns();
ITPattern pattern = patterns.get(0); //only looping thru 1 pattern for this example
Vector<ITRow> rows = pattern.getRows();
for(int i=0;i<rows.size();i++)
{
  ITRow row = rows.get(i);
  Vector<ITColumn> columns = row.getColumns();
  for(int j=0;j<columns.size();j++)
  {
    ITColumn column = columns.get(j);
    if(column.hasChannel()){
      if(column.hasNote()) int newnote = column.getNote();
      if(column.hasVolPan()) int newvolpan = column.getVolPan();
      if(column.hasInstrument()) int newins = column.getInstrument();
      if(column.hasEffect()){
        int neweffect = column.getEffectType();
        int neweffectparams = column.getEffectParameters();
      }
    }
  }
}
```
The 'javaIT' program is a demo program that takes in an .it module and displays some basic info about it, like the name, tempo, speed, etc:
```
.it file: snowf.it
name: Snowfall
ordnum: 46 (real # is 0 based)
insnum: 23
smpnum: 99
patnum: 37 (real # is 0 based)
initial speed: 4
initial tempo: 193
```
You can look at the .java files to see what the rest of the methods are and what they do. I may or may not make a Wiki page for this if I can be bothered to.
