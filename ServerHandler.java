/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zpp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author akasza
 */
public class ServerHandler implements Runnable {

    BufferedReader strWe;
    PrintStream strWy;
    Socket s;
    String katalog;
    String[] dane;
    int dl;

    public ServerHandler(Socket s) {
        try {
            this.s = s;
            this.katalog = ".";
            strWe = new BufferedReader(new InputStreamReader(s.getInputStream()));
            strWy = new PrintStream(s.getOutputStream(), true);


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                dl = nazwy().length;
                strWy.println(dl);
                dane = nazwy();
                for (int i = 0; i < dl; i++) {
                    strWy.println(dane[i]);
                }

                String tmp = strWe.readLine();
                //jakos obsluzyc odlaczenie klienta

                //dla katalogu
                if (tmp.endsWith("/") || tmp.endsWith(".")) {
                    this.zmienKatalog(tmp);
                }//dla pliku
                else {
                    this.wyslijPlik(katalog+File.separator+tmp);
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
                break;
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Listuje pliki w podanym katalogu
     * @return lista plikow w danym katalogu
     */
    public File[] elements() {
        File plik = new File(katalog);
        katalog = plik.getAbsolutePath();
        if (katalog.endsWith(".")) {
            katalog = katalog.substring(0, katalog.length() - 1);
        }
        return plik.listFiles();
    }

    /**
     * zamienia liste fypu file na listę String
     * @return lista String reprezentujaca zawartosc katalogu
     */
    public String[] nazwy() {
        File[] t = elements();
        int tmp = t.length + 1;
        String[] wynik = new String[tmp];
        wynik[0] = "..";
        for (int i = 1; i < tmp; i++) {
            wynik[i] = t[i - 1].getName();
            if (t[i - 1].isDirectory()) {
                wynik[i] += "/";
            }
        }
        return wynik;
    }

    public void zmienKatalog(String tmp) {
        if (tmp.endsWith("/")) {
            katalog += File.separator + tmp.substring(0, tmp.length() - 1);

        } else if (tmp.endsWith(".") && katalog.lastIndexOf(File.separator)
                != katalog.indexOf(File.separator)) {
            katalog = katalog.substring(0, katalog.lastIndexOf(File.separator));
        } else {
            katalog = katalog.substring(0, katalog.indexOf(File.separator) + 1);
        }
        System.out.println(katalog);
    }

    public void wyslijPlik(String name) {
        try {

            // sendfile
            File myFile = new File(name);
            System.out.print(name);
            FileInputStream fis = new FileInputStream(myFile);
            int tmp,i=0,j=0;
            byte []mybytearray=new byte[1024];
            while((tmp=fis.read())!=-1){
                mybytearray[i]=(byte)tmp;
                i++;
            }
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            strWy.write(i);
            OutputStream os = s.getOutputStream();
            System.out.println("Sending...");
            while(j<i){
                os.write(mybytearray[j]);
                j++;
            }
            os.flush();
            //sock.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void zamknijGniazdo() throws IOException {
        s.close();
        strWe.close();
        strWy.close();
    }
}
