import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aufgabe1.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arte on 08.12.2014.
 */
@SuppressWarnings("serial")
public class CrawlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Crawler crawl = new Crawler();
		String start = "http://en.wikipedia.org/wiki/Data_mining";
		ArrayList<String> test = crawl.service(start);
        StringBuilder JSON = new StringBuilder();

        JSON.append("{\n");
        JSON.append("\"name\":\"" + start + "\",\n");
        JSON.append("\"children\":[\n");

        for(String s : test){
            JSON.append("\t {\n");
            JSON.append("\t\t \"name\":\"" + s + "\",\n");
            JSON.append("\t\t \"children\":[\n");
            JSON.append("\t\t\t {\"name\":\"" + s +"\"}\n");

            JSON.append("\t\t ]\n");
            JSON.append("\t },\n");
        }JSON.deleteCharAt(JSON.toString().length() -2);
        JSON.append("]\n");
        JSON.append("}\n");


        resp.setContentType("text/json");
        resp.getWriter().write(JSON.toString());
        System.out.println(JSON.toString());
    }
}
