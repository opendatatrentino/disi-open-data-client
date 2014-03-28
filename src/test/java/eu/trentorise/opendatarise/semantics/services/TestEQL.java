//package eu.trentorise.opendatarise.semantics.services;
//
//import it.unitn.disi.sweb.webapi.client.IProtocolClient;
//import it.unitn.disi.sweb.webapi.client.ProtocolFactory;
//import java.util.Locale;
//import org.junit.Test;
//
///**
// *
// * @author Moaz Reyad <reyad@disi.unitn.it>
// */
//public class TestEQL {
//
//    private static final String SERVER = "www.<testserver>.com";
//    private static final int PORT = 8080;
//
//    /**
//     * Prints the IDs of the location entities. The number of displayed entities
//     * are limited by the client. (e.g. only 10 locations)
//     */
//    @Test
//    public void testFromEntityType() {
//        IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, SERVER, PORT);
//        Search searchService = new Search(api);
//
//        String[][] results = searchService.searchEQL("from location location1");
//
//        for (String[] row : results) {
//            for (String cell : row) {
//                System.out.print(cell);
//                System.out.print(",");
//            }
//
//            System.out.println();
//        }
//    }
//
//    /**
//     * Prints the IDs of the location entities which have "administrative
//     * division" in the description. The number of displayed entities are
//     * limited by the client. (e.g. only 10 locations)
//     */
//    @Test
//    public void testDescriptionValue() {
//        IProtocolClient api = ProtocolFactory.getHttpClient(Locale.ENGLISH, "opendata.disi.unitn.it", 8080);
//        Search searchService = new Search(api);
//
//        String[][] results = searchService.searchEQL("from locations[description:administrative division] location1");
//
//        for (String[] row : results) {
//            for (String cell : row) {
//                System.out.print(cell);
//                System.out.print(",");
//            }
//
//            System.out.println();
//        }
//    }
//}
