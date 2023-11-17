package example.lambda;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class App implements RequestHandler<Map<String, String>, String> {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private class Cert {
        public String subject;
        public String issuer;
        public String publicKey;

        Cert(X509Certificate cert){
            this.subject = cert.getSubjectX500Principal().getName();
            this.issuer = cert.getIssuerX500Principal().getName();
            this.publicKey = cert.getPublicKey().toString();
        }
    }

    private List<Cert> getTrustCertificates() throws Exception {

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);

        List<TrustManager> trustManagers = Arrays.asList(trustManagerFactory.getTrustManagers());
        List<X509Certificate> certificates = trustManagers.stream()
            .filter(X509TrustManager.class::isInstance)
            .map(X509TrustManager.class::cast)
            .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        List<Cert> certs = new ArrayList<Cert>();
        for (X509Certificate cert : certificates) {
            certs.add(new Cert(cert));
        }

        return certs;
    }

    private String transelateToJson(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = objectMapper.writeValueAsString(obj);
        return json;
    }

    public String loadTrustCertificates() throws Exception {
        List<Cert> certs = getTrustCertificates();
        String jsonCerts = transelateToJson(certs);
        return jsonCerts;
    }

    public String handleRequest(final Map<String, String> input, final Context context) {
        
        String logGroupName = context.getLogGroupName();
        String logStreamName = context.getLogStreamName();
        
        try {
            String jsonCerts = loadTrustCertificates();
            log.info(jsonCerts);
            
            return String.format("{ \"statusCode\": \"200\", \"message\": \"Success!! Please check CloudWatch Logs %s - %s\"}", logGroupName, logStreamName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return String.format("{ \"statusCode\": \"500\", \"message\": \"Error!! Please check CloudWatch Logs %s - %s\"}", logGroupName, logStreamName);
        }
    }

    public static void main(String[] args){
        App app = new App();
        app.handleRequest(null, null);
    }
}