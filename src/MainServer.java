import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URI;

import java.io.*;
import java.util.Scanner;


public class MainServer {

    //Notes to run
    //*****************************************************
    /*
        //TO RUN IN TERMINAL
        java MainServer -v -p 8080 -d /Users/Dave/Desktop/Classes/httpServer/src/Disk

        //COMMAND FOR POST
        curl -X POST --data "This is information for the file" localhost:8080/check

        //COMMAND FOR GET
        curl -X GET localhost:8080/foo

        //Main path
        Main path should be set to your working directory in order to function
    */
    //*****************************************************

    private static Boolean verbose = false;
    private static int port;
    private static String directoryPath = "";

    //Function which reads text file and returns contents as a string
    //*********************************************************************************
    private static String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
            }
            return fileContents.toString();
        }
    }
    //*********************************************************************************

    //Set the arguments provided in the terminal
    //*********************************************************************************
    static void setArguments(String args[])
    {
        for(int i =0; i < args.length; i++)
        {
            if(args[i].equals("-v"))
            {
                //Set Verbose to True
                verbose = true;
            }

            if (args[i].equals("-p"))
            {
                port = Integer.parseInt(args[i+1]);
            }

            if(args[i].equals("-d"))
            {
                directoryPath = args[i+1];
            }
        }
    }
    //*********************************************************************************

    //Main Function
    //*********************************************************************************
    public static void main(String[] args) throws IOException {

        setArguments(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(MainServer::handleRequest);
        server.start();
    }
    //*********************************************************************************


    // Handles all request that come into the server
    //*********************************************************************************
    private static void handleRequest(HttpExchange exchange) throws IOException {

        //Function Variables
        //*************************************************
        String response = "";
        String theFileContent;
        int responseCode = 200;
        //*************************************************

        //Setting Request URI Variables
        //*************************************************
        URI requestURI = exchange.getRequestURI();
        String theMethod = exchange.getRequestMethod();
        String createFileName = requestURI.getPath();
        //*************************************************

        //Setting the files in the file directory
        //************************************************************************************
        File diskPath = new File(directoryPath);
        //"/Users/Dave/Desktop/Classes/httpServer/src/Disk"
        File[] allNestedFiles = diskPath.listFiles(File::isFile);
        //************************************************************************************

        //Handling Different Cases for GET
        //************************************************************************************
        if(theMethod.equals("GET"))
        {
            if(createFileName.length() > 1)
            {
                //Getting Foo and it's content to return to the user
                //*******************************************************************************
                File fooFile = new File("/Users/Dave/Desktop/Classes/httpServer/src/Disk" + createFileName + ".txt");

                if(fooFile.exists())
                {
                    //Return the contents
                    response = "Here are the contents of the " + createFileName +" file: \n\n";
                    theFileContent = readFile("/Users/Dave/Desktop/Classes/httpServer/src/Disk" + createFileName+ ".txt");
                    response = response + theFileContent;
                }
                else
                {
                    //Return Error
                    response = "The "+ createFileName + " File Does Not Exist \n\n";
                    responseCode = 404;

                    //Not Actually doing this, doing this to throw error
                    fooFile.setReadable(false);

                    try {
                        theFileContent = readFile("/Users/Dave/Desktop/Classes/httpServer/src/Disk" + createFileName+ ".txt");
                    }
                    catch(IOException ex){

                        //Purposely Set
                        //***************************
                        if(verbose == true) {
                            response = response + ex.getMessage();
                        }
                        //***************************
                    }
                }
                //*******************************************************************************
            }
            else {

                //Listing the Files in the Disk Directory
                //******************************************
                response = "Here are the list of files of the data directory:\n\n";

                for (int i = 0; i < allNestedFiles.length; i++) {
                    //System.out.println(allNestedFiles[i].getName());
                    response = response + allNestedFiles[i].getName() + "\n";
                }
                //******************************************

            }
        }
        //************************************************************************************


        //Handling the POST request
        //************************************************************************************
        if(theMethod.equals("POST"))
        {
            //Fetch File
            File file = new File("/Users/Dave/Desktop/Classes/httpServer/src/Disk"+createFileName+".txt");

            //Create the file
            if (file.createNewFile()) {
                if(verbose == true) {
                    response = response + "File is created! \n";
                    System.out.println("File is created!");
                }
            } else {
                if(verbose == true) {
                    response = response + "File already exists \n";
                    System.out.println("File already exists.");
                }
            }

            //*********************************************
            //Getting Body Data
            //*********************************************
            StringBuilder sb = new StringBuilder();
            InputStream ios = exchange.getRequestBody();
            int i;
            while ((i = ios.read()) != -1) {
                sb.append((char) i);
            }
            //*********************************************

            //*******************************************
            //Writing to the file
            //*******************************************
            FileWriter writer = new FileWriter(file);
            writer.write(sb.toString());
            writer.close();
            //*******************************************
        }
        //************************************************************************************

        //Building the response
        //************************************************************************************
        exchange.sendResponseHeaders(responseCode, response.getBytes().length);//response code and length

        //Setting the response text type, so that it reads end of line chars
        String encoding = "UTF-8";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
        exchange.getResponseHeaders().set("Content-Disposition:", "inline");

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        //************************************************************************************
    }
}
