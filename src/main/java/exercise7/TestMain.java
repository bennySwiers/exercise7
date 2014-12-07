package exercise7;

import org.openrdf.repository.Repository;


public class TestMain {
	private static final String dataPath = "C:\\Benny\\Uni\\Master\\1. Semester\\Netzbasierte Informationssysteme\\Übung\\7. Zettel\\data";
	private static final String repoId = "example-db";

	
	public static void main(String[] args) {
		Manager creator = new Manager();
		Repository repo = creator.createRepository(dataPath);
//		creator.getAccess(repoId);
		creator.addTestData(repo);
		creator.queryTest(repo);
	}

}
