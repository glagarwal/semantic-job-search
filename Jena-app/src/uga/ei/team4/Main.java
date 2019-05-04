package uga.ei.team4;

import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;

import java.util.*;

public class Main {

    final static String PREFIX_JOB_OWL = "http://www.semanticweb.org/gaurav/ontologies/2019/4/job-ontology#";
    final static String RDF_FILE = "src/Job_Ontology.owl";
    static Model model;

    public static void main(String[] args) {
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        model = FileManager.get().loadModel(RDF_FILE);
        getSemantics("Software_Engineer", "Atlantic_City", "2+", "100000");
    }

    public static List<String> execSparql(String queryString, String findParam){
        List<String> relationsFound = new ArrayList<>();

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try{
            ResultSet results = qexec.execSelect();
            while (results.hasNext()){
                QuerySolution soln = results.nextSolution();
                RDFNode className = soln.get(findParam);
//                System.out.println(className.toString().replace(PREFIX_JOB_OWL, ""));
                relationsFound.add(className.toString().replace(PREFIX_JOB_OWL, ""));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            qexec.close();
            return relationsFound;
        }
    }

    public static String execSparql(String queryString){
        String relationFound = "";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try{
            ResultSet results = qexec.execSelect();
            while (results.hasNext()){
                QuerySolution soln = results.nextSolution();
                RDFNode className = soln.get("s");
//                System.out.println(className.toString().replace(PREFIX_JOB_OWL, ""));
                relationFound = className.toString().replace(PREFIX_JOB_OWL, "");
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            qexec.close();
            return relationFound;
        }
    }

    public static List<String> getNearLocations( String loc){
        List<String> locations = new ArrayList<>();
        locations.add(loc);
        String queryString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                        "PREFIX job-ontology: <"+ PREFIX_JOB_OWL +">" +
                        "SELECT * WHERE {" +
                        "job-ontology:"+loc + " job-ontology:isNearTo ?s ." +
                        "}";
        loc = execSparql(queryString);
        if(!locations.contains(loc)){
            locations.add(loc);
        }
        queryString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                        "PREFIX job-ontology: <"+ PREFIX_JOB_OWL +">" +
                        "SELECT DISTINCT * WHERE {" +
                        "?s job-ontology:isNearTo job-ontology:"+loc +
                        "}";
        locations.addAll(execSparql(queryString, "s"));
        Set<String> hset = new LinkedHashSet<>();
        hset.addAll(locations);
        locations.clear();
        locations.addAll(hset);

        return  locations;
    }

    public static void getSemantics(String pos, String loc, String exp, String sal){
//        model.write(System.out, "RDF/XML");
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();


        resultMap.put("location", getNearLocations(loc));
        System.out.println(resultMap);
    }
}

// "{ ?s job-ontology:isNearTo job-ontology:"+loc + " .}" +
//                        "UNION" +
