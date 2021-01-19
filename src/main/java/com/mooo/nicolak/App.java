package com.mooo.nicolak;

import com.mooo.nicolak.downloaders.DefaultDownloader;
import com.mooo.nicolak.downloaders.Downloader;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class App {
    public final static String DEFAULT = "https://nicokalak.files.wordpress.com/2020/12/out.key";
    private static Downloader downloader = new DefaultDownloader();

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        System.exit(app.runApp(args));
    }
    
    public int runApp(String... args) throws InterruptedException {
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(getAppOpts(), args);

            String href = Optional.ofNullable(line.getOptionValue("h")).orElse(DEFAULT);
            downloader.setHref(href);
            Thread t = new Thread(downloader);
            t.start();
            while (t.isAlive()) {
                String mbPerSec = String.format("%.2f MB/s", downloader.getMBPerSec());
                System.out.print(mbPerSec);
                Thread.sleep(500);
                for (int x = 0; x < mbPerSec.length(); x++) {
                    System.out.print("\b");
                }

            }
            System.out.println(downloader);
            if (line.hasOption("f")) {
                File stats = new File(line.getOptionValue("f"));
                if (!stats.exists() && stats.canWrite()) {
                    stats.createNewFile();
                }
                FileUtils.writeStringToFile(stats,
                        String.format("%d %.2f\n",System.currentTimeMillis() ,downloader.getMBPerSec()),
                        Charset.defaultCharset(),
                        true);
            }

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } catch (IOException ioe) {
            System.err.println("failed to write stats.  Reason: " + ioe.getMessage());
        }

        return downloader.getDownloadStatus();
    }

    private static Options getAppOpts() {
        Options options = new Options();
        options.addOption("f","stats-file", true, "statistics file");
        options.addOption("h","href", true, "custom link for test");

        return options;

    }

    public static void setDownloader(Downloader d) {
        App.downloader = d;
    }
}
