package exercise7;

import java.io.File;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
import org.openrdf.sail.nativerdf.NativeStore;

public class Manager {
	private static final Logger LOG = Logger.getLogger(Manager.class);

	public Repository createRepository(String dataPath) {
		File dataDir = new File(dataPath);
		String indexes = "spoc,posc,cosp";
		Repository repo = new SailRepository(new NativeStore(dataDir, indexes));
		try {
			repo.initialize();
		} catch (RepositoryException e) {
			Manager.LOG
					.error("Fehler beim Erzeugen des lokalen Repositories.");
		}
		return repo;
	}

	public Repository getAccess(String repoId) {
		String sesameServer = "http://dbpedia.org/sparql";
		Repository repo = new HTTPRepository(sesameServer, repoId);
		try {
			repo.initialize();
		} catch (RepositoryException e) {
			Manager.LOG.error("Fehler beim Zugriff auf " + sesameServer);
		}
		return repo;
	}

	public void addTestData(Repository repo) {
		ValueFactory f = repo.getValueFactory();
		// create some resources and literals to make statements out of
		URI alice = f.createURI("http://example.org/people/alice");
		URI name = f.createURI("http://example.org/ontology/name");
		URI person = f.createURI("http://example.org/ontology/Person");
		Literal alicesName = f.createLiteral("Alice");
		RepositoryConnection con = null;
		try {
			con = repo.getConnection();
		} catch (RepositoryException e1) {
			Manager.LOG.error("Fehler beim Holen der Connection");
		}

		try {
			// alice is a person
			con.add(alice, RDF.TYPE, person);
			// alice's name is "Alice"
			con.add(alice, name, alicesName);
		} catch (RepositoryException e) {
			Manager.LOG.error("Fehler beim Hinzufügen von Werten");
		}
	}

	public void queryTest(Repository repo) {
		String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
		RepositoryConnection con = null;
		TupleQuery tupleQuery = null;
		try {
			con = repo.getConnection();
			tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
		} catch (RepositoryException e) {
			Manager.LOG.error("Es ist ein Fehler aufgetreten...");
		} catch (MalformedQueryException e) {
			Manager.LOG.error("Es ist ein Fehler aufgetreten...");
		}

		TupleQueryResult result = null;
		try {
			result = tupleQuery.evaluate();
			while (result.hasNext()) { // iterate over the result
				BindingSet bindingSet = result.next();
				Value valueOfX = bindingSet.getValue("x");
				Value valueOfY = bindingSet.getValue("y");
				// do something interesting with the values here...
				Manager.LOG.info("### AUSGABE valueOfX" + valueOfX.toString());
				Manager.LOG.info("### AUSGABE valueOfY" + valueOfY.toString());
			}
		} catch (QueryEvaluationException e) {
			Manager.LOG.error("Es ist ein Fehler aufgetreten...");
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
