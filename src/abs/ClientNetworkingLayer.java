package abs;

import abs.NetworkingLayer;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class ClientNetworkingLayer extends NetworkingLayer {

    private static final int MIN_TRANSFER_RATE_VALUE = 0;
    private static final int MIN_IP_ADDRESS_TOKEN_VALUE = 0;
    private static final int MAX_IP_ADDRESS_TOKEN_VALUE = 255;

    protected final String remoteIp;
    protected final int remotePort;
    protected final int transferRate;

    protected SocketAddress remoteAddress;

    protected ClientNetworkingLayer(int localPort, String remoteIp, int remotePort, int bufferSize, int transferRate) {
        super(localPort, bufferSize);

        try {
            //Throw exceptions for invalid arguments
            if (!isValidIpAddress(remoteIp))
                throw new IllegalArgumentException("Invalid ip address.");
            if (!isValidPort(remotePort))
                throw new IllegalArgumentException("Invalid port.");
            if (!isValidTransferRate(transferRate))
                throw new IllegalArgumentException("Invalid transfer rate.");
        }catch(IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.transferRate = transferRate;

        //Setup remote address after init. of fields
        try{
            remoteAddress = new InetSocketAddress(this.remoteIp, this.remotePort);
        }catch(Exception e) {
            System.err.println("Address could not be resolved. " + e.getMessage());
            System.exit(1);
        }
    }

    /*
    * Perform delay to space out messages according to transfer rate
    * (left here as TCP may also use it in the future)
    * */
    protected void delay() {
        try {
            Thread.sleep(1000/transferRate);
        }catch(ArithmeticException e) {
            System.err.println("Error occurred - cannot divide by 0.");
            System.exit(1);
        }catch(InterruptedException e) {
            System.err.println("Error occurred - program interrupted: " + e.getMessage());
            System.exit(1);
        }
    }

    private boolean isValidTransferRate(int transferRate) {
        return transferRate >= MIN_TRANSFER_RATE_VALUE && transferRate < Integer.MAX_VALUE;
    }

    /*
     * Get separate network/host ids and check for validity (0-255)
     * */
    private boolean isValidIpAddress(String ipAddress) {
        if(ipAddress == null)
            return false;
        String [] ids = ipAddress.split("\\.");
        if(ids.length != 4)
            return false;
        for(String s : ids) {
            int currentId = parseArgumentToInteger(s);
            if(currentId < MIN_IP_ADDRESS_TOKEN_VALUE || currentId > MAX_IP_ADDRESS_TOKEN_VALUE)
                return false;
        }
        return true;
    }


}
