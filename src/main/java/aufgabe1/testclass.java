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
		ArrayList<String> list = crawl.service("http://mvnrepository.com/artifact/javax.servlet/servlet-api/2.5");
		for (String str : list){
			System.out.println(str);
		}
	}

}
