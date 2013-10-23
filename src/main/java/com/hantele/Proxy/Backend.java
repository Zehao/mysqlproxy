/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantele.Proxy;

import org.hnote.MysqlPacket.BinaryPacket;
import org.hnote.MysqlPacket.HandshakePacket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Zehao Jin
 */
public class Backend {

    private static final int SERVER_PORT = 3306;
    private static final String host = "222.201.139.38";
    private Socket socket;
    //private BinaryPacket packet;
    private byte[] bytes;

    public Backend() throws Exception {
        socket = new Socket(host, SERVER_PORT);
    }

    public InputStream getInputStream() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (Exception ex) {
        }
        return is;
    }

    public OutputStream getOutputStream() {
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (Exception ex) {
        }
        return os;
    }

    public BinaryPacket readPacket() {
        BinaryPacket packet = new BinaryPacket();
        try {
            packet.read(socket.getInputStream());
        } catch (Exception ex) {
        }
        return packet;
    }

    public void write(BinaryPacket packet) {
        try {
            packet.write(socket.getOutputStream());
        } catch (Exception ex) {
        }

    }
 
}
