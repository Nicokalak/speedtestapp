import com.mooo.nicolak.App;
import com.mooo.nicolak.downloaders.Downloader;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MainTests {
    @Test()
    public void noParamsTest() throws InterruptedException {
        Downloader downloader = mock(Downloader.class);
        App.setDownloader(downloader);
        doNothing().when(downloader).run();
        App app = new App();
        Assert.assertEquals(Downloader.RUN_OK, app.runApp());
    }


}
