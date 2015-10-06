import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Sender {
	// LinkedList<Object> timerQueue;
	// Timer[] time;
	int maxSeq;
	int[] window;
	int[] messageList;
	DatagramSocket senderSocket;
	DatagramSocket receiverSocket;

	public Sender(int windowI, int seq, int[] dropPkt) throws UnknownHostException, IOException {
		// time = new Timer[seq];
		window = new int[windowI];
		maxSeq = seq;
		senderSocket = new DatagramSocket(9877);
		// receiverSocket = new DatagramSocket(9879);
		receiverSocket = senderSocket;
		messageList = new int[10];

		for (int i = 0; i < 10; i++) {
			messageList[i] = i;
		}

		for (int i = 0; i < windowI; i++) {
			window[i] = messageList[i];

			Send(window[i] % maxSeq);
			for (int j = 0; j<window.length; j++){
				System.out.print(window[j]%maxSeq + ", ");
			}
			System.out.println();

		}

		Receive();
	}

	// class timeOut extends TimerTask {
	// public void run() {
	// Object holdTime = timerQueue.removeFirst();
	// for(int i =0; i<maxSeq; i++){
	// if(holdTime==time[i]){
	// try {
	// Send(i);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }

	public DatagramPacket createPacket(int seqNum) throws UnknownHostException, UnsupportedEncodingException {
		byte[] sendData = new byte[1024];
		InetAddress IPAddress = InetAddress.getByName("localhost");
		sendData[0] = (byte) seqNum;
		sendData[1] = (byte) maxSeq;
		sendData[2] = (byte) window.length;
		DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		return sendPkt;

	}

	/*
	 * public void stopTimer(int seqNum){ time[seqNum].cancel();
	 * timerQueue.remove(time[seqNum]); }
	 */

	public void Receive() throws IOException {
		byte[] rcvData = new byte[1024];
		while (true) {
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
			receiverSocket.receive(rcvPkt);
			int a = (int) rcvData[0];
			// stopTimer(a);

			System.out.println("Recieved: " + a);

			if (!AdjustWindow(a)) {
				senderSocket.close();
				receiverSocket.close();
				break;
			}

		}
	}

	public boolean AdjustWindow(int seqNum) throws UnknownHostException, IOException {
		if (seqNum == (window[0] % maxSeq)) {
			boolean check = true;

			while (check) {
				window[0] = window[1];// fix this. Only does window size of 4
				window[1] = window[2];
				window[2] = window[3];
				window[3] = window[2] + 1;
				if (window[3] < messageList.length) {
					for (int i = 0; i < window.length; i++) {
						System.out.print(window[i]%maxSeq + ", ");
					}
					System.out.println();

					Send(window[3] % maxSeq);
					// System.out.println("Sent packet: " + window[3] % maxSeq);

				} else if (window[0] >= messageList.length) {
					for (int i = 0; i<window.length; i++){
						System.out.print(window[i]%maxSeq + ", ");
					}
					System.out.println();
					return false;
				}

				if (window[0] >= 0) {
					for (int i = 0; i<window.length; i++){
						System.out.print(window[i]%maxSeq + ", ");
					}
					System.out.println();
					check = false;
				}

			}

		} else {
			for (int i = 1; i < window.length; i++) {
				if (seqNum == window[i] % maxSeq) {
					window[i] = -1;
				}
			}
			for (int i = 0; i < window.length; i++) {
				System.out.print(window[i] + ", ");
			}
			System.out.println();
		}
		return true;
	}

	public void Send(int seqNum) throws IOException {

		// time[seqNum] = new Timer();
		senderSocket.send(createPacket(seqNum));
		System.out.println("sent" + seqNum);
		
		// time[seqNum].schedule(new timeOut(), 1000);
		// timerQueue.add(time[seqNum]);

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

		Sender sender = new Sender(4, 8, test);// create new Sender(Inputs
												// unimportant right now)
		// sender.testSend(); // calls send to make and send packet

	}

}
