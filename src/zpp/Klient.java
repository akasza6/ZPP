/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zpp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

/**
 *
 * @author akasza
 */
public class Klient {

    private int dl;
    private String[] dane;
    private Socket gniazdo;

    public Klient() throws IOException {
        System.out.println("klient sie polączył z " + Gui.getServer()
                + " na porcie " + Gui.getPort());
        try {
            gniazdo = new Socket(Gui.getServer(), Gui.getPort());
        } catch (ConnectException ex) {
        }
    }

    void listujKatalog() throws IOException {

        DataInputStream in = new DataInputStream(gniazdo.getInputStream());
        dl = in.readInt();
        dane = new String[dl];
        int i = 0;
        String tmp;
        while (i < dl) {
            tmp = in.readUTF();
            dane[i] = tmp;
            i++;
        }
    }

    public String[] getDane() {
        return dane;
    }

    /**
     * metoda wysyla do serwera zapytanie o wyslanie danego pliku
     * @param nazwaPliku
     * @throws IOException
     */
    public void zapytajOPlik(String nazwaPliku) throws IOException {

        DataOutputStream out = new DataOutputStream(gniazdo.getOutputStream());
        out.writeUTF(nazwaPliku);
        out.flush();
    }

    public void odbierz(String nazwa) throws IOException {

        DataInputStream is = new DataInputStream(gniazdo.getInputStream());
        long filesize = is.readLong(); 
        long rozm=filesize;
        System.out.println(filesize);
        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;

        // receive file
        byte[] mybytearray = new byte[(int)filesize*2];
        FileOutputStream fos = new FileOutputStream(nazwa);
        DataOutputStream bos = new DataOutputStream(fos);
        int tmp=is.available();
        int ileBitow=(tmp>filesize)?tmp:65536;
        bytesRead = is.read(mybytearray, 0, ileBitow);
        current = bytesRead;
        rozm-=bytesRead;

        while (rozm>=0){
            tmp=is.available();
            ileBitow=(int) ((rozm < 65536) ? rozm : 65536);
            bytesRead = is.read(mybytearray, current, (current+ileBitow));
            if (bytesRead >= 0) {
                current += bytesRead;
                rozm-=bytesRead;
            }
        }

        bos.write(mybytearray, 0, (int)filesize);
        bos.flush();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
  
        
        bos.close();
        fos.close();
        this.wyczyscStrumien();
    }

    public void wyczyscStrumien() {
        try {
            InputStream is = gniazdo.getInputStream();
            int i;
            while ((i = is.available()) != 0) {
                System.out.println(i + "!");
                is.skip(i);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void zamknijGniazdo() throws IOException {
        gniazdo.close();
    }
}
