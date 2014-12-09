package aufgabe1;
import org.htmlparser.beans.LinkBean;
import java.net.URL;

/*
        Created by IntelliJ IDEA.
        User: voskart
        Date: 11.11.14
        Time: 14:04
        To change this template use File | Settings | File Templates.
 */

public class LinkParser {
    
    String url;

    
    public LinkParser(String Url)
    {
        url = Url;
    }
    
    public URL[] ExtractLinks()
    {
    	LinkBean linkBean = new LinkBean();
    	linkBean.setURL(this.url);
    	URL[] links = linkBean.getLinks();
    	return links;
    }
 }

