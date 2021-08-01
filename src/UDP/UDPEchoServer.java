package UDP;
import abs.NetworkingLayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/*
  UDPEchoServer.java
  A simple echo server with no error handling
*/

public class UDPEchoServer extends NetworkingLayer {

	private DatagramSocket socket;

	protected UDPEchoServer(int localPort, int bufferSize) {
		super(localPort, bufferSize);
	}

	public static void main(String[] args)  {
		scanArgumentsFormat(args, 2);
		UDPEchoServer server = new UDPEchoServer(parseArgumentToInteger(args[0]), parseArgumentToInteger(args[1]));
		server.connect();
		server.start();
		server.disconnect();
    }

	@Override
	protected void start() {
		while (true)
			receiveAndSend();
	}

	@Override
	protected void connect(){
		try {
			socket = new DatagramSocket(null);
			socket.bind(localAddress);
		}catch(SocketException e) {
			System.err.println("Error connecting to local address: " + e.getMessage());
			System.exit(1);
		}
	}

	@Override
	protected void disconnect() {
		try {
			socket.close();
		}catch(Exception e) {
			System.err.println("Error occurred closing the socket: " + e.getMessage());
			System.exit(1);
		}
	}

	/*
	* Receiving and echoing one packet
	* */
	private void receiveAndSend() {
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			System.err.println("Error receiving a packet: " + e.getMessage());
			System.exit(1);
		}
		DatagramPacket sendPacket =
				new DatagramPacket(receivePacket.getData(),
						receivePacket.getLength(),
						receivePacket.getAddress(),
						receivePacket.getPort());
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			System.err.println("Error sending a packet " + e.getMessage());
		}
		System.out.printf("UDP echo request from %s", receivePacket.getAddress().getHostAddress());
		System.out.printf(" using port %d\n", receivePacket.getPort());
	}
}