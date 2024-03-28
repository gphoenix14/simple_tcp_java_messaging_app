import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Client <server-ip> <port> <username>");
            System.exit(1);
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        try (Socket socket = new Socket(serverIp, port);
                Scanner userInput = new Scanner(System.in);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server. Start typing messages (type 'exit' to quit).");

            Thread serverListener = new Thread(() -> {
                try (Scanner in = new Scanner(socket.getInputStream())) {
                    while (in.hasNextLine()) {
                        System.out.println(in.nextLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();

            while (true) {
                String message = userInput.nextLine();
                out.println(username + ": " + message);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
