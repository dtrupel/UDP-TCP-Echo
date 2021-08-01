package TCP;

import abs.NetworkingLayer;

import java.io.*;
import java.net.Socket;

public class TCPServerThread extends NetworkingLayer implements Runnable {

    private final Socket remoteSocket;

    public TCPServerThread(Socket remoteSocket, int bufferSize) {
        super(remoteSocket.getLocalPort(), bufferSize);
        this.remoteSocket = remoteSocket;
    }

    @Override
    public void start() {
        try(InputStream input = remoteSocket.getInputStream();
            OutputStream output = remoteSocket.getOutputStream()) {
            StringBuffer sb = new StringBuffer();
            do {
                int read = input.read(buffer); //Get a number of read bytes
                String received = new String(buffer, 0, read); //Get string from buffer
                sb.append(received);
            }while(input.available() > 0); //Input exists
            String message = sb.toString();
            output.write(message.getBytes());
            System.out.println("Received from client: " + message);
            System.out.println("Received and sent " + message.length() + " bytes from " +
                    remoteSocket.getInetAddress().getHostAddress());
        }catch(IOException e) {
            System.out.println("An error with I/O occurred: " + e.getMessage());
            disconnect();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        System.out.println("Connection established with " + remoteSocket.getInetAddress().getHostAddress());
        start();
        System.out.println("Connection with " + remoteSocket.getInetAddress().getHostAddress() + " is closed.");
    }

    @Override
    protected void connect() {
        //Nothing to do here
    }

    @Override
    protected void disconnect() {
        try {
            remoteSocket.close();
        }catch(Exception e) {
            System.err.println("Problem occurred with closing the socket: " + e.getMessage());
            System.exit(1);
        }
    }
}
