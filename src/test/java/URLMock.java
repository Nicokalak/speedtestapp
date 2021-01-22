import com.mooo.nicolak.downloaders.Units;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Random;

public class URLMock {


    public static class TestURLConnection extends URLConnection {
        protected TestURLConnection(URL url) {
            super(url);
        }

        @Override
        public void connect() {
            // Do your job here. As of now it merely prints "Connected!".
            System.out.println("Connected!");
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (getURL().toString().contains("throwIO")) {
                throw new IOException();
            }
            Random r = new Random();
            byte[] bytes = new byte[getContentLength()];
            r.nextBytes(bytes);
            return new TestInputStream(bytes);
        }

        @Override
        public int getContentLength() {
            return 3 * Units.MB_IN_BYTES;
        }
    }

    public static class TestURLStreamHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL url) {
            return new TestURLConnection(url);
        }


    }

    /***
     *  mock protocol which writes 3MB of random data in 2 seconds.
     */
    public static class TestURLStreamHandlerFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(String protocol) {
            if ("testurl".equals(protocol)) {
                return new TestURLStreamHandler();
            }

            return null;
        }

    }

    public static class TestInputStream extends InputStream {
        private final byte[] buff;
        private int readIdx = 0;
        private Long firstReadTime = null;
        public TestInputStream(byte[] buff) {
            this.buff = buff;
        }

        @Override
        public int read() {
            throw new NotImplementedException();
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) {
            int readCount = 0;
            if (firstReadTime == null) {
                firstReadTime = System.currentTimeMillis();
            } else {
                //don`t read for Sec
                if (System.currentTimeMillis() - firstReadTime <= 1500L) {
                    return 0;
                }
            }
            if (readIdx >= this.buff.length) {
                return -1;
            }

            for (int i = off; i < len && readIdx < this.buff.length; i++) {
                b[i] = this.buff[readIdx++];
                readCount++;
            }

            return readCount;
        }
    }
}
