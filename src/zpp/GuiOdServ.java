/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zpp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author akasza
 */
public class GuiOdServ extends JFrame implements ListSelectionListener {

    private JScrollPane scroll;
    private JPanel panel, sesja;
    private JList list;
    private JTextArea txtarea;
    private JButton pobierz, zakoncz, zamknij, nowaSesja;
    private Klient klient;
    private ListSelectionModel lsmodel;
    private DefaultListModel listModel;
    private JFileChooser chooser;
    private static int liczbaOkien = 1;

    public GuiOdServ() throws UnknownHostException, IOException {
        klient = new Klient();
        klient.listujKatalog();
        gui();
    }

    private void gui() {
        this.setTitle("Sesja na: " + Gui.getServer() + " port: " + Gui.getPort());
        this.setSize(600, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new JPanel();
        sesja = new JPanel();

        listModel = new DefaultListModel();
        dane();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmodel = list.getSelectionModel();
        lsmodel.addListSelectionListener(this);

        txtarea = new JTextArea();
        txtarea.setEditable(false);
        txtarea.setRows(6);

        pobierz = new JButton("Pobierz");
        pobierz.setEnabled(false);
        pobierz.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    apPobierz();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        zakoncz = new JButton("Zakończ sesję");
        zakoncz.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    apZakoncz();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        nowaSesja = new JButton("Nowa sesja");
        nowaSesja.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                apNowaSesja();
            }
        });

        zamknij = new JButton("Zamknij");
        zamknij.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    apZamknij();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        sesja.add(nowaSesja, BorderLayout.LINE_START);
        sesja.add(zakoncz, BorderLayout.LINE_END);

        panel.add(pobierz, BorderLayout.LINE_START);
        panel.add(sesja, BorderLayout.CENTER);
        panel.add(zamknij, BorderLayout.LINE_END);
        scroll = new JScrollPane(list);

        this.add(panel, BorderLayout.PAGE_START);
        this.add(scroll, BorderLayout.CENTER);
        this.add(new JScrollPane(txtarea), BorderLayout.PAGE_END);
    }

    /**
     * metoda zapisuje do listy poszczególne nazwy
     * plików znajdujących się na serwerze
     */
    private void dane() {
        String[] tmp = klient.getDane();
        for (int i = 0; i < tmp.length; i++) {
            listModel.addElement(tmp[i]);
        }
    }

    /**
     * obsługa przycisku pobierz/otworz
     * jesli przycisk = pobierz zostaje pokazane okno dialogowe które umożliwia wybór lokalizjacji pliku
     * jesli przycisk = otworz do serwera wysyłana jest prośba o wylistowanie "podkatalogu"
     * @throws IOException
     */
    private void apPobierz() throws IOException {
        String tmp = list.getModel().getElementAt(list.getSelectedIndex()).toString();
        klient.zapytajOPlik(tmp);
        if (pobierz.getText().equals("Pobierz")) {
            txtarea.append("pobieranie " + tmp + "\n");
            chooser = new JFileChooser();
            chooser.setDialogTitle("Zapisz plik");
            int n = chooser.showSaveDialog(panel);
            if (n == JFileChooser.CANCEL_OPTION) {
                txtarea.append("pobieranie anulowane przez użytkownika\n");
                klient.wyczyscStrumien();
            } else if (n == JFileChooser.APPROVE_OPTION) {
                klient.odbierz(chooser.getSelectedFile().getCanonicalPath());
                txtarea.append("pobierano plik\n");

            }
        } else {
            if (!tmp.equals("..")) {
                txtarea.append("otwiranie " + tmp + "\n");
            }
            listModel.removeAllElements();
            klient.listujKatalog();
            dane();
        }

    }

    /**
     *
     * @throws IOException
     */
    private void apZakoncz() throws IOException {
        int n = JOptionPane.showConfirmDialog(rootPane,
                "Czy napewno chcesz zakończyć sesję?",
                "Zamknięcie sesji", JOptionPane.OK_CANCEL_OPTION);
        if (n == JOptionPane.OK_OPTION) {
            klient.zamknijGniazdo();
            this.setVisible(false);
            if (liczbaOkien == 1) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        Gui gui = new Gui();
                        gui.initGUI();
                    }
                });
            }
            if (liczbaOkien != 1) {
                liczbaOkien--;
            }
        }
    }

    /**
     * akcja umozliwia wlaczenie nowej sesji
     */
    private void apNowaSesja() {
        liczbaOkien++;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Gui gui = new Gui();
                gui.initGUI();
            }
        });
    }

    /**
     * akcja zamyka aplikację lub usuwa sesje w zależności od ilości sesji
     * - jesli była tylko jedna sesja w programie zamuka go
     * - jesli ilosc sesji jest >1 to zamyka aktualna sesję
     */
    private void apZamknij() throws IOException {
        int n = JOptionPane.showConfirmDialog(rootPane,
                "Czy napewno chcesz zakończyć sesję?",
                "Zamknięcie sesji", JOptionPane.OK_CANCEL_OPTION);
        if (n == JOptionPane.OK_OPTION) {
            klient.zamknijGniazdo();
            if (liczbaOkien != 1) {
                liczbaOkien--;
                this.dispose();
            } else {
                System.exit(0);
            }
        }
    }

    /**
     * metoda sprawdza jaki element listy jest zaznaczony,
     * zmienia napisa na przycisku otworz/pobierz
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                //No selection, disable button.
                pobierz.setEnabled(false);

            } else {
                //Selection, enable the button.
                if (list.getModel().getElementAt(list.getSelectedIndex()).toString().endsWith("/")
                        || list.getModel().getElementAt(list.getSelectedIndex()).toString().endsWith(".")) {
                    pobierz.setText("Otwórz");
                } else {
                    pobierz.setText("Pobierz");
                }
                pobierz.setEnabled(true);
            }
        }
    }
}
