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

    public ServerReadHandler(){
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

    /**
     * Decode data and display
     * @param clientInfo client Information
     */
    public void readString(Attachment clientInfo){
        ByteBuffer buffer = clientInfo.getBuffer();
        buffer.flip();
        try {
            System.out.println(clientInfo.getClient().getRemoteAddress()+ " is send this -> "+charset.decode(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.clear();
        clientInfo.getClient().read(clientInfo.getBuffer(), clientInfo, readHandler);
    }

    /**
     * Get ByteBuffer data and transform to ImageIcon
     * @param clientInfo Client Information
     * @param displayImageInJLabel Display in screen use ImageIcon that transformed data
     */
    public void readImage(Attachment clientInfo,  Consumer<ImageIcon> displayImageInJLabel){
        try{
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

    public CompletionHandler<Integer, Attachment> getReadHandler(){
        return readHandler;
    }
}
