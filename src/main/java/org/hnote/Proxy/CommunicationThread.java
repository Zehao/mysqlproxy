/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hnote.Proxy;

import org.hnote.MysqlPacket.AuthPacket;
import org.hnote.MysqlPacket.BinaryPacket;
import org.hnote.MysqlPacket.CommandPacket;
import org.hnote.MysqlPacket.ErrorPacket;
import org.hnote.MysqlPacket.FieldPacket;
import org.hnote.MysqlPacket.HandshakePacket;
import org.hnote.MysqlPacket.OkPacket;
import org.hnote.MysqlPacket.ResultSetHeaderPacket;
import org.hnote.MysqlPacket.RowDataPacket;

/**
 *
 * @author Zehao Jin
 */
public class CommunicationThread extends Thread {

    Backend back;
    Frontend front;
    BinaryPacket b2f;
    BinaryPacket f2b;

    public CommunicationThread(Backend back, Frontend front) {
        this.back = back;
        this.front = front;
    }

    @Override
    public void run() {


        /**
         * Phase 1: From server to client. Send handshake packet.
         */
        b2f = back.readPacket();
        front.write(b2f);
        HandshakePacket handshake = new HandshakePacket(b2f);
        System.out.println("SERVER:" + new String(handshake.serverVersion));

        /**
         * Phase 2: From client to server. Send auth packet.
         */
        f2b = front.readPacket();
        back.write(f2b);
        AuthPacket auth = new AuthPacket(f2b);
        System.out.println("CLIENT:" + auth.user );
        
        /**
         * Phase 3: From server to client. send OK/ERROR packet.
         */
        b2f = back.readPacket();
        front.write(b2f);
        if (b2f.type == BinaryPacket.ERROR) {
            System.out.println("SERVER: ERROR");
            return;
        }
        System.out.println("SERVER: OK");
        
        /**
         * Start communication.
         */
        while (true) {

            f2b = front.readPacket();
            back.write(f2b);
            CommandPacket cmdPacket = new CommandPacket(f2b);
            System.out.println("CLIENT:" + cmdPacket.getCommand() + ":" + new String(cmdPacket.getArg()));
            
            if(cmdPacket.getCommand() == BinaryPacket.COM_QUIT)
                return;
            b2f = back.readPacket();
            front.write(b2f);
            if (b2f.type == BinaryPacket.ERROR) {

                ErrorPacket error = new ErrorPacket(b2f);
                System.out.println("SERVER:" + " ERROR " + error.errno + ":" + new String(error.message));
                continue;

            } else if (b2f.type == BinaryPacket.OK) {

                OkPacket ok = new OkPacket(b2f);
                System.out.println("SERVER:" + "OK" + " affectedRows:" + ok.affectedRows + " message:" + (ok.message == null ? "null":new String(ok.message)));
                continue;

            } else { //result set packet or eof packet.

                ResultSetHeaderPacket header = new ResultSetHeaderPacket(b2f);
                System.out.println("SERVER:");


                //n field packet.
                System.out.println("-----------------------------------------------");
                for (int i = 0; i < header.fieldCount; i++) {

                    BinaryPacket bin = back.readPacket();
                    front.write(bin);
                    FieldPacket field = new FieldPacket(bin);
                    System.out.print(new String(field.name) + "\t");
                }

                //followed by a eof packet.
                BinaryPacket bin = back.readPacket();
                front.write(bin);

                System.out.println();

                //rows
                while (true) {
                    BinaryPacket row = back.readPacket();
                    front.write(row);
                    if (row.type == BinaryPacket.PACKET_EOF) {
                        break;
                    }
                    RowDataPacket rowPacket = new RowDataPacket(row, header.fieldCount);
                    for (int i = 0; i < header.fieldCount; i++) {
                        System.out.print(new String(rowPacket.fieldValues.get(i)) + "\t");
                    }
                    System.out.println();
                }
                System.out.println("-----------------------------------------------");
            }
            
            System.gc();
        }

    }
}
