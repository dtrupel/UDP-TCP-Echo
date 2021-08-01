package abs;

import exceptions.IllegalArgumentFormatException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class NetworkingLayer {

    private static final int MIN_PORT_VALUE = 0;
    private static final int MAX_PORT_VALUE = 65535;
    private static final int MIN_BUFFER_SIZE = 1;

    protected final int localPort;
    protected int bufferSize;
    protected byte [] buffer;

    protected SocketAddress localAddress;

    protected NetworkingLayer(int localPort) {
        try {
            if (!isValidPort(localPort))
                throw new IllegalArgumentException("Port " + localPort + " is out of range.");
        }catch(IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        this.localPort = localPort;
        try{
            localAddress = new InetSocketAddress(this.localPort);
        }catch(Exception e) {
            System.err.println("Error with the local port: " + e.getMessage());
            System.exit(1);
        }
    }

    protected NetworkingLayer(int localPort, int bufferSize) {
        this(localPort);
        try {
            if (!isValidBufferSize(bufferSize))
                throw new IllegalArgumentException("Error occurred. Minimum buffer value is 1.");
        }catch(IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        this.bufferSize = bufferSize;
        buffer = new byte [bufferSize];
    }

    protected boolean isValidPort(int port) {
        return port >= MIN_PORT_VALUE && port <= MAX_PORT_VALUE;
    }

    protected boolean isValidBufferSize(int bufferSize) {
        return bufferSize >= MIN_BUFFER_SIZE;
    }

    /*
    * Catch exception for invalid argument format
    * */
    protected static void scanArgumentsFormat(String [] args, int limit) {
        String formatMessage = getFormatMessage(limit);
        System.out.println(formatMessage);
        try {
            if (args == null || args.length != limit)
                throw new IllegalArgumentFormatException(formatMessage);
        } catch (IllegalArgumentFormatException e) {
            System.exit(1);
        }
    }

    private static String getFormatMessage(int limit) {
        StringBuilder sb = new StringBuilder("Enter arguments in format: ");
        switch(limit) {
            case 2:
                sb.append("[port][buffer size]");
                break;
            case 3:
                sb.append("[ip][port][buffer size]");
                break;
            case 4:
                sb.append("[ip][port][buffer size][transfer rate]");
                break;
        }
        return sb.toString();
    }

    /*
    * Parse string from command line to integer
    * */
    protected static int parseArgumentToInteger(String arg) {
        int argument = -1;
        try {
            argument = Integer.parseInt(arg);
        } catch(NumberFormatException e){
            System.err.println("Format of the arguments is not given correctly.");
            System.exit(1);
        }
        return argument;
    }

    protected abstract void start();
    protected abstract void connect();
    protected abstract void disconnect();

}
