package org.logbackgelf;

import java.util.Arrays;

/**
 * Responsible for wrapping a subPayload in a GELF Chunk
 */
public class ChunkFactory {

    private final byte[] CHUNKED_GELF_ID;
    private final boolean padSeq;

    public ChunkFactory(byte[] chunked_gelf_id, boolean padSeq) {
        CHUNKED_GELF_ID = chunked_gelf_id;
        this.padSeq = padSeq;
    }

    /**
     * Concatenates everything into a GELF Chunk header and then appends the sub Payload
     */
    public byte[] create(byte[] messageId, byte seqNum, byte numChunks, byte[] subPayload) {
        return  concatArrays(concatArrays(concatArrays(CHUNKED_GELF_ID, messageId), getSeqNums(seqNum, numChunks)), subPayload);
    }

    /**
     * Returns array1 concatenated with array2 (non-destructive)
     *
     * @param array1 The first array
     * @param array2 The array to append to the end of the first
     * @return Array1 + array2
     */
    private byte[] concatArrays(byte[] array1, byte[] array2) {
        byte[] finalArray = Arrays.copyOf(array1, array2.length + array1.length);
        System.arraycopy(array2, 0, finalArray, array1.length, array2.length);
        return finalArray;
    }

    /**
     * Needed because apparently we need to pad this for 0.9.5??
     *
     * @param seqNum
     * @param numChunks
     * @return
     */
    private byte[] getSeqNums(byte seqNum, byte numChunks) {
        return padSeq ? new byte[]{0x00, seqNum, 0x00, numChunks} : new byte[]{seqNum, numChunks};
    }
}
