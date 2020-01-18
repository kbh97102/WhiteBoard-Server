/*
 * ServerReadHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.socket.handler;

import com.thunder_cut.socket.Attachment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
                selectReadMode(attachment);
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                System.out.println("Server Read Error");
            }
        };
    }

    /**
     *  this method will determine data type
     * In buffer, first data is 's', data type is String
     * else 'i', data type is Image
     * @param attachment Client Information
     */
    private void selectReadMode(Attachment attachment){
        ByteBuffer buffer = attachment.getBuffer();
        System.out.println(buffer.toString()+"TEst");
        byte[] arr = buffer.array();
        buffer.flip();

        if(arr[0] == (byte)'s'){
            System.arraycopy(arr,1,arr,0,arr.length-1);
            buffer.clear();
            buffer = ByteBuffer.wrap(arr);
            readString(attachment);
        }
        else{
            System.arraycopy(arr,1,arr,0,arr.length-1);
            buffer.clear();
            buffer = ByteBuffer.wrap(arr);
            readImage(attachment);
        }
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

    /**
     * If you want to display image, you must add Consumer before start reading
     * @param displayImageInJLabel ConsumerType
     */
    public void addDisplay(Consumer<ImageIcon> displayImageInJLabel){
        this.displayImageInJLabel = displayImageInJLabel;
    }
    public CompletionHandler<Integer, Attachment> getReadHandler() {
        return readHandler;
    }
}
