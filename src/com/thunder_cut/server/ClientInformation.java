/*
 * ClientInformation.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class has information, read and write about connected client
 */
public class ClientInformation {

    interface WriteCallBack {
        void write(int srcID, char type, ByteBuffer buffer);
    }

    public int ID;

    private SocketChannel client;
    private WriteCallBack writeToAll;

    public ClientInformation(int ID) {
        this.ID = ID;
    }

    public SocketChannel getClient() {
        return client;
    }

    public void setClient(SocketChannel client) {
        this.client = client;
    }

    public void read() {
        new Thread(this::reading).start();
    }

    public void setSending(WriteCallBack callBack) {
        writeToAll = callBack;
    }

    private void reading() {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            try {
                client.read(buffer);
            } catch (IOException e) {
                break;
            }
            buffer.flip();

            char type = buffer.getChar();
            int size = buffer.getInt();
            buffer = ByteBuffer.allocate(size);

            while (buffer.hasRemaining()) {
                try {
                    client.read(buffer);
                } catch (IOException e) {
                    break;
                }
            }

            buffer.flip();
            writeToAll.write(ID, type, buffer);
        }
    }
}

//TODO
//소켓의 버퍼 사이즈 보다 큰 경우 read 한번에 못 읽어옴 그럴경우 바로 보내면 이미지 아니라고 뱉어냄 (Sliding window 생각)개선필요
// call back 인터페이스 만들어서 보낼 id랑 데이터를 던지면 됨 개선필요
//try ~ catch 안에는 필요한 작업만 있게끔 체크  완료
//NullPointerException 조심
//다음주 까지 반드시 완성
//다중클라 환경에서 클라가 접속을 끊을 시  clientgroup를 수정해야됨 write에서 하니까 즉각수정이 안됨
//12가 있다가 12퇴장후 34가 입장을하면 12 자리 그대로있고 34가 나타남
//12가 퇴장시 clientgroup를 빠진만큼 앞으로 당겨야 될듯 함 srcID를 바꿔야 됨