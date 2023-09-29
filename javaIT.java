import javatrax.*;

import java.util.Scanner;
import java.io.File;
import java.lang.NullPointerException;
import java.io.IOException;
import java.util.Vector;

public class javaIT { 
	public static File file;
	
    public static void main(String[] args) throws IOException{
        System.out.print(".it file: ");
		Scanner scanner = new Scanner(System.in);
		try
		{
			file = new File(scanner.nextLine());
			ITModule module = new ITModule(file);
			System.out.println("name: "+module.getName());
			System.out.println("ordnum: "+module.getOrdNum()+" (real # is 0 based)");
			System.out.println("insnum: "+module.getInsNum());
			System.out.println("smpnum: "+module.getSmpNum());
			System.out.println("patnum: "+module.getPatNum()+" (real # is 0 based)");
			System.out.println("initial speed: "+module.getInitialSpeed());
			System.out.println("initial tempo: "+module.getInitialTempo());
			module.unpackPatterns();
		}
		catch(NullPointerException ex){
			ex.printStackTrace();
			System.exit(0);
		}
		
    }
}
