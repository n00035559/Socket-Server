import java.net.*;
import java.io.*;
import java.util.Scanner;

public class IterativeSocketServer {

    // Helper method for running the system commands
    private static String runCmd(String... args) {
        try {
            // ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true); // merge stderr into stdout
            // Start the process
            Process p = pb.start();

            // StringBuilder for bringing all text outputs together
            StringBuilder out = new StringBuilder();
            // Wrap
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String s;
                while ((s = r.readLine()) != null) { // Reads line until nothing left
                    out.append(s).append('\n');
                }
            }
            // Wait for command to finish running with exit code
            int code = p.waitFor();
            if (code != 0) {
                out.append("\n(Command exited with code ").append(code).append(")\n");
            }
            return out.toString(); // Return all output
        } catch (Exception e) { // Error handling
            return "ERROR executing command: " + e.getMessage() + "\n";
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        Scanner sc = new Scanner(System.in);
        // Query user for port
        System.out.println("Enter your desired port number: ");
        int port = sc.nextInt();

        // Create the ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) { // accepts the client
                    socket.setSoTimeout(30_000); // safety
                    System.out.println("New connection from " + socket.getInetAddress());

                    // Read Data as a string
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    // Read in one client from the user
                    String line = reader.readLine();
                    // Error Handling
                    if (line == null) {
                        writer.println("ERROR: empty request");
                        continue;
                    }

                    String command = line.trim().toUpperCase();

                    switch (command) {
                        case "DATE_TIME" -> writer.print(runCmd("sh", "-c", "date"));

                        case "UPTIME" -> writer.print(runCmd("sh", "-c", "uptime"));

                        // 'vm_stat' for macOS or 'free -h' for Linux;
                        case "MEMORY_USE" -> writer.print(runCmd("sh", "-c", "free -h"));

                        case "NETSTAT" -> writer.print(runCmd("sh", "-c", "ss -tuln"));

                        case "CURRENT_USERS" -> writer.print(runCmd("sh", "-c", "who"));

                        case "RUNNING_PROCESSES" -> writer.print(runCmd("sh", "-c", "ps aux"));

                        default -> writer.println("ERROR: Unknown operation '" + command + "'. "
                                + "Supported: DATE_TIME, UPTIME, MEMORY_USE, NETSTAT, CURRENT_USERS, RUNNING_PROCESSES");
                    }

                    // ensure a newline + flush for good measure
                    writer.println();
                    writer.flush();

                    // close the socket
                    socket.close();
                    System.out.println("Connection with " + socket.getInetAddress() + " closed.");
                }
            }
        }
    }
}
