package util;

import java.io.FileWriter;
import java.io.IOException;

public class FileManager {


    public void writeLog(String message) {


        StringBuilder log = new StringBuilder();


        log.append("ScholarMatch Log : ");
        log.append(message);
        log.append("\n");


        try {


            FileWriter writer = new FileWriter("data/logs.txt", true);

            writer.write(log.toString());

            writer.close();


        }
        catch(IOException e) {


            System.out.println("File Error : " + e.getMessage());


        }


    }


}