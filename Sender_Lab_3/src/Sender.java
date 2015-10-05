import java.io.*;
import java.net.*;
import java.util.Scanner;

/*This class sends one packet and then listens for an ack respose. If a response is gathered the class will print ACK + message
*/
public class Sender {

	int maxSeq;
	int[] window;
	int[] messageList;
	DatagramSocket senderSocket;
	DatagramSocket receiverSocket;

	public Sender(int windowI, int seq, int[] dropPkt) throws UnknownHostException, IOException {
		window = new int[windowI];
		maxSeq = seq;
		senderSocket = new DatagramSocket(9877);
		receiverSocket = new DatagramSocket(9879);

		messageList = new int[10];
		for (int i = 0; i < 10; i++) {
			messageList[i] = i;
		}

		for (int i = 0; i < 4; i++) {
			window[i] = messageList[i];
			
			senderSocket.send(createPacket(window[i]));
			System.out.println("Sent packet:" +window[i]%maxSeq);
		}
		receive();

	}

	public DatagramPacket createPacket(int seqNum) throws UnknownHostException, UnsupportedEncodingException {
		byte[] sendData = new byte[1024];
		InetAddress IPAddress = InetAddress.getByName("localhost");
		sendData[0] = (byte)seqNum;
		DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		return sendPkt;

	}

	public void receive() throws IOException {
		byte[] rcvData = new byte[1024];
		while (true) {
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
			receiverSocket.receive(rcvPkt);
			int a =(int)rcvData[0];
			
			System.out.println("Recieved: "+a);
			
			if(!adjustWindow(a)){
				senderSocket.close();
				receiverSocket.close();
				break;
			}

		}
	}

	public boolean adjustWindow(int seqNum) throws UnknownHostException, IOException {
		if (seqNum == (window[0] % maxSeq)) {// if sequence number first
			
			boolean check = true;
			while (check) {
				window[0] = window[1];
				window[1] = window[2];
				window[2] = window[3];
				window[3] = window[2] + 1;
				if (window[3] < messageList.length){
					send(window[3]%maxSeq);
					System.out.println("Sent packet: "+ window[3]%maxSeq);
				}else if(window[0]>=messageList.length){
					return false;
				}
				
				
				
				if (window[0] > 0) {
					check = false;
				}
			}
			
		} else {
			for (int i = 1; i < window.length; i++) {
				if (seqNum == window[i] % maxSeq) {
					window[i] = -1;
				}
			}
		}
		return true;
	}

	public void testReceive() throws IOException {
		// create listener socket
		byte[] rcvData = new byte[1024]; // create byte array to put messag in

		while (true) {
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);// create
																				// a
																				// packet
																				// for
																				// recieving
																				// message
			receiverSocket.receive(rcvPkt); // stalls here until a packet is
											// received, sets packet to
											// rcvpacket
			String message = new String(rcvPkt.getData()); // set String message
															// to the rcvPkt
															// converted to a
															// string
			InetAddress IPAddress = rcvPkt.getAddress(); // Get reciever IP(Not
															// used)
			int port = rcvPkt.getPort(); // get reciever port(Not Used)
			System.out.println("Ack =" + message); // print out ack command that
													// was recieved
		}
	}

	public void testSend() throws IOException {

		// create socket for sending
		InetAddress IPAddress = InetAddress.getByName("localhost"); // get local
																	// ip
																	// address
		byte[] sendData = new byte[1024]; // allocate message space
		String message = new String("Success!"); // create string message
		sendData = message.getBytes("UTF-8"); // convert string message into byte array
		DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);// create
																								// sender
																								// socket
																								// with
																								// port
																								// 9876
		senderSocket.send(sendPkt); // send packet

		testReceive(); // start linstening for acks

	}

	public void send(int seqNum) throws IOException {
		
		senderSocket.send(createPacket(seqNum));

	}

	public static void main(String[] args) throws IOException {

		/*
		 * Scanner reader = new Scanner(System.in); System.out.println(
		 * "Enter the window's size on the sender:"); int tWin =
		 * reader.nextInt(); System.out.println(
		 * "Enter the maximum sequence number on the sender:"); int tSeq =
		 * reader.nextInt(); System.out.println(
		 * "Select the packet(s) that will be dropped:");
		 * System.out.println(tWin); System.out.println(tSeq);
		 */
		int[] test = { 1, 2, 3 };

		Sender sender = new Sender(4, 9, test);// create new Sender(Inputs
												// unimportant right now)
		//sender.testSend(); // calls send to make and send packet

	}

}
