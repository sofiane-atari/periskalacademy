package periskal.academy.backend.service.Authentication;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import periskal.academy.backend.model.AuthenticateResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAuthenticationService {

    protected final static RestTemplate restTemplate = new RestTemplate();
    protected final static String authenticateEndpointUrl = "https://academyws.periskal.com/Academy.asmx";

   
    public AuthenticateResponse authenticate(String token) {
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
            String responseXml = soapResponseEntity.getBody();
            return parseSoapResponse(responseXml);
        }
        return null;
    }

    
    //Child classes need to implement how to build the SOAP request.
     
    protected abstract String buildSoapRequest(String token);

   
    protected AuthenticateResponse parseSoapResponse(String responseXml) {
        AuthenticateResponse authResponse = new AuthenticateResponse();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));
    
            authResponse.setId(getElementValue(doc, "ID"));
            authResponse.setUsername(getElementValue(doc, "Username"));
            authResponse.setFirstname(getElementValue(doc, "Firstname"));
            authResponse.setLastname(getElementValue(doc, "Lastname"));
            authResponse.setShipname(getElementValue(doc, "Shipname"));
            authResponse.setRole(getElementValue(doc, "Role"));
            authResponse.setLoginResult(getElementValue(doc, "LoginResult"));
    
            // Extract products as a list of strings
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
        }
        return authResponse;
    }

    
     //Helper method to extract XML element text content.
     
    protected String getElementValue(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}
