package cluelesscoders.clueless;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// A class that can read and write packets over a socket
// todo move all the socket code into here
public class SocketHandler extends Socket {
    Socket socket;
    ObjectInputStream  in;
    ObjectOutputStream  out;

    public SocketHandler(Socket socket, ObjectInputStream oin, ObjectOutputStream oout){
        this.socket = socket;
        this.in  = oin;
        this.out = oout;
    }

    public ServerSocket GetServerSocket(int port){
        try{
            ServerSocket clServer = new ServerSocket(port);
            return clServer;
            
        }
        catch(IOException e){
            System.out.println(e);
        }
                return null;
        
    }

    public void write(Packet p){
        try {
            this.out.writeObject(p);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Packet read(){
        Packet p;
        try {
            p = (Packet) this.in.readObject();
            return p;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


}