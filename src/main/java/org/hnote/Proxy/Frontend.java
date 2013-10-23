/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hnote.Proxy;

import org.hnote.MysqlPacket.BinaryPacket;
import java.net.Socket;

/**
 *
 * @author Zehao Jin
 */
public class Frontend {

    private Socket socket;
    //BinaryPacket packet;

    public Frontend(Socket socket) {
        this.socket = socket;
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
