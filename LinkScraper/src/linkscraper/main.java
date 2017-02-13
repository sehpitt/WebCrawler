package linkscraper;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Eliezer Hashimi
 */
public class main {
        public static void main(String[] args) throws IOException, InterruptedException {
        
        String[] startingURLs = {"http://www.touro.edu/", "http://www.psu.edu//",
            "http://www.nyu.edu/", "http://www.qc.cuny.edu/", "https://wustl.edu/", "https://www.osu.edu/", 
            "https://www.umich.edu/", "http://www.cmu.edu/", "http://www.ucla.edu/","https://www.stanford.edu/"};
        
        LinkScraper.URLs.addAll(Arrays.asList(startingURLs));

        LinkScraper theScraper = new LinkScraper("http://www.nyu.edu/");

        theScraper.createThreads();
 
        
        String userName = "lander",
                password = "dovberish",
                url = "lcm.cqzhmas5ky4m.us-east-1.rds.amazonaws.com:1433";
        Database dB = new Database(url, userName, password);
        dB.databaseConnect(String.format("jdbc:jtds:sqlserver://%s//hashimi", url));
    }
}
