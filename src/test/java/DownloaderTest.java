import com.mooo.nicolak.Downloader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloaderTest {

    @Test( expected = MalformedURLException.class)
    public void BadURLTest() throws MalformedURLException {
        Downloader d = new Downloader("blabla");
    }

    @Test()
    public void SpeedTest() throws IOException {
        /*
         mock protocol which writes MB of data.
         */
        URL.setURLStreamHandlerFactory(new URLMock.TestURLStreamHandlerFactory());
        URL testURL = new URL("testurl://thisistest");

        Downloader d = new Downloader(testURL);
        d.run();
        Assert.assertEquals(Double.valueOf(1.0), d.getMBPerSec());
    }



}
