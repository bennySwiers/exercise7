package aufgabe1;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;


public class testclass {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public static void main(String[] args) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Crawler crawl = new Crawler();
		ArrayList<String> list = crawl.service("http://en.wikipedia.org/wiki/Data_mining");
		for (String str : list){
			System.out.println(str);
		}
	}

}
