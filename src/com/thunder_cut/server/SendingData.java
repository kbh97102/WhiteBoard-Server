/*
 * SendingData.java
 * Author : Arakene
 * Created Date : 2020-02-07
 */
package NoAscync;

import java.nio.ByteBuffer;

public class SendingData {

    public final DataType dataType;
    public final int srcID;
    public final int destID;
    public final byte[] data;

    public SendingData(int srcID, int destID, DataType dataType, byte[] data) {
        this.srcID = srcID;
        this.destID = destID;
        this.dataType = dataType;
        this.data = data;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 14);
        buffer.putChar(dataType.type);
        buffer.putInt(srcID);
        buffer.putInt(destID);
        buffer.putInt(data.length);
        buffer.put(data);

        buffer.flip();

        return buffer;
    }
}
