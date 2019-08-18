import java.util.Scanner;

import java.io.*;
import java.io.BufferedReader;
import java.net.URL;

public class FileIO {

	public static void main(String[] args) throws IOException {
		
		//Check if RAF has already been created
		URL path1 = FileIO.class.getResource("CoordinatesFile.txt");

		if(path1 != null) { 
			FileIO.FindProbe();
			
		}
		else {
			System.out.println("Organizing Data, please wait...");
			FileIO.BuildRAF();
			//Call function to build RAF
			FileIO.FindProbe();

		}

	}
	
	public static void BuildRAF() throws  IOException{
		URL path2 = FileIO.class.getResource("probes.txt");
		File f = new File(path2.getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));

		String curLine = null;
		String chrm = null;
		String start = null;
		String end = null;
		String value = null;

        curLine = reader.readLine();
        
        //initialize two RandomAccessFiles, one for chromosome coordinates, and one just for the value
        RandomAccessFile coordinatesfile = new RandomAccessFile("CoordinatesFile.txt", "rw");
        RandomAccessFile valuefile = new RandomAccessFile("valuefile.txt", "rw");

	    int i = 0;
	    int curchrm = 0;
	    int curchrmcnt = 1;
	    long[] chrompositions = new long[25];
	    int lastchrm = 0;
	    int curchrmtst = 0;
	    while((curLine = reader.readLine()) != null){
	   
	        //Parse input by tab
	        String[] splitted = curLine.split("\t");
	         chrm = splitted[0].trim();
	         start = splitted[1].trim();
	         end = splitted[2].trim();
	         value = splitted[3].trim();
	        i = i+1;
	        //Extract current chromosome
	        //Handle X/Y non-numeric cases
	        	if(chrm.charAt(3) == 'X'){
	        		curchrm = 23;   
	        	} else if (chrm.charAt(3) == 'Y'){
	        		curchrm = 24;   
	        	}
	        	else {
	        		curchrm = Integer.parseInt(chrm.replaceAll("[\\D]", ""));
	        	}
	        
	        if (curchrm != lastchrm) {
	        		chrompositions[curchrmcnt] = coordinatesfile.getFilePointer();
	        		curchrmcnt++;
	        }
	        lastchrm = curchrm;
	        coordinatesfile.writeDouble(Double.parseDouble(start));
	        coordinatesfile.writeDouble(Double.parseDouble(end));
	        valuefile.writeDouble(Double.parseDouble(value));
	        valuefile.writeDouble(Double.parseDouble(value));


	}
reader.close();
coordinatesfile.close();
valuefile.close();
RandomAccessFile chromseekfile = new RandomAccessFile("chromseekfile.txt", "rw");

for(int j = 1; j < chrompositions.length; j++){
	chromseekfile.writeLong(chrompositions[j]);
}
chromseekfile.close();
	}
	
	public static void FindProbe() throws  IOException{
	    //accept query + search here
		
		Scanner input = new Scanner(System.in);
		System.out.println("Enter Query");
	    String queryString = input.next();
	    
	    double startchr;
		 double endchr;
		 double startcoord;
		 double endcoord;
	    
		    String startend1[] = queryString.split("-"); 
		    String start1 = startend1[0]; 
		    String end1 = startend1[1];
		
		    String startchrcoord[] = start1.split(":"); 
		    String startchrstring = startchrcoord[0]; 
		    String startcoordstring = startchrcoord[1];
		    //Handle X/Y non-numeric cases
        	if(startchrstring.charAt(3) == 'X'){
        		startchr = 23;   
        	} else if (startchrstring.charAt(3) == 'Y'){
        		startchr = 24;   
        	}
        	else {
    	    	startchr =  Double.parseDouble(startchrstring.replaceAll("[\\D]", ""));
        	}
	    	startcoord =  Double.parseDouble(startcoordstring);

		    
		    if (end1.indexOf(':') > 0){
		    	String startend2[] = end1.split(":"); 
			    String start2 = startend2[0]; 
			    String end2 = startend2[1];
		    	endcoord = Double.parseDouble(end2);
		    	if(start2.charAt(3) == 'X'){
		    		endchr = 23;   
	        	} else if (start2.charAt(3) == 'Y'){
	        		endchr = 24;   
	        	}
	        	else {
	        		endchr =  Double.parseDouble(start2.replaceAll("[\\D]", ""));
	        	}

	    }
		    else {
		    	 endcoord = Double.parseDouble(end1);
		    	 endchr = startchr;
		    }
	    
	    URL chromseekfileURL = FileIO.class.getResource("chromseekfile.txt");
		File chromseekfilef = new File(chromseekfileURL.getFile());
		RandomAccessFile chromseekfile = new RandomAccessFile(chromseekfilef, "rw");
		
		URL coordfilepath = FileIO.class.getResource("CoordinatesFile.txt");
		File coordfile = new File(coordfilepath.getFile());
		RandomAccessFile coordinatesfile = new RandomAccessFile(coordfile, "rw");
		chromseekfile.seek(0);
		long [] chrompositionsarr = new long [25];
		for (int k = 1; k < 25; k++){
			chrompositionsarr[k] = chromseekfile.readLong();

		}
		URL valuefilepath = FileIO.class.getResource("valuefile.txt");
		File valuefilef = new File(valuefilepath.getFile());
		RandomAccessFile valuefile = new RandomAccessFile(valuefilef, "rw");
		
		chromseekfile.seek(8*(int)(startchr-1));
		Long startsearch = chromseekfile.readLong();
		coordinatesfile.seek(startsearch);
		
		
		chromseekfile.seek(8*(int)endchr);
		Long endsearch = chromseekfile.readLong()-8;
		int currentchromosome = (int)startchr;
		
		double SearchCoord;
			while(coordinatesfile.getFilePointer() <= endsearch){
				
				SearchCoord = coordinatesfile.readDouble();
				if(coordinatesfile.getFilePointer() > chrompositionsarr[currentchromosome+1]){
					currentchromosome++;
					}
				if(startchr == endchr){
					if ((SearchCoord >= startcoord) && (SearchCoord <= endcoord) ){
						if((coordinatesfile.getFilePointer() / 8) % 2 == 0){
							coordinatesfile.seek(coordinatesfile.getFilePointer()-16);
							valuefile.seek(coordinatesfile.getFilePointer());
							System.out.printf("chr%d: ", (int)currentchromosome);
							System.out.print(coordinatesfile.readDouble()+ " ");
							System.out.print(coordinatesfile.readDouble()+ " ");
							System.out.println(valuefile.readDouble());
								} else {
									coordinatesfile.seek(coordinatesfile.getFilePointer()-8);
									valuefile.seek(coordinatesfile.getFilePointer());
									System.out.printf("chr%d: ", (int)currentchromosome);
									System.out.print(coordinatesfile.readDouble()+" ");
									System.out.print(coordinatesfile.readDouble()+" ");
									System.out.println(valuefile.readDouble());
								}
				}
				}
				else {
					if(currentchromosome==startchr){
						if (SearchCoord >= startcoord){

							if((coordinatesfile.getFilePointer() / 8) % 2 == 0){
								coordinatesfile.seek(coordinatesfile.getFilePointer()-16);
								valuefile.seek(coordinatesfile.getFilePointer());
								System.out.printf("chr%d: ", (int)currentchromosome);
								System.out.print(coordinatesfile.readDouble()+ " ");
								System.out.print(coordinatesfile.readDouble()+ " ");
								System.out.println(valuefile.readDouble());
									} else {
										coordinatesfile.seek(coordinatesfile.getFilePointer()-8);
										valuefile.seek(coordinatesfile.getFilePointer());
										System.out.printf("chr%d: ", (int)currentchromosome);
										System.out.print(coordinatesfile.readDouble()+" ");
										System.out.print(coordinatesfile.readDouble()+" ");
										System.out.println(valuefile.readDouble());
									}
					}
					}
					else if((currentchromosome>startchr) && (currentchromosome < endchr)){
						if((coordinatesfile.getFilePointer() / 8) % 2 == 0){
							coordinatesfile.seek(coordinatesfile.getFilePointer()-16);
							valuefile.seek(coordinatesfile.getFilePointer());
							System.out.printf("chr%d: ", (int)currentchromosome);
							System.out.print(coordinatesfile.readDouble()+ " ");
							System.out.print(coordinatesfile.readDouble()+ " ");
							System.out.println(valuefile.readDouble());
								} else {
									coordinatesfile.seek(coordinatesfile.getFilePointer()-8);
									valuefile.seek(coordinatesfile.getFilePointer());
									System.out.printf("chr%d: ", (int)currentchromosome);
									System.out.print(coordinatesfile.readDouble()+" ");
									System.out.print(coordinatesfile.readDouble()+" ");
									System.out.println(valuefile.readDouble());
								}
					}
					else if(currentchromosome == endchr){
						if(SearchCoord <= endcoord){
							if((coordinatesfile.getFilePointer() / 8) % 2 == 0){
								coordinatesfile.seek(coordinatesfile.getFilePointer()-16);
								valuefile.seek(coordinatesfile.getFilePointer());
								System.out.printf("chr%d: ", (int)currentchromosome);
								System.out.print(coordinatesfile.readDouble()+ " ");
								System.out.print(coordinatesfile.readDouble()+ " ");
								System.out.println(valuefile.readDouble());
									} else {
										coordinatesfile.seek(coordinatesfile.getFilePointer()-8);
										valuefile.seek(coordinatesfile.getFilePointer());
										System.out.printf("chr%d: ", (int)currentchromosome);
										System.out.print(coordinatesfile.readDouble()+" ");
										System.out.print(coordinatesfile.readDouble()+" ");
										System.out.println(valuefile.readDouble());
									}
						}
					}
					
				}
				
			}
			coordinatesfile.close();
			valuefile.close();
	}
}



