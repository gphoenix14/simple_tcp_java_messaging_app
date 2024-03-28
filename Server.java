
// Importa le classi necessarie per gestire input/output e networking.
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

// Dichiarazione della classe Server.
public class Server {
    // Porta su cui il server ascolterà le connessioni in entrata.
    private static final int PORT = 8000;
    // Insieme dei PrintWriter, uno per ogni client connesso, per inviare messaggi.
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    // Metodo main, punto di ingresso del programma.
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Prova ad aprire un ServerSocket sulla porta
                                                                   // specificata.
            System.out.println("Server avviato sulla porta " + PORT); // Stampa di conferma avvio server.
            while (true) { // Ciclo infinito per accettare connessioni in continuazione.
                new ClientHandler(serverSocket.accept()).start(); // Crea e avvia un nuovo thread per ogni connessione
                                                                  // accettata.
            }
        } catch (IOException e) { // Cattura eccezioni di I/O.
            e.printStackTrace(); // Stampa lo stack trace delle eccezioni catturate.
        }
    }

    // Classe interna ClientHandler che gestisce le connessioni client.
    private static class ClientHandler extends Thread {
        private Socket clientSocket; // Socket del client.
        private PrintWriter out; // PrintWriter per inviare dati al client.

        // Costruttore che accetta il socket del client.
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // Metodo run eseguito quando il thread è avviato.
        @Override
        public void run() {
            try {
                Scanner in = new Scanner(clientSocket.getInputStream()); // Scanner per leggere dati dal client.
                out = new PrintWriter(clientSocket.getOutputStream(), true); // PrintWriter per inviare dati al client,
                                                                             // con auto-flush.
                clientWriters.add(out); // Aggiunge il PrintWriter all'insieme di client.

                while (true) { // Ciclo infinito per leggere i messaggi in entrata.
                    String message = in.nextLine(); // Legge la prossima riga di testo inviata dal client.
                    if (message.equalsIgnoreCase("exit")) { // Se il messaggio è "exit", termina il ciclo.
                        break;
                    }
                    broadcast(message); // Invia il messaggio ricevuto a tutti i client connessi.
                }
            } catch (IOException e) { // Cattura eccezioni di I/O.
                e.printStackTrace(); // Stampa lo stack trace delle eccezioni catturate.
            } finally { // Blocco finally eseguito dopo il try o il catch.
                if (out != null) {
                    clientWriters.remove(out); // Rimuove il PrintWriter dall'insieme dei client se non è null.
                }
                try {
                    clientSocket.close(); // Prova a chiudere il socket del client.
                } catch (IOException e) { // Cattura eccezioni di I/O.
                    e.printStackTrace(); // Stampa lo stack trace dell'eccezione.
                }
            }
        }

        // Metodo per inviare un messaggio a tutti i client connessi.
        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) { // Itera su tutti i PrintWriter dei client.
                writer.println(message); // Invia il messaggio al client.
            }
        }
    }
}
