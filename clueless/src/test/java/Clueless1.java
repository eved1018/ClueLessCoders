/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */



/** Test code
 *
 * @author Chris Dixon
 * @version 1.0
 */
import java.io.*;
public class Clueless1 {

    public static void main(String[] args) throws IOException{
        if(args[0].equals("s")){ 
            Server1 s = new Server1();
            s.start();
        }
        else if(args[0].equals("c")){
            Client1 c = new Client1();
            c.start();
        }
    }
}
