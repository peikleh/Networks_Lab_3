import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
/*This class waits for packets and sends a simple ack that just consists of the original message back to the sender every time a packet is recieved.
*/

public class Receiver {
	DatagramSocket rSocket;
	byte[][] window;
	byte maxSeq;
	
	
	
	public Receiver() throws SocketException {
		rSocket = new DatagramSocket(9876);
	}
	
	public void send(Byte ack, InetAddress ip, int port) throws IOException, InterruptedException{
		
		byte[] sendData = new byte[1024];
		
		sendData[0] = ack;								
		DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, ip, port);
		rSocket.send(sendPkt);
		System.out.print("Packet" + ack + "was received, Ack " + ack + "was sent   ");
		PrintWindow();
		receive();
																	
		
		
	}
	
	public void PrintWindow(){
		System.out.print("[");
		for(int i =0; i<window.length; i++){
			
			if(window[i][1] == 0){
				System.out.print(window[i][0]+", ");
			}else{
				System.out.print(window[i][0]+"#, ");
			}
			
		}
		System.out.print("]");
		System.out.println();
		
	}
	
	public void receiveFirst() throws IOException, InterruptedException{
		byte[] rcvData = new byte[1024];
		DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
		rSocket.receive(rcvPkt);	
		window = new byte[rcvData[2]][2];
		maxSeq = rcvData[1];
		byte seq = rcvData[0];
		InetAddress IPAddress = rcvPkt.getAddress();
		int port = rcvPkt.getPort();
		
		for(int i = 0; i<window.length; i++){//fill window
			window[i][0] = (byte) i;
		}
		
		shiftWindow(seq);
		
		
		
		
		send(seq, IPAddress, port);
		
		
		
	}
	
	public void shiftWindow(byte seqNum){
		int checkAck = 1;
		if (seqNum == window[0][0]){
			while(window[checkAck][1] > 0 && checkAck < window.length){
				checkAck += 1;
			}
			for(int i =0; i<window.length; i++){
				window[i][0]=(byte) ((window[i][0]+((byte)checkAck))%maxSeq);
			}
		}else{
			for (int i = 1; i<window.length; i++){
				if (window[i][0] == seqNum){
					window[i][1] = 1;
				}
			}
		}
		
		
	}
	
	
	

	public void receive() throws IOException, InterruptedException {
		
		byte[] rcvData = new byte[1024];					
		while (true) {											
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);
			rSocket.receive(rcvPkt);					
			InetAddress IPAddress = rcvPkt.getAddress();
			int port = rcvPkt.getPort();			
			shiftWindow(rcvData[0]);
			send(rcvData[0], IPAddress, port);
			
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Receiver rec = new Receiver();
		rec.receiveFirst();
	}

}
