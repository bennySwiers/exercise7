package aufgabe2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import aufgabe1.Crawler;

public class TestMain {
	private static final Logger LOG = Logger.getLogger(TestMain.class);

	public static void main(String[] args) {
		Crawler crawl = new Crawler();
		List<String> list = null;
		try {
			list = crawl.service("http://en.wikipedia.org/wiki/Data_mining");
		} catch (ServletException e) {
			TestMain.LOG.error("Fehler bei der Ausführung des Crawlers.", e);
		} catch (IOException e) {
			TestMain.LOG.error("Fehler bei der Ausführung des Crawlers.", e);
		}

		Manager creator = new Manager();
		Repository sesameRepo = creator.getAccessToSesame();
		Map<String, Map<String, String>> map = creator.executeQueries(
				sesameRepo, list);
		for (String uri : map.keySet()) {
			System.out.println("##### " + uri + " #####");
			Map<String, String> values = map.get(uri);
			for (String field : values.keySet()) {
				System.out.println(field + " = " + values.get(field));
			}
			System.out.println("\n");
		}

		try {
			sesameRepo.shutDown();
		} catch (RepositoryException e) {
			TestMain.LOG.error("Fehler bei Shutdown der Repositories.", e);
		}
		TestMain.LOG.info("FERTIG...");
	}
}
