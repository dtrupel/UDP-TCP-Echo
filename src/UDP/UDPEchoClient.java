package UDP;

import abs.ClientNetworkingLayer;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPEchoClient extends ClientNetworkingLayer {

    private static final int MY_PORT = 0;

    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;

	protected UDPEchoClient(String remoteIp, int remotePort, int bufferSize, int transferRate) {
		super(MY_PORT, remoteIp, remotePort, bufferSize, transferRate);
	}

	public static void main(String[] args) {
		scanArgumentsFormat(args, 4);
		UDPEchoClient client = new UDPEchoClient(args[0], parseArgumentToInteger(args[1]),
				parseArgumentToInteger(args[2]), parseArgumentToInteger(args[3]));
		client.connect();
		client.start();
		client.disconnect();
	}

	@Override
	protected void start() {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a message to echo: ");
		String message = input.nextLine();
		sendPacket = new DatagramPacket(message.getBytes(), message.length(), remoteAddress);
		receivePacket = new DatagramPacket(buffer, buffer.length);
		if(transferRate == 0)
			sendAndReceive();
		else {
			runTimeLimited(60_000);
		}
	}

	@Override
	protected void connect() {
		try {
			socket = new DatagramSocket(null);
			socket.bind(localAddress);
		}catch(SocketException e) {
			System.err.println("Error establishing connection: " + e.getMessage());
			System.exit(1);
		}
	}

	@Override
	protected void disconnect() {
		try {
			socket.close();
		}catch(Exception e) {
			System.err.println("Error occurred with closing the socket: " + e.getMessage());
			System.exit(1);
		}
	}

	/*
	* Run until manually interrupted
	* */
	private void runUntilInterrupted() {
		while (!Thread.interrupted()) {
			runSecond();
		}
	}

	/*
	* Run for number of milliseconds
	* */
	private void runTimeLimited(int milliseconds) {
		System.out.println("Running for " + milliseconds/1000 + " seconds...");
		long startTime = System.currentTimeMillis() + milliseconds;
		while (System.currentTimeMillis() < startTime) {
			runSecond();
		}
	}

	/*
	* Loop sending message for 1 second
	* Prints log after 1 second
	* */
	private void runSecond() {
		long endTime = System.currentTimeMillis() + 1000;
		int storeCount = 0;
		while(System.currentTimeMillis() < endTime) {
			int count = 0;
			do {
				if(System.currentTimeMillis() >= endTime)
					break;
				sendAndReceive();
				count++;
				delay();
			}while(count < transferRate);
			storeCount = count;
		}
		logBySecond(storeCount, transferRate);
	}

	/*
	* Logging information
	* */
	private void logBySecond(int count, int transferRate) {
		if(transferRate > count) {
			System.out.println("Packets sent previous second: " + count + "\n" +
					"Packets left unsent: " + (transferRate-count));
		}else {
			System.out.println("Packets sent previous second: " + count);
		}
	}

	/*
	* Sending and receiving one packet
	* */
	private void sendAndReceive() {
		try {
			socket.send(sendPacket);
			socket.receive(receivePacket);

			if(receivePacket.getLength() == sendPacket.getLength())
				System.out.println(receivePacket.getLength() + " bytes sent and received.");
			else
				System.out.println("Packets are not of the same length!");

		}catch(IOException e) {
			System.out.println("There was a problem with packet distribution: " + e.getMessage());
			disconnect();
			System.exit(1);
		}
	}
}