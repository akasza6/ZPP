/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zpp;

/**
 * Klasa sprawdza czy podany port mieści się w przedziale 0-65535
 * @author akasza
 */
public class TestNrPortu {

    /**
     * 
     * @param i numer portu
     * @return prawda gdy liczba spełnia założenia przedziału (0-6553)
     * @throws ZlyNrPortu 
     */
    public boolean spr(int i) throws ZlyNrPortu{
        if(i<0){
            throw new ZlyNrPortu();
        } else if(i>65535) {
            throw new ZlyNrPortu();
        }
        else {
            return true;
        }
    }

}
