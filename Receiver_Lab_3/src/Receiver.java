import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
/*This class waits for packets and sends a simple ack that just consists of the original message back to the sender every time a packet is recieved.
*/

public class Receiver {
	int count;

	public Receiver() {
		count = 0;
	}
	
	public void send(Byte ack, InetAddress ip) throws IOException, InterruptedException{//Note that this just sends the message recieved as an ack
		Thread.sleep(50);
		DatagramSocket senderSocket = new DatagramSocket(9878);//create socket for sending
		byte[] sendData = new byte[1024];	
		sendData[0] = ack;	//allocate message space
		if (count == 0 || count == 1){
			sendData[0]= 1;
		}else if(count ==2){
			sendData[0] =0;
		}
		count++;
									
		DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, ip, 9879);//create packet to send to sender port 9879
		senderSocket.send(sendPkt);														//send packet
		senderSocket.close();															//close port
		
		
	}

	public void receive() throws IOException, InterruptedException {
		DatagramSocket receiverSocket = new DatagramSocket(9876);//open socket for listening on port 9876
		byte[] rcvData = new byte[1024];						//create byte array to accept incoming message
		while (true) {											//loop to keep listening
			DatagramPacket rcvPkt = new DatagramPacket(rcvData, rcvData.length);//initialize a packet 
			receiverSocket.receive(rcvPkt);						//set rcvPkt to the next packet waiting on the socket
			InetAddress IPAddress = rcvPkt.getAddress();		//get IPAdress of sender
			int port = rcvPkt.getPort();						//get port of sender
						//print message
			System.out.println(rcvData[0]);
			if (rcvData[0]!=2){
				send(rcvData[0], IPAddress);
			}
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Receiver rec = new Receiver();//create instance of receiver class
		rec.receive();//call receive message
	}

}
