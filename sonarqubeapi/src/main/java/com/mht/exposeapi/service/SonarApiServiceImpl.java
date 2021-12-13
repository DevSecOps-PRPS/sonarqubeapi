package com.mht.exposeapi.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.mht.exposeapi.dto.Measure;
import com.mht.exposeapi.dto.MeasureHistory;
import com.mht.exposeapi.dto.MeasureHistoryResponse;
import com.mht.exposeapi.dto.Range;

// import io.jsonwebtoken.Jwts;  
// import io.jsonwebtoken.SignatureAlgorithm;  
  
// byte[] key = getSignatureKey();  
  
// String jwt = Jwts.builder().setIssuer("http://trustyapp.com/")  
//     .setSubject("users/1300819380")  
//     .setExpiration(expirationDate)  
//     .put("scope", "self api/buy")  
//     .signWith(SignatureAlgorithm.HS256,key)  
//     .compact();

@Service()
public class SonarApiServiceImpl implements SonarApiService{
	@Override
	public List<List<Object>> getDataPoint(Range range, String metrics) {
		// String accessToken = "3c29f93c18fdf1b2713302f93b15078ca1c7a77e:";
		String accessToken = "c6822504a1813787e4a87f47b1238645fd6ec5be:";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(accessToken.getBytes())));
		HttpEntity entity = new HttpEntity(headers);
		List<List<Object>> datapoints = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		// String historyMetrics = "http://localhost:8080/api/measures/search_history";
		// String historyMetrics = "http://10.101.0.204:9000/api/measures/search_history?metrics=code_smells,bugs,vulnerabilities,security_hotspots";
		String historyMetrics = "http://10.101.0.204:9000/api/measures/search_history";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(historyMetrics)
			    // .fromUriString(historyMetrics)
			    // Add query parameter
			    //.queryParam("component", "com.infy.sonar:sonar-spring-demo")
				.queryParam("component", "PRPS")
				// .queryParam("token",accessToken)
				.queryParam("metrics", metrics);
		
				ResponseEntity<MeasureHistoryResponse> measureHistoryResponse = restTemplate.exchange(builder.toUriString(),HttpMethod.GET, entity, MeasureHistoryResponse.class);
				System.out.println("sonar response - "+measureHistoryResponse.getBody());
				MeasureHistoryResponse sonar_response = measureHistoryResponse.getBody();
				for(Measure measure: sonar_response.getMeasures()) {
					for(MeasureHistory measureHistory: measure.getHistory()) {
						ZonedDateTime zonedDateTime = measureHistory.getDate();
						System.out.println("zoned time - "+zonedDateTime);
						Long epochSecond = zonedDateTime.toEpochSecond();
						LocalDateTime historyTime = zonedDateTime.toLocalDateTime();
						LocalDateTime rangeFrom = range.getFrom();
						LocalDateTime rangeTo = range.getTo();
						System.out.println("history - "+historyTime);
						System.out.println("from - "+rangeFrom);
						System.out.println("to - "+rangeTo);
						System.out.println("epoch sec - "+epochSecond);
						if(historyTime.isBefore(range.getFrom()) || historyTime.isAfter(range.getTo()))
							;//continue;
						List<Object> datapoint = Arrays.asList(measureHistory.getValue(),epochSecond*1000);
						datapoints.add(datapoint);
					}
				}
				System.out.println(datapoints.size());
				return datapoints;
	}

}


					// HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
					// System.out.println(response.getBody());