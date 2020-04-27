package processes;

import java.util.Scanner;

public class Entry {
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);  
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].equals("debug")) {
				
				 System.out.println("1 IISNearestCity");
				 System.out.println("2 WeatherAtCity");
				 
				 int selection = in.nextInt();
				 in.reset();
		
				 switch (selection) {
					 case 1: {
						 new ISSNearestCity(in);
						 break;
					 }
					 case 2: {
						 new WeatherAtCity(in);
						 break;
					 }
				 }
				 
			} else if(args[i].equals("ISSNearestCity")) {
				
				System.out.println("Executing: " + args[i]);
				new ISSNearestCity(in);
				
			} else if (args[i].equals("WeatherAtCity")) {
				
				System.out.println("Executing: " + args[i]);
				new WeatherAtCity(in);
				
			} else {
				System.out.println("No matching endpoint, quitting");
			}
			
		}
		
	}



}
