package Server;

import javax.transaction.xa.Xid;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class CaroServer {
    int n=15;
    List<Point> dadanh = new ArrayList<Point>();

        Vector<Xuly> cls = new Vector<Xuly>();

    public static void main(String[] args) {
        new CaroServer();
    }

    public CaroServer() {
        try {
            ServerSocket server = new ServerSocket(5000);
            while (true) {
                Socket s = server.accept();
//                new Xuly(this).start();
                Xuly x= new Xuly(this,s);
                cls.add(x);
                x.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class Xuly extends Thread{
    Socket soc;
    CaroServer server ;
    public boolean Status = true;
    public Xuly(CaroServer server, Socket soc) {
        this.server = server;
        this.soc=soc;
    }
    public void run() {

        try {
            System.out.println("New");
            while (true) {
                System.out.println("wait");
                DataInputStream dis = new DataInputStream(soc.getInputStream());
                String s = dis.readUTF();
                System.out.println(s);
                if (s.equals("want to join")) {
                    System.out.println("New user");
                    DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
                    if (server.cls.size() <= 1) {
                        dos.writeUTF("you are a player");
                        System.out.println("New Player");
                    } else {
                        dos.writeUTF("you are a viewer");
                        System.out.println("New viewer");
                    }
                }
            }
        }catch (Exception e)
        {
        }

        try {
            DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
            for (int i=0; i<server.dadanh.size(); i++){
                dos.writeUTF(server.dadanh.get(i).x +"," + server.dadanh.get(i).y);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loop: while (true) {
            if(Status == false) {
                Thread.currentThread().interrupt();
            }
            try {
                DataInputStream dis =new DataInputStream(soc.getInputStream());
                String s = dis.readUTF();
                if(this != server.cls.get(0) && this != server.cls.get(1)) continue;

                if(server.dadanh.size() %2 == 0 && this != server.cls.get(0)) continue;
                if(server.dadanh.size() %2 == 1 && this != server.cls.get(1)) continue;

                int idx = Integer.parseInt(s.split(",")[0]);
                int idy = Integer.parseInt(s.split(",")[1]);

                for (Point p : server.dadanh){
                    if(p.x == idx && p.y == idy) {
                        continue loop;
                    }
                }
                server.dadanh.add(new Point(idx,idy));
                for (int i=0;i<server.cls.size(); i++){
                    DataOutputStream dos = new DataOutputStream(server.cls.get(i).soc.getOutputStream());
                    dos.writeUTF(s);
                    new CheckStatus(this).start();
                }

            } catch (IOException e) {

            }
        }
    }
}

 class CheckStatus extends Thread {
     public Xuly xuli;

     public CheckStatus(Xuly xuli) {
         this.xuli = xuli;
     }

     public void run() {
         Date t1 = new java.util.Date();
         while (true) {
             try {
                 DataInputStream dis = new DataInputStream(xuli.soc.getInputStream());
                 String s = dis.readUTF();
                 if (s.equals( "I am alive" )) {
                     System.out.println(this.getId() + "User is avlive");
                     Thread.currentThread().interrupt();
                 }
                 Date t2 = new java.util.Date();
                 long diff = t1.getTime() - t2.getTime();
                 long diffSeconds = diff / 1000 % 60;
                 if (diffSeconds >= 60) {
                     xuli.server.cls.remove(this.xuli);
                     System.out.println(this.getId() + "User is Disconnect");
                     this.xuli.Status = false;
                     Thread.currentThread().interrupt();
                 }
             } catch (Exception e) {
             }
         }
     }
 }