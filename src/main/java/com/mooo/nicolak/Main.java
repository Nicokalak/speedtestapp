package com.mooo.nicolak;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class Main {
    final static String DEFAULT = "https://nicokalak.files.wordpress.com/2020/12/out.key";


    public static void main(String[] args) throws InterruptedException {

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(getAppOpts(), args);


            String link = Optional.ofNullable(line.getOptionValue("l")).orElse(DEFAULT);
            Downloader downloader = new Downloader(link);
            Thread t = new Thread(downloader);
            t.start();
            while (t.isAlive()) {
                String mbPerSec = String.valueOf(downloader.getMBPerSec() + " MB/s");
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
                        String.format("%d %f",System.currentTimeMillis() ,downloader.getMBPerSec()),
                        Charset.defaultCharset(),
                        true);
            }

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } catch (IOException ioe) {
            System.err.println("failed to write stats.  Reason: " + ioe.getMessage());
        }
    }

    private static Options getAppOpts() {
        Options options = new Options();
        options.addOption("f","stats-file", true, "statistics file");
        options.addOption("l","link", true, "custom link for test");

        return options;

    }
}
