import com.mooo.nicolak.Consts;

import java.io.ByteArrayInputStream;
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
        public InputStream getInputStream() {
            Random r = new Random();
            byte[] randomData = new byte[Consts.MB_IN_BYTES];
            r.nextBytes(randomData);
            return new ByteArrayInputStream(randomData);
        }

        @Override
        public int getContentLength() {
            return Consts.MB_IN_BYTES;
        }
    }

    public static class TestURLStreamHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL url) {
            return new TestURLConnection(url);
        }


    }

    public static class TestURLStreamHandlerFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(String protocol) {
            if ("testurl".equals(protocol)) {
                return new TestURLStreamHandler();
            }

            return null;
        }

    }
}
