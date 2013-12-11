import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;

/*********************************************************************
 *                           VIVOexport                              *
 * @author Alberto Nogales (alberto.nogales@uah.es)                  *
 *                                                                   *
 *A tool to add new data to a VIVO instance. For example, in this    *
 *case it adds references to a paper.                                *
 *Developed in Alcal‡ University (Spain) by IERU                     *
 *research group (http://www.ieru.org/).                             * 
 *Any incidence please submit to the email below.                    *
 *********************************************************************/


public class Component{
	public static void main(String[] args) throws SQLException, IOException {
		
		System.out.println("Obtaining titles from VIVO instance");
		
		//Configure the path for a VIVO instance stored in your local machine
		//It corresponds to the VIVO instance where you are adding information
		OntologyService ontService = new OntologyService("../VIVOInstance.owl");
		
		//Titles from the VIVO instance are obtained, so we can add information to them.
		com.hp.hpl.jena.query.ResultSet titles = ontService.getTitles();
		ArrayList<String> titleList = new ArrayList<String>();
		while (titles.hasNext()){
		    QuerySolution soln = titles.nextSolution();
		    titleList.add(soln.getLiteral("?title").toString());
		}
		
		for(String document : titleList) {
			    System.out.println("Title in ontology: "+document);
			    
			    //The information we are adding is be stored in a database. In that case 
			    //we are using the title of a papaer to obtain its referece.
			    
			    System.out.println("Obtaining data from database");
			    ResultSet data = DBService.getData(document);

				while (data.next()){
					
					//The information we are interested to add is the bibtex citation and the references
					String bibtex = data.getString("citation");
					System.out.println("bibtex: "+bibtex);
					String reference = data.getString("snippetTitle");
					System.out.println("reference: "+reference);
					
					ontService.addData(document, reference);
				}
		}
		
		//Configure the path for a new VIVO instance to be stored in your local machine
		//Once we have added the new information we need to save it in a new ontology instance
		ontService.saveModel("../NewVIVOInstance.owl");
		System.out.println("Demo finished");
	}
}