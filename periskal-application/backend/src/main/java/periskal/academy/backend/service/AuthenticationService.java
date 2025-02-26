package periskal.academy.backend.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.util.EncryptionUtil;

@Service
public class AuthenticationService {

   private final RestTemplate restTemplate = new RestTemplate();
   private final String authenticateEndpointUrl = "https://academyws.periskal.com/Academy.asmx";

    public AuthenticateResponse authenticate(String username, String password) throws Exception {
    
    String token = EncryptionUtil.encode(username, password);
    
    String soapRequest = buildSoapRequest(token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));


     HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

     ResponseEntity<String> soapResponseEntity;
        try {
            soapResponseEntity = restTemplate.postForEntity(authenticateEndpointUrl, requestEntity, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
   }

    if (soapResponseEntity.getStatusCode() == HttpStatus.OK) {
            // 5. Parse the SOAP response XML
            String responseXml = soapResponseEntity.getBody();
            AuthenticateResponse authResponse = parseSoapResponse(responseXml);
            return authResponse;
        }
        return null;

    }

    private String buildSoapRequest(String token) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
           "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
           "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
           "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
           "  <soap12:Body>\n" +
           "    <Authenticate xmlns=\"http://tempuri.org/\">\n" +
           "      <Token>" + token + "</Token>\n" +
           "    </Authenticate>\n" +
           "  </soap12:Body>\n" +
           "</soap12:Envelope>";
}

    private AuthenticateResponse parseSoapResponse(String responseXml) {
        AuthenticateResponse authResponse = new AuthenticateResponse();
    try {
        // Initialize an XML document parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

        // Extract required values using getElementsByTagName
        authResponse.setId(getElementValue(doc, "ID"));
        authResponse.setUsername(getElementValue(doc, "Username"));
        authResponse.setFirstname(getElementValue(doc, "Firstname"));
        authResponse.setLastname(getElementValue(doc, "Lastname"));
        authResponse.setShipname(getElementValue(doc, "Shipname"));
        authResponse.setRole(getElementValue(doc, "Role"));
        authResponse.setLoginResult(getElementValue(doc, "LoginResult"));

        // Extract products as a list
        NodeList productNodes = doc.getElementsByTagName("Products");
        List<String> products = new ArrayList<>();
        if (productNodes.getLength() > 0) {
            Node productNode = productNodes.item(0);
            NodeList productList = productNode.getChildNodes();
            for (int i = 0; i < productList.getLength(); i++) {
                Node item = productList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    products.add(item.getTextContent());
                }
            }
        }
        authResponse.setProducts(products);

    } catch (Exception e) {
        e.printStackTrace();
        // Handle parsing exceptions
    }
    return authResponse;
    }

    private String getElementValue(Document doc, String tagName) {
    NodeList nodes = doc.getElementsByTagName(tagName);
    if (nodes.getLength() > 0) {
        return nodes.item(0).getTextContent();
    }
    return null;
    }


}



