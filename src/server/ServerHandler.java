/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author akasza
 */
public class ServerHandler implements Runnable {

    DataInputStream is;
    DataOutputStream os;
    DataInputStream splik;
    Socket s;
    String katalog;
    String[] dane;
    int dl;

    /**
     * Przypisuje aktualny katalog jako domyślny.
     * @param s przekazuje parametry potrzebne do utworzenia ganiazda
     */
    public ServerHandler(Socket s) {
        this.s = s;
        this.katalog = ".";
    }

    /**
     * Metoda tworzy strumienie i przy ich pomocy wysyła nazwy plików 
     * znajdujące się w katalogu określonym polem 'katalog' do klienta.
     * Następnie metoda oczekuje na odpowiedź klienta.
     */
    public void run() {
        try {
            os = new DataOutputStream(s.getOutputStream());
            is = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            System.err.println("Błąd we/wy\n"+ex.getSuppressed());
        }

        while (true) {
            try {
                dl = nazwy().length;
                os.writeInt(dl);
                dane = nazwy();
                for (int i = 0; i < dl; i++) {
                    os.writeUTF(dane[i]);
                }

                String tmp = null;
                try {
                    tmp = is.readUTF();
                } catch (EOFException e) {
                }

                //dla katalogu
                if (tmp.endsWith("/") || tmp.endsWith(".")) {
                    this.zmienKatalog(tmp);
                }//dla pliku
                else {
                    this.wyslijPlik(katalog + File.separator + tmp);
                }
                tmp = "";

            } catch (SocketException ex) {
                //ex.printStackTrace();
                break;
            } catch (NullPointerException ex) {
                //ex.printStackTrace();
                break;
            } catch (IOException ex) {
                System.err.println("Błąd we/wy\n"+ex.getSuppressed());
            }
        }
        try {
            System.out.println("Client zakończył sesję");
            is.close();
            os.close();
            s.close();
        } catch (IOException ex) {
            System.err.println("Błąd we/wy\n"+ex.getSuppressed());
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

    /**
     * metoda zmienia scieżkę na wybraną przez klienta
     * @param tmp katalog do którego będziemy przychechodzić
     */
    public void zmienKatalog(String tmp) {
        if (tmp.endsWith("/")) {
            if (tmp.equals("/")) {
                katalog += tmp.substring(0, tmp.length() - 1);
            } else {
                katalog += File.separator + tmp.substring(0, tmp.length() - 1);
            }

        } else if (tmp.endsWith(".") && katalog.lastIndexOf(File.separator)
                != katalog.indexOf(File.separator)) {
            katalog = katalog.substring(0, katalog.lastIndexOf(File.separator));
        } else {
            katalog = katalog.substring(0, katalog.indexOf(File.separator) + 1);
        }
        System.out.println(katalog);
    }

    /**
     * metoda wysyła wybrany plik do klienta
     * @param name - nazwa pliku do wysłania
     */
    public void wyslijPlik(String name) {
        try {
            // sendfile
            File myFile = new File(name);
            if (myFile.exists() && myFile.isFile()) {
                FileInputStream fis = new FileInputStream(myFile);
                os.flush();
                os.writeLong(myFile.length());
                byte[] mybytearray = new byte[(int) myFile.length()];
                fis.read(mybytearray, 0, mybytearray.length);
                splik = new DataInputStream(fis);
                splik.read(mybytearray, 0, mybytearray.length);
                os = new DataOutputStream(s.getOutputStream());
                System.out.println("wysyłanie pliku "+myFile.getName()+" do klienta");
                os.write(mybytearray, 0, mybytearray.length);
                System.out.println("wysłano "+myFile.getName());

                os.flush();

                splik.close();
                fis.close();

            }
        } catch (IOException ex) {
            System.err.println("Błąd we/wy:\n"+ex.getSuppressed());
        }
    }
}
