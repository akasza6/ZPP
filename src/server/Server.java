/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author akasza
 */
public class Server {
    public static void main(String[] args) {
        try{
                InetAddress adres=InetAddress.getByName("localhost");
                System.out.println("adres serwera to: "+adres.getHostAddress());
		ServerSocket ss=new ServerSocket(8080);
		while(true){
                        System.out.println("server oczekuje na połączenie");
			Socket s=ss.accept();
			ServerHandler ch=new ServerHandler(s);
                        Thread watek=new Thread(ch);

			watek.start();

		}
	  }catch(IOException e){
		e.printStackTrace();
	  }
    }
}
