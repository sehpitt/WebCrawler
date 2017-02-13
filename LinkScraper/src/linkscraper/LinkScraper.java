package linkscraper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static linkscraper.LinkScraper.URLs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkScraper {

    Thread[] listOfThreads = new Thread[30];
    static ConcurrentLinkedQueue<String> URLs = new ConcurrentLinkedQueue();
    static Set<String> visitedSites;
    static Set<String> emailList;
    static ConcurrentLinkedQueue<String> emailBufferList = new ConcurrentLinkedQueue();
    static String[] sitesNotAllowed = {".pdf", ".jpeg", ".png", ".mp3",
        "facebook", "vimeo", ".php", "youtube", "twitter"};
    Pattern emailRegex = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9]+");

    public LinkScraper(String URL) {
        URLs.add(URL);
    }

    void createThreads() {
        visitedSites = Collections.synchronizedSet(new HashSet<>());
        emailList = Collections.synchronizedSet(new HashSet<>());
        for (int i = 0; i < listOfThreads.length; i++) {
            listOfThreads[i] = new WebCrawlerThreading();
            listOfThreads[i].start();
        }

        for (int j = 0; j < listOfThreads.length; j++) {
            try {
                listOfThreads[j].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(LinkScraper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class WebCrawlerThreading extends Thread {

        @Override
        public void run() {
            while (!URLs.isEmpty() && emailList.size() < 10000) {
                try {
                    scrapeSites(URLs.remove());
                } catch (IOException | IllegalArgumentException | NullPointerException ex) {
                    Logger.getLogger(LinkScraper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void scrapeSites(String URL) throws IOException {

        Document doc;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException | IllegalArgumentException | NullPointerException ex) {
            return;
        }

        storeSites(doc);
        findEmails(doc);
    }

    private void storeSites(Document doc) {
        Elements links = doc.select("a[href]");

        for (Element Link : links) {
            String domainName = Link.attr("abs:href");

            if (siteIsValid(domainName)) {
                visitedSites.add(domainName);
                URLs.add(domainName);
            }
        }
    }
       private void findEmails(Document doc) {
        Matcher matcher = emailRegex.matcher(doc.text());
        int count = 0;
        while (matcher.find()) {
            String email = matcher.group();
            if (!emailList.contains(email)) {
                emailList.add(email);
                emailBufferList.add(email);
                System.out.println(email + " " + emailList.size() + "  " + count);
                count++;
            }
        }
    }

    private boolean siteIsValid(String domainName) {

        return (!visitedSites.contains(domainName)
                && sitesNotAllowed(domainName));
    }

    private static boolean sitesNotAllowed(String domainName) {

        for (String elt : sitesNotAllowed) {
            if (domainName.contains(elt)) {
                return false;
            }
        }
        return true;
    }

 
}
