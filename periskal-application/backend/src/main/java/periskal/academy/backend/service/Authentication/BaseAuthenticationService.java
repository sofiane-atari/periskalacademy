package periskal.academy.backend.service.Authentication;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.model.Client;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationCommunicationException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationInvalidCredentialsException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationParsingException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationServerErrorException;


@Slf4j
public abstract class BaseAuthenticationService {

    protected final static RestTemplate restTemplate = new RestTemplate();
    protected final static String AUTHENTICATE_ENDPOINT_URL = "https://academyws.periskal.com/Academy.asmx";

   
    public AuthenticateResponse authenticate(String token) {
        String soapRequest = buildSoapRequest(token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));
        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);
        
        ResponseEntity<String> soapResponseEntity;
        try {
            soapResponseEntity = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT_URL, requestEntity, String.class);
        } catch (RestClientException  ex) {
            String errorMsg = "SOAP authentication request failed: Communication error";
            log.error(errorMsg, ex);
            throw new AuthenticationCommunicationException(errorMsg, ex);
        }
        
        if (!soapResponseEntity.getStatusCode().is2xxSuccessful()) {
            String errorMsg = "SOAP authentication failed: Server returned non-successful status code";
            log.error("{} - Status: {}, Body: {}", errorMsg, soapResponseEntity.getStatusCode(), soapResponseEntity.getBody());
            throw new AuthenticationServerErrorException(errorMsg, soapResponseEntity.getStatusCode().value(), soapResponseEntity.getBody()
            );
        }


        String responseXml = soapResponseEntity.getBody();
        if (responseXml == null || responseXml.isBlank()) {
             throw new AuthenticationParsingException("Received empty response body from SOAP endpoint.", null, "");
        }
        return parseSoapResponse(responseXml);

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
            
            // Extract Skippers/Clients
            NodeList SkippersNodeList = doc.getElementsByTagName("Skippers");
            List<Client> skippersList = new ArrayList<>();
            if (SkippersNodeList.getLength() > 0) {
                NodeList clientNodes = SkippersNodeList.item(0).getChildNodes();
                for (int i = 0; i < clientNodes.getLength(); i++) { 
                    Node clientNode = clientNodes.item(i); 
                    if (clientNode.getNodeName().equals("Client")) { 
                        Client client = parseClientNode(clientNode); 
                        skippersList.add(client); 
                    }
                }   
            }
            authResponse.setClients(skippersList);


            if (authResponse.getLoginResult() == null || authResponse.getLoginResult().isBlank() || authResponse.getLoginResult().equals("Invalid Credentials")) {
                log.debug("Authentication attempt failed: Service returned empty LoginResult, likely invalid credentials.");
                throw new AuthenticationInvalidCredentialsException("Authentication failed: Invalid username or password.");
            }

    
        } 
        catch (ParserConfigurationException | SAXException | IOException e) {
            String errorMsg = "Failed to parse SOAP response XML";
            log.error(errorMsg + ". XML content: {}", responseXml, e); 
            throw new AuthenticationParsingException(errorMsg, e, responseXml);
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
