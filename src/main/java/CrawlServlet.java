import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import aufgabe1.Crawler;
import aufgabe2.Manager;

/**
 * Created by Arte on 08.12.2014.
 */
@SuppressWarnings("serial")
public class CrawlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Crawler crawl = new Crawler();
		String start = "http://en.wikipedia.org/wiki/Data_mining";
		ArrayList<String> list = crawl.service(start);
		Map<String, Map<String, String>> infoMap = getInfos(list);
		
		
        StringBuilder JSON = new StringBuilder();

        JSON.append("{\n");
        JSON.append("\"name\":\"" + start + "\",\n");
        JSON.append("\"children\":[\n");
        
        for (String uri : infoMap.keySet()) {
			jsonAdChildWithChildren(JSON, uri, infoMap.get(uri) );
		}
        JSON.deleteCharAt(JSON.toString().length() -2);
        JSON.append("]\n");
        JSON.append("}\n");
        resp.setContentType("text/json");
        resp.getWriter().write(JSON.toString());
        System.out.println(JSON.toString());
    }
    
    private void jsonAdChildWithChildren(StringBuilder JSON, String child, Map<String, String> childrenOfChild){
    	 JSON.append("\t {\n");
         JSON.append("\t\t \"name\":\"" + child + "\",\n");
         jsonAdChild(JSON, childrenOfChild);
         JSON.append("\t },\n");
    }
    
    private void jsonAdChild(StringBuilder JSON,  Map<String, String> childrenOfChild){
    	for (String field : childrenOfChild.keySet()) {
    		 JSON.append("\t {\n");
             JSON.append("\t\t \"name\":\"" + field + " = " +  childrenOfChild.get(field) + "\",\n");
             JSON.append("\t },\n");
		}
    	 JSON.deleteCharAt(JSON.toString().length() -2);

   }
    
    private Map<String, Map<String, String>> getInfos(List<String> list){
    	Manager creator = new Manager();
		Repository sesameRepo = creator.getAccessToSesame();
		Map<String, Map<String, String>> map = creator.executeQueries(
				sesameRepo, list);
		try {
			sesameRepo.shutDown();
		} catch (RepositoryException e) {
			System.out.println("Fehler bei Shutdown der Repositories. "+ e.getStackTrace());
		}
		return map;
    }
    
//	@Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		Crawler crawl = new Crawler();
//		String start = "http://en.wikipedia.org/wiki/Data_mining";
//		ArrayList<String> test = crawl.service(start);
//        StringBuilder JSON = new StringBuilder();
//
//        JSON.append("{\n");
//        JSON.append("\"name\":\"" + start + "\",\n");
//        JSON.append("\"children\":[\n");
//
//        for(String s : test){
//            JSON.append("\t {\n");
//            JSON.append("\t\t \"name\":\"" + s + "\",\n");
//            JSON.append("\t\t \"children\":[\n");
//            JSON.append("\t\t\t {\"name\":\"" + s +"\"}\n");
//
//            JSON.append("\t\t ]\n");
//            JSON.append("\t },\n");
//        }JSON.deleteCharAt(JSON.toString().length() -2);
//        JSON.append("]\n");
//        JSON.append("}\n");
//
//
//        resp.setContentType("text/json");
//        resp.getWriter().write(JSON.toString());
//        System.out.println(JSON.toString());
//    }
    
}
