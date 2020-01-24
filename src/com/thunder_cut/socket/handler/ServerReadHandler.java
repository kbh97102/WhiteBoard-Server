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
import java.io.BufferedInputStream;
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

            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                System.out.println("Server Read Error");
            }
        };
    }

    /**
     * If you want to display image, you must add Consumer before start reading
     *
     * @param displayImageInJLabel ConsumerType
     */
    public void addDisplay(Consumer<ImageIcon> displayImageInJLabel) {
        this.displayImageInJLabel = displayImageInJLabel;
    }

    public CompletionHandler<Integer, Attachment> getReadHandler() {
        return readHandler;
    }
}
