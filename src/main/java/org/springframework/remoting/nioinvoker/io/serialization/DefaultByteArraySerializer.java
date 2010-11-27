package org.springframework.remoting.nioinvoker.io.serialization;

import hr.sting.niocommunicator.serialization.ByteArraySerializer;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author mstipanov
 * @since 24.10.10. 06:58
 */
public class DefaultByteArraySerializer implements ByteArraySerializer {
    @Override
    public byte[] serialize(Object o) throws IOException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            return baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(oos);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> aClass) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (T) ois.readObject();
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }
}
