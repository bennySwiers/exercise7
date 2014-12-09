package aufgabe2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.NativeStore;

public class Manager {
	private static final Logger LOG = Logger.getLogger(Manager.class);
	private static final String ABSTRACT = "dbpedia:ontology/abstract";
	private static final String LABEL = "rdfs:label";
	private static final String TOPIC = "foaf:isPrimaryTopicOf";
	private static final String SUBJECT = "http://purl.org/dc/terms/subject";

	public Repository createRepository(String dataPath) {
		File dataDir = new File(dataPath);
		// String indexes = "spoc,posc,cosp";
		Repository repo = new SailRepository(new NativeStore(dataDir));
		try {
			repo.initialize();
		} catch (RepositoryException e) {
			Manager.LOG.error("Fehler beim Erzeugen des lokalen Repositories.",
					e);
		}
		return repo;
	}

	public Repository getAccessToSesame() {
		String sesameServer = "http://dbpedia.org/sparql";
		Repository repo = new HTTPRepository(sesameServer); // , repoId);
		try {
			repo.initialize();
		} catch (RepositoryException e) {
			Manager.LOG.error("Fehler beim Zugriff auf " + sesameServer, e);
		}
		return repo;
	}

	/**
	 * SELECT ?s ?o WHERE { <http://dbpedia.org/resource/Battle_of_Hundsfeld>
	 * <http://purl.org/dc/terms/subject> ?o } limit 5
	 * 
	 * SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/subject> ?o } limit 5
	 * 
	 * @param repo
	 */

	public void queryTest(Repository repo) {
		try {
			RepositoryConnection con = repo.getConnection();
//			String prefix = "PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX : <http://dbpedia.org/resource/> PREFIX dbpedia2: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";
			String prefix = "PREFIX : <http://dbpedia.org/resource/>";
			
			try {
//				String queryString = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/subject> ?o } limit 5 ";
				
				String queryString = prefix + " SELECT ?property ?value WHERE { <http://dbpedia.org/resource/Data_mining> ?property ?value }"; //  :Experiment
				TupleQuery tupleQuery = con.prepareTupleQuery(
						QueryLanguage.SPARQL, queryString);

				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) { // iterate over the result
						BindingSet bindingSet = result.next();
						String property = bindingSet.getValue("property").toString();
						String value = bindingSet.getValue("value").toString();
						
						if(property.equals(ABSTRACT) || property.equals(LABEL) || property.equals(TOPIC) || property.equals(SUBJECT) ){							
							Manager.LOG.info("### AUSGABE: " + property + "    " + value);
						}
					}
				} finally {
					result.close();
				}
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			Manager.LOG.error("Fehler beim Ausführen der Abfrage.", e);
		}
	}
	
	public void writeToMyRepo(Repository myRepo, Repository sesameRepo) {
		String resource = "<http://dbpedia.org/resource/Data_mining>";
		String prefix = "PREFIX : <http://dbpedia.org/resource/>";
		String queryString = prefix + " SELECT ?property ?value WHERE { " + resource + " ?property ?value }"; //  :Experiment
		try {
			RepositoryConnection sesameCon = sesameRepo.getConnection();
			RepositoryConnection myCon = myRepo.getConnection();
			ValueFactory f = myRepo.getValueFactory();
			try {
				TupleQuery tupleQuery = sesameCon.prepareTupleQuery(
						QueryLanguage.SPARQL, queryString);

				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) { // iterate over the result
						BindingSet bindingSet = result.next();
						String property = bindingSet.getValue("property").toString();
						String value = bindingSet.getValue("value").toString();
						
						if(property.equals(ABSTRACT) || property.equals(LABEL) || property.equals(TOPIC) || property.equals(SUBJECT) ){							
							URI resourceUri = f.createURI(property);
							URI propertyUri = f.createURI(property);
							URI valueUri = f.createURI(value);
							myCon.add(resourceUri, propertyUri, valueUri);
						}
						
					}
				} finally {
					result.close();
				}
			} finally {
				sesameCon.close();
				myCon.close();
			}
		} catch (OpenRDFException e) {
			Manager.LOG.error("Fehler beim Ausführen der Abfrage.", e);
		}
		queryMyrepoTest(myRepo);
	}
	
	public void queryMyrepoTest(Repository repo) {
		try {
			RepositoryConnection con = repo.getConnection();
			try {
				
				String queryString = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/subject> ?o }"; //  :Experiment
				TupleQuery tupleQuery = con.prepareTupleQuery(
						QueryLanguage.SPARQL, queryString);

				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) { // iterate over the result
						BindingSet bindingSet = result.next();
						String property = bindingSet.getValue("s").toString();
						String value = bindingSet.getValue("o").toString();
						
						if(property.equals(ABSTRACT) || property.equals(LABEL) || property.equals(TOPIC) || property.equals(SUBJECT) ){							
							Manager.LOG.info("### AUSGABE: " + property + "    " + value);
						}
					}
				} finally {
					result.close();
				}
			} finally {
				con.close();
			}
		} catch (OpenRDFException e) {
			Manager.LOG.error("Fehler beim Ausführen der Abfrage.", e);
		}
	}

//	public void queryTestANDWriteRDF(Repository repo) {
//		String pathToRDFDir = "C:\\Benny\\Uni\\Master\\1. Semester\\Netzbasierte Informationssysteme\\Übung\\7. Zettel\\rdf\\file.rdf";
//		String queryString = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/subject> ?o } limit 5 ";
//		RepositoryConnection con = null;
//		try {
//			FileOutputStream out = new FileOutputStream(pathToRDFDir);
//			con = repo.getConnection();
//			RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
//
//			con.prepareGraphQuery(QueryLanguage.SPARQL, queryString).evaluate(
//					writer);
//		} catch (Exception e) {
//			Manager.LOG.error("Fehler...", e);
//		} finally {
//			try {
//				con.close();
//			} catch (RepositoryException e) {
//				Manager.LOG.error("Fehler beim Schließen der Connection.", e);
//			}
//		}
//
//	}
//
//	public void writeRDFFile(String path, Repository repo) {
//		String queryString = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/subject> ?o } limit 5 ";
//
//		RepositoryConnection con = null;
//		try {
//			FileOutputStream out = new FileOutputStream(path);
//			con = repo.getConnection();
//			RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
//
//			con.prepareGraphQuery(QueryLanguage.SPARQL, queryString).evaluate(
//					writer);
//		} catch (Exception e) {
//			Manager.LOG.error("Fehler...", e);
//		} finally {
//			try {
//				con.close();
//			} catch (RepositoryException e) {
//				Manager.LOG.error("Fehler beim Schließen der Connection.", e);
//			}
//		}

		// Model myGraph = null; // a collection of several RDF statements
		// FileOutputStream out = null;
		// try {
		// out = new FileOutputStream(pathToRDFDir);
		// } catch (FileNotFoundException e1) {
		// Manager.LOG.error("Fehler beim Erzeugen des FileOutputStreams.");
		// }
		// RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
		// try {
		// writer.startRDF();
		// for (Statement st: myGraph) {
		// writer.handleStatement(st);
		// }
		// writer.endRDF();
		// }
		// catch (RDFHandlerException e) {
		// Manager.LOG.error("Fehler beim Schreiben einer RDF-Datei.");
		// }
//	}

	

	// public void addTestData(Repository repo) {
	// ValueFactory f = repo.getValueFactory();
	// // create some resources and literals to make statements out of
	// URI alice = f.createURI("http://example.org/people/alice");
	// URI name = f.createURI("http://example.org/ontology/name");
	// URI person = f.createURI("http://example.org/ontology/Person");
	// Literal alicesName = f.createLiteral("Alice");
	// RepositoryConnection con = null;
	// try {
	// con = repo.getConnection();
	// } catch (RepositoryException e1) {
	// Manager.LOG.error("Fehler beim Holen der Connection");
	// }
	//
	// try {
	// // alice is a person
	// con.add(alice, RDF.TYPE, person);
	// // alice's name is "Alice"
	// con.add(alice, name, alicesName);
	// } catch (RepositoryException e) {
	// Manager.LOG.error("Fehler beim Hinzufügen von Werten");
	// }
	// }

	// public void queryTest(Repository repo) {
	// // String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
	// String queryString =
	// "PREFIX dbpedia: <http://dbpedia.org/> SELECT ?n WHERE { :Data_mining <http://purl.org/dc/terms/subject> ?s . ?s rdfs:label ?n } ";
	// RepositoryConnection con = null;
	// TupleQuery tupleQuery = null;
	// try {
	// con = repo.getConnection();
	// tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
	// queryString);
	// } catch (RepositoryException e) {
	// Manager.LOG.error("Es ist ein Fehler aufgetreten...");
	// } catch (MalformedQueryException e) {
	// Manager.LOG.error("Es ist ein Fehler aufgetreten...");
	// }
	//
	// TupleQueryResult result = null;
	// try {
	// result = tupleQuery.evaluate();
	// while (result.hasNext()) { // iterate over the result
	// BindingSet bindingSet = result.next();
	// Manager.LOG.info("### AUSGABE valueOfY x "
	// + bindingSet.getValue("n").toString());
	//
	// // Value valueOfX = bindingSet.getValue("x");
	// // Value valueOfY = bindingSet.getValue("y");
	//
	// // Value valueOfY = bindingSet.getValue("x");
	// // do something interesting with the values here...
	// // Manager.LOG.info("### AUSGABE valueOfX" +
	// // valueOfX.toString());
	// // Manager.LOG.info("### AUSGABE valueOfY" +
	// // valueOfY.toString());
	//
	// // Manager.LOG.info("### AUSGABE valueOfY y "
	// // + bindingSet.getValue("y").toString());
	// // Manager.LOG.info("### AUSGABE valueOfY z "
	// // + bindingSet.getValue("z").toString());
	// }
	// } catch (QueryEvaluationException e) {
	// Manager.LOG.error("Es ist ein Fehler aufgetreten...");
	// } finally {
	// if (result != null) {
	// try {
	// result.close();
	// } catch (QueryEvaluationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
}
