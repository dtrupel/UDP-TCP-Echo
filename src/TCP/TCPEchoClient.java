package TCP;

import abs.ClientNetworkingLayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TCPEchoClient extends ClientNetworkingLayer {

    private static final int LOCAL_PORT = 0;
    private Socket socket;

    protected TCPEchoClient(String remoteIp, int remotePort, int bufferSize) {
        super(LOCAL_PORT, remoteIp, remotePort, bufferSize, 0);
    }

    public static void main(String[] args) {
        scanArgumentsFormat(args, 3);
        TCPEchoClient client = new TCPEchoClient(args[0], parseArgumentToInteger(args[1]),
                parseArgumentToInteger(args[2]));
        client.connect();
        client.start();
    }

    @Override
    protected void start() {

        try(InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            Scanner userInput = new Scanner(System.in)) {

            System.out.print("Enter a message to echo: "); //Enter message and send to server
            String message = userInput.nextLine();
            output.write(message.getBytes(StandardCharsets.UTF_8), 0, message.length()); //Write to server

            StringBuffer sb = new StringBuffer();
            do {
                int readSize = input.read(buffer); //Get number of bytes read
                if(readSize < 1)
                    break;
                String received = new String(buffer, 0, readSize); //Get string from buffer
                sb.append(received);
            }while(input.available() != -1); //Input exists

            String echoedMessage = sb.toString();
            System.out.println("Echoed message: " + echoedMessage);

            if(echoedMessage.compareTo(message) == 0)
                System.out.println("Sent and received " + message.length() + " bytes.");
            else
                System.out.println("Messages are not equal!");

        }catch(IOException e) {
            System.err.println("An error with I/O occurred: " + e.getMessage());
            System.exit(1);
        }finally {
            disconnect();
        }
    }

    @Override
    public void connect() {
        try {
            socket = new Socket();
            socket.bind(localAddress);
            socket.connect(remoteAddress);
        }catch(SocketException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    protected void disconnect() {
        try {
            socket.close();
        }catch(Exception e) {
            System.err.println("An error occurred with closing the socket: " + e.getMessage());
            System.exit(1);
        }
    }
}
