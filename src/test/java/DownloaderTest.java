import com.mooo.nicolak.downloaders.DefaultDownloader;
import com.mooo.nicolak.downloaders.Downloader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloaderTest {

    @Test( expected = MalformedURLException.class)
    public void BadURLTest() throws MalformedURLException {
        DefaultDownloader d = new DefaultDownloader();
        d.setHref("http://goodurl.com");
        d.setHref("blablabla");
    }

    @BeforeClass
    public static void setUrlHandler() {
        URL.setURLStreamHandlerFactory(new URLMock.TestURLStreamHandlerFactory());
    }
    @Test()
    public void SpeedTest() throws MalformedURLException {
        DefaultDownloader d = new DefaultDownloader("testurl://thisistest");
        d.run();
        Assert.assertEquals(Double.valueOf(1.5), d.getMBPerSec());
        Assert.assertTrue(d.toString().contains(String.format("%.2f MB/s", d.getMBPerSec())));
    }

    @Test()
    public void IOErrorTest() throws MalformedURLException {
        DefaultDownloader d = new DefaultDownloader("testurl://throwIO");
        d.run();
        Assert.assertEquals(Downloader.RUN_UNEXPECTED_ERROR, d.getDownloadStatus());
    }





}
