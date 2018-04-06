package org.clearbyte.demo.jena.tutorial1;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pete
 */
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tutorial1 {

    final static Logger LOGGER = LoggerFactory.getLogger(Tutorial1.class);
    final static String MODEL_1_URI = "https://jena.apache.org/tutorials/sparql_data/vc-db-1.rdf";
    final static String MODEL_2_URI = "https://jena.apache.org/tutorials/sparql_data/vc-db-4.rdf";
    final static String RESOURCE_URI = "http://somewhere/RebeccaSmith/";

    public static void main(String args[]) {
        Tutorial1 obj = new Tutorial1();
        //obj.runTutorial1();
        //obj.runTutorial5Mod();
        //obj.runTutorial8Mod();
        //obj.runtTutorial9Mod();
        obj.runTutorial10Mod();
    }

    private void runTutorial3() {
        String personURI = "http://somewhere/JohnSmith";
        String givenName = "John";
        String familyName = "Smith";
        String fullName = givenName + " " + familyName;
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // create the resource and add propterties
        Resource johnSmith = model.createResource(personURI)
                .addProperty(VCARD.FN, fullName)
                .addProperty(VCARD.N, model.createResource()
                        .addProperty(VCARD.Given, givenName)
                        .addProperty(VCARD.Family, familyName));

        //model.write(System.out, "N-TRIPLE");
        // print out the predicate, subject and object of each statement
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            String node = subject.toString() + " " + predicate.toString() + " ";
            //LOGGER.debug(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                node += object.toString();
            } else {
                // object is a literal
                node += " \"" + object.toString() + "\"";
            }
            LOGGER.debug(node + " .");
        }

        LOGGER.debug("------------------------------");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        LOGGER.debug(new String(out.toByteArray()));
    }

    private void runTutorial5Mod() {

        // create an empty model
        Model model = ModelFactory.createDefaultModel();

//        InputStream in = new URL(resourceURI).openStream();
//        if (in == null) {
//            LOGGER.debug( "Resource not found: " + resourceURI);
//            throw new IllegalArgumentException( "Resource not found: " + resourceURI);
//        }
        // read the RDF/XML file
        model.read(MODEL_1_URI);

        Resource vcard = model.getResource(RESOURCE_URI);
        if (vcard == null) {
            LOGGER.debug("Resource not found: " + RESOURCE_URI);
            return;
        }
        //String resourceFullname = vcard.getProperty(VCARD.FN).getString();
        LOGGER.debug("Resources full name: " + vcard.getProperty(VCARD.FN).getString());
        LOGGER.debug("Resource family name: " + vcard.getProperty(VCARD.N).getResource().getProperty(VCARD.Family).getString());

        //Add nickname property 
        vcard.addProperty(VCARD.NICKNAME, "Dicky");

        LOGGER.debug("------------------------------");
        //Write complet model to console
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        LOGGER.debug(new String(out.toByteArray()));
    }

    private void runTutorial8Mod() {
        String searchStr = "Smith";
        Model model = ModelFactory.createDefaultModel();
        model.read(MODEL_1_URI);

        StmtIterator iter = model.listStatements(
                new SimpleSelector(null, VCARD.FN, (RDFNode) null) {
            @Override
            public boolean selects(Statement s) {
                return s.getString().endsWith(searchStr);
            }
        });
        if (iter.hasNext()) {
            LOGGER.debug("The model contains vcards for:");
            while (iter.hasNext()) {
                LOGGER.debug(" " + iter.nextStatement().getString());
            }
        } else {
            LOGGER.debug("No {}'s found in the model", searchStr);
        }
    }

    private void runtTutorial9Mod() {
        Model model1 = ModelFactory.createDefaultModel();
        Model model2 = ModelFactory.createDefaultModel();

        model1.read(MODEL_1_URI);
        model2.read(MODEL_2_URI);

        Model unionModel = model1.union(model2);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        unionModel.write(out, "RDF/XML-ABBREV");
        LOGGER.debug(new String(out.toByteArray()));
    }

    private void runTutorial10Mod() {
        String searchStr = "Smith";
        Model model = ModelFactory.createDefaultModel();
        model.read(MODEL_1_URI);

        Bag searchBag = model.createBag();

        StmtIterator iter = model.listStatements(
                new SimpleSelector(null, VCARD.FN, (RDFNode) null) {
            @Override
            public boolean selects(Statement s) {
                return s.getString().endsWith(searchStr);
            }
        });
        if (iter.hasNext()) {
            LOGGER.debug("The model contains vcards for {}", searchStr);
            while (iter.hasNext()) {
                //LOGGER.debug(" " + iter.nextStatement().getString());
                searchBag.add(iter.nextStatement().getSubject());
            }
        } else {
            LOGGER.debug("No {}'s found in the model", searchStr);
        }
        // print out the members of the bag
        NodeIterator iter2 = searchBag.iterator();
        if (iter2.hasNext()) {
            LOGGER.debug("Search bag consits of following resource");
            while (iter2.hasNext()) {
                LOGGER.debug("  "
                        + ((Resource) iter2.next())
                                .getRequiredProperty(VCARD.FN)
                                .getString());
            }
        } else {
            System.out.println("The bag is empty");
        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        searchBag.getModel().write(out, "RDF/XML-ABBREV");
//        LOGGER.debug("Search bag consits of following objects");
//        LOGGER.debug(new String(out.toByteArray()));

    }
}
