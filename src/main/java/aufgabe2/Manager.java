package aufgabe2;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 * Diese Klasse realisert den Zugriff und die Erzeugung der Repositories. Weiterhin enthält sie 
 * die Funktionalitäten für das Abfragen der Attribute.
 */
public class Manager {
	
	private static final Logger LOG = Logger.getLogger(Manager.class);
	public static final String PRIM_TOPIC = "primTopic";
	public static final String ABSTRACT_ = "abstract";
	public static final String LABEL = "label";
	public static final String SUBJ = "subj";
	public static final String SKOS = "skos";
	private String pathToLocalRepoDir;
	
	public Manager(){}
	
	/**
	 * 
	 * @param pathToLocalRepoDir Pfad zu einem Verzeichnis, in dem das Repo angelegt werden soll
	 */
	public Manager(String pathToLocalRepoDir){
		this.pathToLocalRepoDir = pathToLocalRepoDir;
	}
	

	/**
	 * Erzeugt ein lokales Repository
	 * @return ein lokales Repository
	 */
	public Repository createRepository() {
		Manager.LOG.info("Erzeuge lokales Repository...");
		File dataDir = new File(this.pathToLocalRepoDir);
		Repository repo = new SailRepository(new NativeStore(dataDir));
		try {
			repo.initialize();
		} catch (RepositoryException e) {
			Manager.LOG.error("Fehler beim Erzeugen des lokalen Repositories.",
					e);
		}
		return repo;
	}
	
	/**
	 * Gibt ein Repository für den Zugriff auf "http://dbpedia.org/sparql"
	 * @return Repository für "http://dbpedia.org/sparql"
	 */
	public Repository getAccessToSesame() {
		Manager.LOG.info("Baue Verbindung zum Sesame-Repo auf...");
		String sesameServer = "http://dbpedia.org/sparql";
		Repository repo = new HTTPRepository(sesameServer);
		try {
			repo.initialize();
		} catch (Exception e) {
			Manager.LOG.error("Fehler beim Zugriff auf " + sesameServer, e);
		}
		return repo;
	}

	/**
	 * Führt für jede URI, die durch den Crawler ermittelt wurden eine Abfrage für die Werte primTopicOf, abstract, label und subject aus.
	 * @param repo
	 * @param list
	 * @return Ergbnisse der Abfrage
	 */
	public Map<String, Map<String, String>> executeQueries(Repository repo, List<String> list) {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		try {
			RepositoryConnection con = repo.getConnection();
			Repository localRepo = createRepository();
			RepositoryConnection localRepoCon = localRepo.getConnection();
			ValueFactory f = localRepo.getValueFactory();
			try {
				// mache eine Abfrage für jede URI des Crawlers
				for (String resource : list) {
					String queryString = "SELECT ?primTopic ?abstract ?label ?subj ?skos WHERE { <"
							+ resource
							+ ">  foaf:primaryTopic ?primTopic  . "
							+ "?primTopic dbpedia-owl:abstract ?abstract . "
							+ "?primTopic rdfs:label ?label . "
							+ "?primTopic <http://purl.org/dc/terms/subject> ?subj . "
							+ "?subj <http://www.w3.org/2004/02/skos/core#broader> ?skos "
							+ "FILTER(  langMatches(lang(?label), \"EN\")  &&  langMatches(lang(?abstract), \"EN\") ) }";
					TupleQuery tupleQuery = con.prepareTupleQuery(
							QueryLanguage.SPARQL, queryString);

					TupleQueryResult result = tupleQuery.evaluate();
					try {
						while (result.hasNext()) { // iterate over the result
							BindingSet bindingSet = result.next();
							String primTopic = bindingSet.getValue("primTopic")
									.toString();
							String abstr = bindingSet.getValue("abstract")
									.toString();
							String label = bindingSet.getValue("label")
									.toString();
							String subj = bindingSet.getValue("subj")
									.toString();
							String skos = bindingSet.getValue("skos")
									.toString();
							
							Map<String,String> innerMap = new HashMap<String, String>();
							innerMap.put(PRIM_TOPIC, primTopic);
							innerMap.put(ABSTRACT_, abstr);
							innerMap.put(LABEL, label);
							innerMap.put(SUBJ, subj);
							innerMap.put(SKOS, skos);
							
							map.put(resource, innerMap);
							
							localRepoCon.add(f.createURI(resource), f.createURI("foaf:primaryTopic"), f.createURI(primTopic));
							localRepoCon.add(f.createURI(resource), f.createURI("dbpedia-owl:abstract"), f.createLiteral(abstr));
							localRepoCon.add(f.createURI(resource), f.createURI("<http://purl.org/dc/terms/subject>"), f.createURI(subj));
							localRepoCon.add(f.createURI(resource), f.createURI("rdfs:label"), f.createLiteral(label));
							localRepoCon.commit();
//							Manager.LOG.info("### AUSGABE: " + primTopic
//									+ " ------ " + abstr + " ------ " + label
//									+ " ------ " + subj + " ------ " + skos);
						}
					} finally {
						result.close();
					}
				}

			} finally {
				con.close();
				localRepoCon.close();
				localRepo.shutDown();
			}
		} catch (OpenRDFException e) {
			Manager.LOG.error("Fehler beim Ausführen der Abfrage.", e);
		}
		return map;
	}
}
