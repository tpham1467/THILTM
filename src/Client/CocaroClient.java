package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CocaroClient extends JFrame implements MouseListener,Runnable {

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        new CocaroClient() ;

    }
    int n=15;
    int s=30;
    int of = 50;
    List<Point> dadanh = new ArrayList<Point>() ;

    DataInputStream dis;
    DataOutputStream dos;

    public CocaroClient(){
        this.setSize(n*s + of*2,n*s+of*2);
        this.setTitle("Co Caro Online");
        this.setDefaultCloseOperation(3);
        try {
            Socket soc = new Socket("localhost",5000);
            dis = new DataInputStream(soc.getInputStream());
            dos = new DataOutputStream(soc.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(this).start();
        this.addMouseListener(this);
        this.setVisible(true);
    }

    public void paint(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,this.getWidth(), this.getHeight());

        g.setColor(Color.BLACK);
        for (int i=0; i<=n; i++){
            g.drawLine(of,of +s*i, of+s*n,of + s*i);
            g.drawLine(of + s*i, of, of+s*i, of+s*n);
        }
        g.setFont(new Font("arial",Font.BOLD,s));
        for (int i=0;i<dadanh.size();i++){
            int x= dadanh.get(i).x*s +of +s/2 -s/4;
            int y= dadanh.get(i).y*s +of + s/2 +s/4;

            String s= "o";
            if(i%2!=0){
                s="x";
            }
            g.drawString(s,x,y);
        }
    }
    public void run(){
        try {
            dos.writeUTF("want to join");
            while (true)
            {
                String s = dis.readUTF();
                if(s.equals("you are a player"))
                {
                    JOptionPane.showMessageDialog(null, "you are a player", null ,  JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                else
                {
                    System.out.println(s);
                    JOptionPane.showMessageDialog(null, "you are a viewer", null ,  JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
        catch (Exception e)
        {

        }
        while(true){
            try {
                String s = dis.readUTF();
                int idx = Integer.parseInt(s.split(",")[0]);
                int idy = Integer.parseInt(s.split(",")[1]);
                dadanh.add(new Point(idx,idy));
                dos.writeUTF("I am alive");
                this.repaint();
            } catch (IOException e) {

            }
        }

    }
    @Override
    public void mouseClicked(MouseEvent e) {
        int x= e.getX();
        int y=e.getY();
        System.out.println(x + " " + y);
        if(x<of || x >=of+s*n) return;
        if(y<of || y >=of+s*n) return;

        int idx = (x-of)/s;
        int idy = (y-of)/s;

        for (Point p : dadanh){
            if(p.x == idx && p.y == idy) return;
        }
        try {
            dos.writeUTF(idx + "," + idy);
        } catch (IOException ex) {

        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
