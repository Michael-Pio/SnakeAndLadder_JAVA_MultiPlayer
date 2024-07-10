
package Game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoadMap {

    // ANSI color codes
    public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_MAGENTA = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";


    // ANSI color codes for background color
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_MAGENTA = "\u001B[45m";

    public Board currentBoard;


    public LoadMap(Board board){
        this.currentBoard = board;
    }
    public String ReadRawFromDisk(String filePath) {
        StringBuffer sb = new StringBuffer();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String ReadRawFromDisk(){
        return ReadRawFromDisk(currentBoard.file);
    }


    public String AddColor(String nonColordString) {
        StringBuffer sb = new StringBuffer();

        // split nonColordString into lines
        String[] lines = nonColordString.split("\n");

        for (String line : lines) {
                // Replace ## with red color
                line = line.replaceAll("##", ANSI_RED + "##" + ANSI_RESET);
                line = line.replaceAll("@", ANSI_YELLOW + "@" + ANSI_RESET);
                line = line.replaceAll("#>", ANSI_RED + "#>" + ANSI_RESET);
                line = line.replaceAll("<#", ANSI_RED + "<#" + ANSI_RESET);

                
                // Replace <H> with green color
                line = line.replaceAll("<H>", ANSI_GREEN + "<H>" + ANSI_RESET);


                line = line.replaceAll("/-/", ANSI_GREEN +"/-/"+ ANSI_RESET );
                line = line.replaceAll("/-", ANSI_GREEN +"/-"+ ANSI_RESET );
                line = line.replaceAll("-/", ANSI_GREEN +"-/"+ ANSI_RESET );
                line = line.replaceAll("/", ANSI_GREEN +"/"+ ANSI_RESET );

                line = line.replaceAll("\\\\\\-\\\\", ANSI_GREEN + "\\\\-\\\\" + ANSI_RESET);


                line = line.replaceAll("<P1>", ANSI_YELLOW + BACKGROUND_CYAN + "<P1>" + ANSI_RESET);
                // Replace P2 with blue text and yellow background
                line = line.replaceAll("<P2>", ANSI_BLUE + BACKGROUND_YELLOW + "<P2>" + ANSI_RESET);
                // Replace P3 with cyan text and green background
                line = line.replaceAll("<P3>", ANSI_CYAN + BACKGROUND_GREEN + "<P3>" + ANSI_RESET);
                // Replace P4 with green text and magenta background
                line = line.replaceAll("<P4>", ANSI_GREEN + BACKGROUND_MAGENTA + "<P4>" + ANSI_RESET);

                // System.out.println(line);
                sb.append(line).append("\n");
            }

        return sb.toString();
    }
}

