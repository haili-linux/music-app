package com.my.musicPlayer;
import java.io.*;

public class myFile
{
	final static String appDatePath="/storage/emulated/0/Android/data/com.my musicPlayer";
	final static String appDatePath2="/storage/emulated/0/Android/data/com.my musicPlayer/lyrics";

	public static void createMPath(){
		File path = new File(appDatePath);
		if(path.exists()&&path.isDirectory())
			;
		else
			path.mkdir();
	   
		File path2 = new File(appDatePath2);
		if(path2.exists()&&path2.isDirectory())
			;
		else
			path2.mkdir();
	}
	
	public static String readFileToStr(String fileName){
		File file = new File(fileName);
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    sb.append(text);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
	
	public static boolean writeToFile(String date,String filename)
	{  
		File OutputLrcFile=new File(filename);
		FileWriter fw;
		try {
			fw=new FileWriter(OutputLrcFile);
			String LrcString = date;
			fw.write(LrcString);
			fw.close();
			return true;
		}
		catch (IOException e)
		{ 
			e.printStackTrace(); 
			return false;
		}  
	}
	
	public static void copyFile(String filenme,String newfilename){
		 writeToFile(readFileToStr(filenme),newfilename);
	}
}
