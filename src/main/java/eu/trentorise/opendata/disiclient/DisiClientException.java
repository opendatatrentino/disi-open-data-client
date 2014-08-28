package eu.trentorise.opendata.disiclient;

/**
 * @author DavidLeoni <david.leoni@unitn.it>
 */
public class DisiClientException extends RuntimeException {


        public DisiClientException(String s){
            super(s);
        }

        public DisiClientException(String s, Exception ex) {
            super(s, ex);
        }

        public DisiClientException(Exception ex) {
            super(ex);
        }

}
