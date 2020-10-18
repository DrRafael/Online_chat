package network;

import sun.nio.cs.UTF_8;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThead;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThead = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
               String msg = in.readLine();
            } catch (IOException e){

                }finally {

                }
            }
        });
        rxThead.start();

    }
}
