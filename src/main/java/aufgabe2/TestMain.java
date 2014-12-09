package aufgabe2;

import org.openrdf.repository.Repository;


public class TestMain {
	private static final String dataPath = "C:\\data";
//	private static final String pathToRDFDir = "C:\\Benny\\Uni\\Master\\1. Semester\\Netzbasierte Informationssysteme\\Übung\\7. Zettel\\rdf\\file.rdf";
//	private static final String repoId = "example-db";

	
	public static void main(String[] args) {
		Manager creator = new Manager();
		Repository sesameRepo = creator.getAccessToSesame();
		Repository myRepo = creator.createRepository(dataPath);
		creator.writeToMyRepo(myRepo, sesameRepo );
//		creator.getAccess(repoId);

		
//		creator.queryTest(sesameRepo); // WICHITG

		

//		creator.queryTestANDWriteRDF(sesameRepo);
		
		//		creator.writeRDFFile(pathToRDFDir, repo);
		System.out.println("FERTIG");
	}

}
