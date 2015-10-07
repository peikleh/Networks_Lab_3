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
	int[][] window;
	int[] messageList;
	DatagramSocket sSocket;


	public Sender(int windowI, int seq, int[] dropPkt) throws UnknownHostException, IOException {
		// time = new Timer[seq];
		window = new int[windowI][2];
		maxSeq = seq;
		sSocket = new DatagramSocket(9877);
		
		sSocket = sSocket;
		messageList = new int[10];

		for (int i = 0; i < 10; i++) {
			messageList[i] = i;
		}
		for(int i = 0; i <window.length; i++){
			window[i][0] = messageList[i];
		}

		Send();
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
			sSocket.receive(rcvPkt);
			int a = (int) rcvData[0];
			System.out.print("Acked:" + a);
			AdjustWindow(a);
			Send();
			
		}
	}

	public void AdjustWindow(int seqNum) throws UnknownHostException, IOException {
		int checkAck=1;
		if (seqNum == (window[0][0] % maxSeq)) { //shift to next unacked packet
			while (window[checkAck][1] == -1 && checkAck < window.length){
				checkAck += 1;
			}
			for (int i = 0; i<window.length; i++){
				if(window[i][0]+checkAck < messageList.length){
					window[i][0] += checkAck;
					if (i+checkAck<window.length){
						window[i][1] = window[i+checkAck][1];
					}else{
						window[i][1] = 0;
					}
					
					
				}else{
					window[i][0] = window[i][0]+checkAck;
					window[i][1] = 1;
				}
			}
			
		} else { //mark as acked;
			for (int i = 1; i < window.length; i++) {
				if (seqNum == window[i][0] % maxSeq) {
					window[i][1] = -1;
				}
			}
		}
		printWindow();
	}

	public void Send() throws IOException {
		
		for (int i = 0; i < window.length; i++){
			
			if (window[i][1] ==0){
				if(window[i][0]!=2){
					sSocket.send(createPacket(window[i][0]%maxSeq));
				}
				
				window[i][1]=1;
				System.out.print("Sent:" + window[i][0]%maxSeq);
				printWindow();
			}
		}
		
	}
	
	public void printWindow(){
		System.out.print("[");
		for (int i = 0; i < window.length; i++){
			
			if(window[i][0]<maxSeq){
				if(window[i][1] == 0 || window[i][1] == -1){
					System.out.print( + window[i][0]%maxSeq +", " );
				}else if(window[i][1] ==1){
					System.out.print(  window[i][0]%maxSeq +"*, " );
				}
			
			}else{
				System.out.print("-, ");
			}
			
		
	}
		System.out.print("]");
		System.out.println();
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

		Sender sender = new Sender(4, 10, test);// create new Sender(Inputs
												// unimportant right now)
		// sender.testSend(); // calls send to make and send packet

	}

}
