

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyService {
	private OntModel model = null;
	private SecureRandom random;
    /**
     * create Database object
     */
    public OntologyService(String modelName) {
    	
		model = createModel();
		model.read(modelName, "RDF/XML");
		random = new SecureRandom();
    }

    public static OntModel createModel() {

    	//Jena needs to create a model from the ontology instance every time we are using it
		OntModel model = ModelFactory.createOntologyModel();

        return model;
    }

    //The following queries are used for our example case. Users interested in adding other information
    //need to implement their own queries.
    
    public ResultSet getTitles() {
        
    	//This SPARQL query gives the titles of all the documents in the VIVO instance
    	
        String SPARQLquery = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX  bibo: <http://purl.org/ontology/bibo/>\n" +
                "PREFIX  vivo: <http://vivoweb.org/ontology/core#>\n" +
                "\n" +
                "SELECT ?title\n" +
                "WHERE \n" +
                "{ ?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> bibo:Document .\n" +
                " ?resource vivo:title ?title" +
                "  }\n" +
                "";
		Query query = QueryFactory.create(SPARQLquery);
		QueryExecution qExe = QueryExecutionFactory.create(query, model);
		ResultSet resultsRes = qExe.execSelect();
		
		return resultsRes;
    }

    public boolean existsDocument(String reference) {
		
    	//As some of the references stored in the database will exist in the ontology.
    	//We need to test if they exist in order to create it in the ontology or not.
    	
		boolean exists = false;
		
		String SPARQLquery = "PREFIX vivo: <http://vivoweb.org/ontology/core#>\n" +
			"SELECT ?subject\n" +
			"WHERE {?subject vivo:title ?title .\n" +
			"FILTER (REGEX(STR(?title), \""+reference+"\", \"i\"))}";

		Query query = QueryFactory.create(SPARQLquery);
		QueryExecution qExe = QueryExecutionFactory.create(query, model);
		ResultSet resultsRes = qExe.execSelect();
		exists= resultsRes.hasNext();

		System.out.println("Result exists: "+exists);
		return exists;
    }		
	

	public Individual retrieveIndividual(String title) {
		
		//We need to obtain the individual of each title to generate new information
		String NS = "http://vivo.iu.edu/individual/";
		String indStr = "";
		Individual ind = null;
		
		String SPARQLquery = "PREFIX vivo: <http://vivoweb.org/ontology/core#>\n" +
				"SELECT ?subject\n" +
				"WHERE {?subject vivo:title ?title .\n" +
				"FILTER (REGEX(STR(?title), \""+title+"\", \"i\"))}";;

		Query query = QueryFactory.create(SPARQLquery);
		QueryExecution qExe = QueryExecutionFactory.create(query, model);
		ResultSet resultsRes = qExe.execSelect();

        if (!resultsRes.hasNext())
            System.out.println("Result retrieve. There are no instances of " + title);
        else{
        	indStr = resultsRes.next().get("subject").toString();
    		System.out.println("Result retriv. The instance of "+indStr+" exists.");
    		ind = model.getIndividual(indStr);
        }
		
		return ind;
	}
	
	public void addData(String document, String reference) throws IOException {

		//We are adding references to a paper. We are using the ObjectProperty cites
		String NSdocument = "http://purl.org/ontology/bibo/Document";
		String NStitle = "http://vivoweb.org/ontology/core#title";
		String NS = "http://vivo.iu.edu/individual/";

		Individual ref;
		System.out.println("Paper: "+document+" Reference: "+reference);
		
		//If a reference of a paper does not exist in the ontology, we have to create it
		if (!existsDocument(reference)){
			
			//We are creating a random Id to store the new reference in the ontology as an individual
			String rndStr = nextSessionId();
			
			OntClass docu = model.getOntClass( NSdocument );
			ref = model.createIndividual( NS + "n" + rndStr, docu );
			AnnotationProperty title = model.createAnnotationProperty(NStitle);
			ref.addProperty(title, reference);
			System.out.println("Created individual: " + NS + "n" + rndStr);
		}else{
			//If the reference exists we need to obtain its URI
			ref = retrieveIndividual(reference);
		}
		
		//We need to obtain the URI of the paper we are adding references
		Individual doc = retrieveIndividual(document);
		
		//Once we have the URIs of the paper and the reference we are using the ObjectProperty cites to add the new information
		ObjectProperty cites = model.getObjectProperty("http://purl.org/ontology/bibo/cites");
		model.add(doc,cites,ref);

	}
	
	public boolean saveModel(String Out){
		//THe information added, need to be save
		try {
			model.write(new FileOutputStream(new File(Out)),"RDF/XML");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	

	private String nextSessionId()
	{
		//This function creates new ids for the individuals we are creating
		return new BigInteger(130, random).toString(32);
	}

}