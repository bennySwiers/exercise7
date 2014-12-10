import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;

import aufgabe1.Crawler;
import aufgabe2.Manager;

@SuppressWarnings("serial")
public class CrawlServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(CrawlServlet.class);
	public String localDir;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Crawler crawl = new Crawler();
		String start = "http://en.wikipedia.org/wiki/Data_mining";
		
		CrawlServlet.LOG.info("Crawler startet...");
		ArrayList<String> list = crawl.service(start);
		CrawlServlet.LOG.info("Crawler ist fertig...");
		
		Map<String, Map<String, String>> infoMap = getInfos(list);
		
		
        StringBuilder JSON = new StringBuilder();

        JSON.append("{\n");
        JSON.append("\"name\":\"" + start + "\",\n");
        JSON.append("\"children\":[\n");
        
        for (String uri : infoMap.keySet()) {
        	JSON.append("\t {\n");
        	JSON.append("\t\t \"name\":\"" + uri + "\",\n");
        	addChildren(JSON, infoMap.get(uri));
        	JSON.append("\t },\n");
		}
        JSON.deleteCharAt(JSON.toString().length() -2);
        JSON.append("]\n");
        JSON.append("}\n");
        resp.setContentType("text/json");
        resp.getWriter().write(JSON.toString());
        System.out.println(JSON.toString());
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	this.localDir = (String) req.getParameter("localdir"); 
    	System.out.println("Hallo" + this.localDir);
    }
    
    private void addChildren(StringBuilder JSON, Map<String, String> childrenOfChild){
    	 JSON.append("\t\t \"children\":[\n");
    	for (String field : childrenOfChild.keySet()) {
    		if(field.equals(Manager.SUBJ)){    			
    			JSON.append("\t\t\t {\n");
    			JSON.append("\t\t\t\t \"name\":\"" + field + " = " +  childrenOfChild.get(field) + "\"\n");
    			JSON.append("\t\t\t },\n");
    		}
    	}
         JSON.deleteCharAt(JSON.toString().length() -2);
         JSON.append("\t\t]\n");
    }
    
    private Map<String, Map<String, String>> getInfos(List<String> list){
//    	String dataPath = "C:\\Benny\\Uni\\Master\\1. Semester\\Netzbasierte Informationssysteme\\Übung\\7. Zettel\\data";
    	String dataPath = this.localDir.trim();
    	Manager creator = new Manager(dataPath);
		Repository sesameRepo = creator.getAccessToSesame();
		Map<String, Map<String, String>> map = creator.executeQueries(
				sesameRepo, list);
		try {
			sesameRepo.shutDown();
		} catch (Exception e) {
			System.out.println("Fehler bei Shutdown der Repositories. "+ e.getStackTrace());
		}
		return map;
    }
}
