package net.jsocket.test;

import net.jsocket.Message;

public class PayloadTest implements Message {
    private final byte[] data;
    private final int size;

    public PayloadTest(int size) {
        this.data = new byte[size];
        this.size = size;

        for (int i = 0; i < size; i++) {
            this.data[i] = (byte) (i % 256);
        }
    }

    private boolean isValid() {
        for (int i = 0; i < data.length; i++) if (this.data[i] != (byte) (i % 256)) return false;
        return true;
    }

    public boolean verify() {
        System.out.println("Begin verify payload test object");
        boolean isValid = isValid();
        if (isValid) System.out.println("Payload valid");
        else System.out.println("Payload contains errors");
        return isValid;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String getDescription() {
        return "Payload test size: " + data.length;
    }
}
