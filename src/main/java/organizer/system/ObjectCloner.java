package organizer.system;


import java.io.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * ObjectCloner for cloning every object
 * by serialization,because I hate the clone() method.
 */
public final class ObjectCloner {
    private ObjectCloner() {
    }

    /**
     * @param oldObj Object to clone.
     * @return Cloned Object.
     * @throws IOException            IOException.
     * @throws ClassNotFoundException ClassNotFoundException.
     */
    public static Object deepCopy(Object oldObj) throws IOException,
            ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(oldObj);

        oos.flush();
        System.out.println(Arrays.toString(bos.toByteArray()));
        ByteArrayInputStream bin =
                new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        return ois.readObject();
    }

    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }


    public static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
