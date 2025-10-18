import java.net.*;
import java.io.*;
import java.util.Scanner;

public class IterativeSocketServer {

    //Helper method for running the system commands
    private static String runCmd(String... args) {
        try {
            //ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true); // merge stderr into stdout
            //Start the process
            Process p = pb.start();

            //StringBuilder for bringing all text outputs together
            StringBuilder out = new StringBuilder();
            //Wrap
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String s;
                while ((s = r.readLine()) != null) { //Reads line until nothing left
                    out.append(s).append('\n');
                }
            }
            //Wait for command to finish running with exit code
            int code = p.waitFor();
            if (code != 0) {
                out.append("\n(Command exited with code ").append(code).append(")\n");
            }
            return out.toString(); //Return all output 
        } catch (Exception e) { //Error handling
            return "ERROR executing command: " + e.getMessage() + "\n";
        }
    }


    public static void main(String[] args) throws java.io.IOException {
        Scanner sc = new Scanner(System.in);
        //Query user for port
        System.out.println("Enter your desired port number: ");
        int port = sc.nextInt();

        //Create the ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) { //accepts the client
                    socket.setSoTimeout(30_000); //safety
                    System.out.println("New connection from " + socket.getInetAddress());


                    //Read Data as a string
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    // Read in one client from the user
                    String line = reader.readLine();
                    //Error Handling
                    if (line == null){writer.println("ERROR: empty request");
                        continue;
                    }

                    String command = line.trim().toUpperCase();

                    switch (command) {
                        case "DATE_TIME":
                            writer.print(runCmd("cmd.exe", "/c", "echo %DATE% %TIME%"));
                            break;

                        case "UPTIME":
                            // Uptime info is shown by 'net stats srv' (look for "Statistics since...")
                            writer.print(runCmd("cmd.exe", "/c", "net stats srv"));
                            break;

                        case "MEMORY_USE":
                            // A quick summary pulled from systeminfo; findstr filters the lines
                            writer.print(runCmd("cmd.exe", "/c", "systeminfo | findstr /I \"Memory\""));
                            break;

                        case "NETSTAT":
                            writer.print(runCmd("cmd.exe", "/c", "netstat -ano"));
                            break;

                        case "CURRENT_USERS":
                            // Lists users who have sessions on the machine/domain context
                            writer.print(runCmd("cmd.exe", "/c", "query user"));
                            break;

                        case "RUNNING_PROCESSES":
                            writer.print(runCmd("cmd.exe", "/c", "tasklist"));
                            break;

                        default:
                            writer.println("ERROR: Unknown operation '" + command + "'. "
                                    + "Supported: DATE_TIME, UPTIME, MEMORY_USE, NETSTAT, CURRENT_USERS, RUNNING_PROCESSES");
                            break;
                    }
// ensure a newline + flush for good measure
                    writer.println();
                    writer.flush();

                }
            }
            //close the socket
        }
    }
}
