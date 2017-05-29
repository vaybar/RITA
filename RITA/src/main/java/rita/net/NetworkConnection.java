package rita.net;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import rita.battle.ZIPRobotProccessor;

/**
 * Esta clase administra las conexiones entre los equipos que pertenecen a un
 * grupo de competencia. Primero, via Multicast se informa la IP y el grupo
 * seleccionado (porque el Multicast funciona con UDP).
 * 
 * PC1 ------------10.3.8.95, 1 --------------> IP Multicast G1
 * 
 * Por cada IP recibida, PC1 envia el robot via Sockets (puerto bien definido)
 * si está listo para competir
 * 
 * 
 * 
 * */
public final class NetworkConnection {

	public static String[] grupos = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

	/*
	 * private static MulticastSocket s; private static InetAddress group;
	 * private final static String IP = "224.0.8.1"; private final static int
	 * PORT = 6789;
	 */

	// private static boolean listening = true;

	private static JChannel channel;

	private static String selectedGroup;

	public static void start() throws Exception {
		/* Implementacion con JGroups */
		channel = new JChannel();
		/* if (selectedGroup!=null){ */
		channel.setDiscardOwnMessages(true);
		// channel.connect(selectedGroup);

		channel.setReceiver(new ReceiverAdapter() {
			
			
			
			
			public void receive(Message msg) {

				System.out.println("received message " + msg);
				ZIPRobotProccessor.proccess(msg.getBuffer());
			}

			public void viewAccepted(View view) {
				System.out.println("received view " + view);
			}
		});
		/* } else throw new Excpetion */

		/* ******* */
	}

	/*
	 * public static void start(String selectedGroup) throws Exception {
	 * System.out.println("Grupo ELEGIDO:" + selectedGroup);
	 * NetworkConnection.selectedGroup=selectedGroup; NetworkConnection.start();
	 * }
	 */
	public static void setMulticastGroup(String selectedGroup) {
		if (channel.isConnected())
			channel.disconnect();
		NetworkConnection.selectedGroup = selectedGroup;
		try {
			channel.connect(selectedGroup);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void disconnectAndClose() {
		selectedGroup=null;
		channel.disconnect();
		//channel.close();
	}

	public static void sendRobot(String zipFilename) throws Exception {
		File f = new File(zipFilename);
		Message msg;

		Path path = Paths.get(f.getPath());
		try {
			byte[] bytes = Files.readAllBytes(path);
			msg = new Message(null, null, bytes);
			channel.send(msg);
			System.out.println("Enviado..." + bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * public static void setMulticastConnection() {
	 * 
	 * try { group = InetAddress.getByName(IP); s = new MulticastSocket(PORT);
	 * // s.setLoopbackMode(false); s.joinGroup(group); // s.joinGroup(new
	 * InetSocketAddress(group, PORT), //
	 * NetworkConnection.getCurrentEnvironmentNetworkIp());
	 * 
	 * // System.out.println("LOOPBACK MODE " + s.getLoopbackMode()); //
	 * s.setTimeToLive(1); final byte[] dataReceived = new byte[1024];
	 * 
	 * // los datos a recibirse deberían ser: // nombre del robot // contenido
	 * del class file new Thread(new Runnable() {
	 * 
	 * @Override public void run() { while (listening) { // Se recibirá un ZIP
	 * conteniendo: // un TXT con el nombre y la posicion del robot // el
	 * archivo .class conteniendo el código del robot DatagramPacket rsp = new
	 * DatagramPacket(dataReceived, 0, dataReceived.length); try {
	 * s.receive(rsp); } catch (Throwable t) { t.printStackTrace();
	 * System.out.println("\n"); break; } byte[] data = rsp.getData();
	 * System.out.println("Recibido..." + data);
	 * ZIPRobotProccessor.proccess(data);
	 * 
	 * } } });
	 * 
	 * } catch (UnknownHostException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); // TODO Mensaje host desconocido
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); // TODO Mensaje problema para conectarse en red }
	 * 
	 * }
	 */

	/*
	 * for (File f : files) { Path path = Paths.get(f.getPath()); DatagramPacket
	 * hi = null; try { byte[] bytes = Files.readAllBytes(path); hi = new
	 * DatagramPacket(bytes, bytes.length, group, PORT); s.send(hi);
	 * System.out.println("Enviado..." + bytes); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * }
	 */

	// }

	/*
	 * public void receive(Message msg) {
	 * 
	 * String line = msg.getSrc() + ": " + msg.getObject();
	 * 
	 * // System.out.println(line); // // synchronized(state) { // //
	 * state.add(line); // // }
	 * 
	 * }
	 */

	/*
	 * public static NetworkInterface getCurrentEnvironmentNetworkIp() {
	 * 
	 * Enumeration<NetworkInterface> netInterfaces = null; try { netInterfaces =
	 * NetworkInterface.getNetworkInterfaces();
	 * 
	 * while (netInterfaces.hasMoreElements()) { NetworkInterface ni =
	 * netInterfaces.nextElement(); Enumeration<InetAddress> address =
	 * ni.getInetAddresses(); while (address.hasMoreElements()) { InetAddress
	 * addr = address.nextElement(); // log.debug("Inetaddress:" +
	 * addr.getHostAddress() + // " loop? " + addr.isLoopbackAddress() +
	 * " local? " // + addr.isSiteLocalAddress()); if (!addr.isLoopbackAddress()
	 * && addr.isSiteLocalAddress() && !(addr.getHostAddress().indexOf(":") >
	 * -1)) { // currentHostIpAddress = addr.getHostAddress();
	 * System.out.println(ni); return ni;
	 * 
	 * } } }
	 * 
	 * } catch (SocketException e) { // log.error(
	 * "Somehow we have a socket error acquiring the host IP... Using loopback instead..."
	 * ); e.printStackTrace(); }
	 * 
	 * return null; }
	 */

	/*
	 * public static void setMulticastGroup(String selectedValue) {
	 * selectedGroup = selectedValue;
	 * 
	 * }
	 */

	public static boolean isSelectedGroup() {
		return selectedGroup != null;
	}

	public static String getSelectedGroup() {
		return selectedGroup;
	}
	
	public static void sendMessage(String texto) throws Exception {
		Message msg = new Message();
		msg.setBuffer(texto.getBytes());
		channel.send(msg);
	}

	/*
	 * public static void main(String[] args){
	 * 
	 * try { NetworkConnection.setMulticastGroup("1");
	 * NetworkConnection.start(); NetworkConnection.sendMessage("Holas!!!!!");
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

}
