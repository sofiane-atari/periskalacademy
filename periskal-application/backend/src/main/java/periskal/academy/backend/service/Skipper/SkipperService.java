package periskal.academy.backend.service.Skipper;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.SkippersDTO;

@Slf4j
@Service
public class SkipperService {

    protected final static RestTemplate restTemplate = new RestTemplate();
    protected final static String AUTHENTICATE_ENDPOINT_URL = "https://academyws.periskal.com/Academy.asmx";
    
    public List<SkippersDTO> getSkippers(String fleetmanager)
    {

        String soapRequest = buildSoapRequest(fleetmanager);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/soap+xml; charset=utf-8"));
        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);


        ResponseEntity<String> soapResponseEntity;
        soapResponseEntity = restTemplate.postForEntity(AUTHENTICATE_ENDPOINT_URL, requestEntity, String.class);

        String responseXml = soapResponseEntity.getBody();
        return parseGetSkippersResponse(responseXml);


    }    


    protected String buildSoapRequest(String fleetmanager) 
    {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
               "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
               "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
               "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" + 
               "  <soap12:Body>\n" + 
               "    <GetSkippers xmlns=\"http://tempuri.org/\">\n" +
               "      <fleetmanager>" + fleetmanager + "</fleetmanager>\n" +
               "    </GetSkippers>\n" +
               "  </soap12:Body>\n" +
               "</soap12:Envelope>";
    }

    protected List<SkippersDTO> parseGetSkippersResponse(String responseXml) 
    {

        
        return Collections.emptyList();

    }


}
