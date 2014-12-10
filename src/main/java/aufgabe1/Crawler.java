package aufgabe1;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

/*
 Created by IntelliJ IDEA.
 User: voskart
 Date: 11.11.14
 Time: 14:04
 To change this template use File | Settings | File Templates.
 */

public class Crawler {
	private static final int CRAWL_UNTIL = 150;

	private final static Pattern BINARY_FILES_EXTENSIONS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|svg|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");


	public static boolean shouldnotVisit(String url) {
		String href = url.toLowerCase();

		return BINARY_FILES_EXTENSIONS.matcher(href).matches();
	}

	private static ArrayList<String> indexed;
	private static int i = 0;

	private static void indexDocs(String url) throws Exception {
		try {

			// Crawl links
			LinkParser lp = new LinkParser(url);
			URL[] links = lp.ExtractLinks();
			if (links.length == 0 || shouldnotVisit(url)) {
				return;
			}
			indexed.add(url);
			for (URL l : links) {
				if ((!indexed.contains(l.toURI().toString())) && l != null
						&& (!l.toURI().toString().contains("?"))
						&& (!l.toURI().toString().contains("#")) && i < Crawler.CRAWL_UNTIL) {
					i = i + 1;
					indexDocs(l.toURI().toString());
				}
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public ArrayList<String> service(String site) throws ServletException,
			IOException {

		String link = site;

		indexed = new ArrayList<String>();

		try {
			indexDocs(link);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return indexed;
	}
}
