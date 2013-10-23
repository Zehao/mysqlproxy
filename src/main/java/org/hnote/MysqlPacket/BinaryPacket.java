/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hnote.MysqlPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author xianmao.hexm 2011-5-6 上午10:58:33
 */
public class BinaryPacket extends MySQLPacket {

    public static final byte UNSET = 0;
    public static final byte OK = 1;
    public static final byte ERROR = 2;
    public static final byte HEADER = 3;
    public static final byte FIELD = 4;
    public static final byte FIELD_EOF = 5;
    public static final byte ROW = 6;
    public static final byte PACKET_EOF = 7;
    public static final byte RESULT_SET = 8;
    public byte[] data;
    public byte type = UNSET;

    public void read(InputStream in) throws IOException {
        packetLength = StreamUtil.readUB3(in);
        packetId = StreamUtil.read(in);
        byte[] ab = new byte[packetLength];
        StreamUtil.read(in, ab, 0, ab.length);
        data = ab;
        if(data[0] == (byte)0x00){
            type = OK;
        }else if(data[0] == (byte)0xff){
            type = ERROR;
        }else if(data[0] == (byte)0xfe){
            type = PACKET_EOF;
        }else{
            type = RESULT_SET;
        }
        
        
    }

    public void write(OutputStream out) throws IOException {
        StreamUtil.writeUB3(out, packetLength);
        StreamUtil.write(out, packetId);
        StreamUtil.write(out, data);
    }

    public int getPacketLength() {
        return packetLength;
    }

    public int getPacketId() {
        return packetId;
    }

    @Override
    public int calcPacketSize() {
        return data == null ? 0 : data.length;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Binary Packet";
    }
}
