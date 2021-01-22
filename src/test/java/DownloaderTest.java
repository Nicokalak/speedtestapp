import com.mooo.nicolak.downloaders.DefaultDownloader;
import com.mooo.nicolak.downloaders.Downloader;
import com.mooo.nicolak.downloaders.MultiDownloader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class DownloaderTest {

    @Test( expected = MalformedURLException.class)
    public void BadURLTest() throws MalformedURLException {
        Downloader d = new DefaultDownloader();
        d.setHref(Collections.singletonList("http://goodurl.com"));
        d.setHref(Collections.singletonList("blablabla"));
    }

    @BeforeClass
    public static void setUrlHandler() {
        URL.setURLStreamHandlerFactory(new URLMock.TestURLStreamHandlerFactory());
    }
    @Test()
    public void SpeedTest() throws MalformedURLException {
        Downloader d = new DefaultDownloader("testurl://thisistest");
        d.run();
        Assert.assertEquals(Double.valueOf(1.5), d.getMBPerSec());
        Assert.assertTrue(d.toString().contains(String.format("%.2f MB/s", d.getMBPerSec())));
    }

    @Test()
    public void SpeedTestMulti() throws MalformedURLException {
        Downloader d = new MultiDownloader();
        d.setHref(Arrays.asList("testurl://thisistest300", "testurl://thisistest400"));
        d.run();
        Assert.assertEquals(Double.valueOf(1.5 * 4), d.getMBPerSec());
        Assert.assertTrue(d.toString().contains(String.format("%.2f MB/s", d.getMBPerSec())));
    }

    @Test()
    public void IOErrorTest() throws MalformedURLException {
        Downloader d = new DefaultDownloader("testurl://throwIO");
        d.run();
        Assert.assertEquals(Downloader.RUN_UNEXPECTED_ERROR, d.getDownloadStatus());
    }





}
