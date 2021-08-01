package TCP;
import TCP.TCPServerThread;
import abs.NetworkingLayer;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPEchoServer extends NetworkingLayer {

    private static ServerSocket serverSocket;

    protected TCPEchoServer(int localPort, int bufferSize) {
        super(localPort, bufferSize);
    }

    public static void main(String[] args) {
        scanArgumentsFormat(args, 2);
        TCPEchoServer server = new TCPEchoServer(parseArgumentToInteger(args[0]), parseArgumentToInteger(args[1]));
        server.connect();
        server.start();
    }

    @Override
    protected void start() {
        while(true) {
            try {
                new Thread(new TCPServerThread(serverSocket.accept(), bufferSize)).start();
            }catch(Exception e) {
                System.err.println("Server interrupted error: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    @Override
    protected void connect() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(localAddress);
        }catch(IOException e) {
            System.err.println("Something went wrong with connecting to the server : " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    protected void disconnect() {
       try {
           serverSocket.close();
       }catch(IOException e) {
           System.err.println("Server was interrupted on shutdown: " + e.getMessage());
           System.exit(1);
       }
    }
}
