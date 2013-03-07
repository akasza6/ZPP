/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zpp;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Klasa wyświetla okienko przeznaczone do wpisania
 * danych potrzbnych do połączenia z serwerem
 * @author akasza
 */
public class Gui {

    private JFrame ramka;
    private JTextField serverName, portNr;
    private JButton buton;
    private JPanel gora, dol;
    private static InetAddress server;
    private static int port;
    private boolean test;

    /**
     * konstruktor inicalizujący elementy gui
     */
    public Gui() {
        this.buton = new JButton("Połącz");
        serverName = new JTextField(10);
        portNr = new JTextField(4);
    }

    /**
     * metoda wyświetla okno logowania na wybrany serwer
     */
    void initGUI() {
        ramka = new JFrame("Zdalna przeglądarka plików");
        ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // rozmieszczenie pol server + numer portu
        gora = new JPanel();
        gora.add(new JLabel(" Podaj adres servera:"), BorderLayout.LINE_START);
        serverName.setText("localhost");
        serverName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                akcjaPolacz(e);
            }
        });
        gora.add(serverName, BorderLayout.LINE_END);
        dol = new JPanel();
        dol.add(new JLabel(" Podaj numer portu:"), BorderLayout.LINE_START);
        portNr.setText("8080");
        portNr.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                akcjaPolacz(e);
            }
        });
        dol.add(portNr, BorderLayout.LINE_END);
        ramka.add(gora, BorderLayout.PAGE_START);
        ramka.add(dol, BorderLayout.CENTER);
        ramka.add(buton, BorderLayout.PAGE_END);

        //akcja po kliknięciu przycisku połącz
        buton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                akcjaPolacz(e);
            }
        });

        ramka.setResizable(false);
        ramka.pack();
        ramka.setVisible(true);
    }

    /*
     * metoda wykonuje akcję po kliknięciu na przycisk połącz lub
     * enter w polu nazwa serwera lub numer portu
     * sprawdzenie poprawności wpisanej nazwy w polu tekstowym 
     * oraz numeru portu, jeśli w trakcie terstu nie znajdzie błąd przechodzi 
     * do metody iGuiOdServ
     */
    public void akcjaPolacz(ActionEvent e) {
        test = true;
        if (serverName.getText().equals("") || portNr.getText().equals("")) {
            JOptionPane.showMessageDialog(ramka,
                    "Żadne pole nie może pozostać puste",
                    "Uzupełnij pola", JOptionPane.ERROR_MESSAGE);
            test = false;
        } else {
            try {
                server = InetAddress.getByName(serverName.getText());
            } catch (UnknownHostException ex) {
                JOptionPane.showMessageDialog(ramka,
                        "Podany host nie istnieje",
                        "Błędna nazwa hosta", JOptionPane.ERROR_MESSAGE);
                test = false;
            }
            try {
                port = Integer.parseInt(portNr.getText());
                TestNrPortu spr = new TestNrPortu();
                spr.spr(port);
            } catch (NumberFormatException ex) {
                error();
                test = false;
            } catch (ZlyNrPortu ex) {
                error();
                test = false;
            }
        }
        if (test) {
            ramka.dispose();
            aGuiOdServ();
        }
    }

    /*
     * Metoda otwiera okno błędu spowodowane złym numerem portu
     */
    public void error() {
        JOptionPane.showMessageDialog(ramka,
                "Numer portu musi być liczbą z zakresu 0-65535",
                "Błędny numer portu", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * metoda inicjuje okienko z plikami na serwerze
     */
    public void aGuiOdServ() {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    new GuiOdServ().setVisible(true);
                } catch (UnknownHostException ex) {
                    System.err.println("Błąd hosta: "+ex.getStackTrace().toString());
                } catch (IOException ex) {
                    System.err.println("Błąd wejścia wyjścia: "+ex.getStackTrace().toString());
                }
            }
        });
    }

    /*
     * Metoda pobiera i zwraca pole portu
     */
    public static int getPort() {
        return port;
    }

    /*
     * Metoda pobiera i zwraca nazwę lub adres hosta
     */
    public static InetAddress getServer() {
        return server;
    }
}
