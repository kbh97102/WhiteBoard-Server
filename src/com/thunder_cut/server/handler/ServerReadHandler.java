/*
 * ServerReadHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.server.handler;

import com.thunder_cut.server.Attachment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ServerReadHandler {

    private CompletionHandler<Integer, Attachment> readHandler;
    private Charset charset = StandardCharsets.UTF_8;
    private Consumer<ImageIcon> displayImageInJLabel;

    public ServerReadHandler() {
        readHandler = new CompletionHandler<Integer, Attachment>() {
            /**
             * If read method success completed will start
             * @param result Statement
             * @param attachment Client Information
             */
            @Override
            public void completed(Integer result, Attachment attachment) {
                readString(attachment);
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                System.out.println("Server Read Error");
            }
        };
    }

    private void selectReadMode(Attachment attachment){
        ByteBuffer buffer = attachment.getBuffer();
        byte[] arr = buffer.array();
        if(arr[0] == (byte)'s'){
            System.arraycopy(arr,1,arr,0,arr.length);
            attachment.getBuffer().clear();
            attachment.getBuffer().put(arr);
            readString(attachment);
        }
        else{
            System.arraycopy(arr,1,arr,0,arr.length);
            attachment.getBuffer().clear();
            attachment.getBuffer().put(arr);
//            readImage(attachment,);
        }
        buffer = ByteBuffer.wrap(arr);
    }
    /**
     * Decode data and display
     *
     * @param clientInfo client Information
     */
    public void readString(Attachment clientInfo) {
        ByteBuffer buffer = clientInfo.getBuffer();
        try {
            System.out.println(clientInfo.getClient().getRemoteAddress() + " is send this -> " + charset.decode(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.clear();
        clientInfo.getClient().read(clientInfo.getBuffer(), clientInfo, readHandler);
    }

    /**
     * Get ByteBuffer data and transform to ImageIcon
     *
     * @param clientInfo           Client Information
     */
    public void readImage(Attachment clientInfo) {
        try {
            ByteBuffer buffer = clientInfo.getBuffer();
            buffer.flip();
            ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
            BufferedImage image = ImageIO.read(input);
            ImageIcon icon = new ImageIcon(image);
            displayImageInJLabel.accept(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDisplay(Consumer<ImageIcon> displayImageInJLabel){
        this.displayImageInJLabel = displayImageInJLabel;
    }
    public CompletionHandler<Integer, Attachment> getReadHandler() {
        return readHandler;
    }
}
