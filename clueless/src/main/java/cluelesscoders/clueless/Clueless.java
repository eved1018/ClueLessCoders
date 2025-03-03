/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cluelesscoders.clueless;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
/** To run, in command line use java Clueless c for client side
 *                              java Clueless s for server side
 *  Type ESC_SEQ in command line to terminate both programs
 * @see ESC_SEQ X
 * @author  Chris Dixon
 * @version 1.0
 */
public class Clueless {

    public static void main(String[] args) throws IOException{
        if(args[0].equals("s")){ 
            Server s = new Server();
            s.start();
        }
        else if(args[0].equals("c")){
            Client c = new Client();
            c.start();
        } 
    }
}



