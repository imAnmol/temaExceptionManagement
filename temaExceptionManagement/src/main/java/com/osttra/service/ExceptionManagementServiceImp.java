package com.osttra.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osttra.entity.SourceExceptionEntity;
import com.osttra.entity.TemaExceptionEntity;
import com.osttra.repository.sourceExceptionDatabase.SourceExceptionRepository;
import com.osttra.repository.temaDatabase.TemaExceptionRepository;

@Service
public class ExceptionManagementServiceImp implements ExceptionManagementService{
	
	String ipAddress = "10.196.22.55";

	@Autowired
	private SourceExceptionRepository sourceExceptionRepository;

 

	@Autowired
	private TemaExceptionRepository temaExceptionRepository;

 

	@Autowired
	RestTemplate restTemplate;

 

	public List<SourceExceptionEntity> getAllFromSource() {

 

		return sourceExceptionRepository.findAll();
	}
	
	public void deleteAllItemsSource() {
		sourceExceptionRepository.deleteAll();
    }
	
	public void deleteAllItemsTema() {
		temaExceptionRepository.deleteAll();
    }

 

//	public List<TemaMongoEntity> getAllFromTema() {
//
//		return temaMongoRepository.findAll();
//	}

 

	public SourceExceptionEntity addExceptionInSource(SourceExceptionEntity sourceData) {
		return sourceExceptionRepository.save(sourceData);
	}
//    
//    public void migrateData() {
//        List<SourceMongoEntity> dataToMigrate = sourceMongoRepository.findAll();
//        for (SourceMongoEntity mongoData : dataToMigrate) {
//        	 if (!temaMongoRepository.existsById(mongoData.getExceptionId())) {
//        temaMongoRepository.save(mongoData);
//        }
//        }
//    }
//    
////    public void migrateData() {
////        List<SourceMongoEntity> sourceData = sourceMongoRepository.findAll();
////        List<TemaMongoEntity> destinationData = new ArrayList<>();
////
////        for (SourceMongoEntity sourceEntity : sourceData) {
////            // Check if a document with the same exceptionId exists in the destination database
////           
////
////          //  if (!temaMongoRepository.existsById(sourceEntity.getExceptionId())) {
////                // Document with the same exceptionId doesn't exist in destination, so migrate it
////                TemaMongoEntity destinationEntity = new TemaMongoEntity();
////               // destinationEntity.setAssign("ASSIGN");
////                // Copy fields from sourceEntity to destinationEntity
////                // ...
////                // Save destinationEntity to the destination database
////                destinationData.add(destinationEntity);
////              
////          //  }
////        }
////        temaMongoRepository.saveAll(destinationData);
////    
////    }

 

//*********************old migration code***************************************8	
//	public void migrateData() {
//		List<SourceMongoEntity> sourceData = sourceMongoRepository.findAll();
//
//		for (SourceMongoEntity sourceEntity : sourceData) {
//			// Check if a document with the same exceptionId exists in the destination
//			// database
//			TemaMongoEntity existingEntity = temaMongoRepository.findByExceptionId(sourceEntity.getExceptionId());
//
//			if (existingEntity == null) {
//
//				TemaMongoEntity destinationEntity = new TemaMongoEntity();
//
//				destinationEntity.setExceptionId(sourceEntity.getExceptionId());
//				destinationEntity.setTradeId(sourceEntity.getTradeId());
//				destinationEntity.setCounterParty(sourceEntity.getCounterParty());
//				destinationEntity.setTradeDate(sourceEntity.getTradeDate());
//				destinationEntity.setExceptionType(sourceEntity.getExceptionType());
//				destinationEntity.setResolutionSteps(sourceEntity.getResolutionSteps());
//				destinationEntity.setStatus(sourceEntity.getStatus());
//				destinationEntity.setPriority(sourceEntity.getPriority());
//				destinationEntity.setDescription(sourceEntity.getDescription());
//				destinationEntity.setCreatedBy(sourceEntity.getCreatedBy());
//				destinationEntity.setCreatedAt(sourceEntity.getCreatedAt());
//				destinationEntity.setUpdatedBy(sourceEntity.getUpdatedBy());
//				destinationEntity.setUpdatedAt(sourceEntity.getUpdatedAt());
//				destinationEntity.setAssign("ASSIGN"); 
//
//			    String exceptionId = destinationEntity.getExceptionId();
//			    
////				String externalApiUrl = "https://example.com/api/products?exceptionId=" + exceptionId;
////				ResponseEntity<String> response = restTemplate.exchange(externalApiUrl, HttpMethod.GET, null, String.class);
//			    
//			    Map<String, String> mapExceptionId = new HashMap<>();
//			    mapExceptionId.put("exceptionId", exceptionId);
//			    
//			    String ExceptionIdJson = maptoJson(mapExceptionId);
//			    
//			    String externalApiUrl = "https://example.com/api/products";
//			    
//				HttpHeaders headers = new org.springframework.http.HttpHeaders();
//				headers.setContentType(MediaType.APPLICATION_JSON);
//				HttpEntity<String> requestEntity = new HttpEntity<>(ExceptionIdJson, headers);
//				ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, requestEntity, String.class);
//				
//				String businessId = extractProductIdFromResponse(response.getBody());
//				destinationEntity.setBusinessId(businessId); 				
//				temaMongoRepository.save(destinationEntity);
//			}
//		}
//	}

 

	// new migration method

 

	public void migrateData() {
		List<SourceExceptionEntity> sourceData = sourceExceptionRepository.findAll();
		System.out.println(sourceData);

 

		for (SourceExceptionEntity sourceEntity : sourceData) {
			System.out.println(sourceEntity);

 

			System.out.println(sourceEntity.getExceptionId());

 

			// TemaMongoEntity existingEntity =
			// temaMongoRepository.findById(sourceEntity.getExceptionId()).get();

 

			Optional<TemaExceptionEntity> existingEntityOptional = temaExceptionRepository
					.findById(sourceEntity.getExceptionId());
			if (!existingEntityOptional.isPresent()) {
				TemaExceptionEntity temaExceptionEntity = createTemaExceptionEntity(sourceEntity);
				String exceptionId = temaExceptionEntity.getExceptionId();
				String exceptionType = temaExceptionEntity.getExceptionType();
				System.out.println(exceptionId + " " + exceptionType);
				String processId = fetchProcessId(exceptionId, exceptionType);
				System.out.println(processId);
				temaExceptionEntity.setProcessId(processId);
				temaExceptionRepository.save(temaExceptionEntity);
			} else {
				System.out.println(" data already present");
			}

 

//			if (existingEntity == null) {
//				
//				TemaMongoEntity temaExceptionEntity = createTemaExceptionEntity(sourceEntity);
//				String exceptionId  = temaExceptionEntity.getExceptionId();
//				String exceptionType = temaExceptionEntity.getExceptionType();
//				System.out.println(exceptionId +" "+ exceptionType);
//				String processId = fetchProcessId(exceptionId, exceptionType);
//				temaExceptionEntity.setProcessId(processId);
//				temaMongoRepository.save(temaExceptionEntity);
//			}
		}
	}

 

	private TemaExceptionEntity createTemaExceptionEntity(SourceExceptionEntity sourceEntity) {
		TemaExceptionEntity temaExceptionEntity = new TemaExceptionEntity();
		temaExceptionEntity.setExceptionId(sourceEntity.getExceptionId());
		temaExceptionEntity.setTradeId(sourceEntity.getTradeId());
		temaExceptionEntity.setCounterParty(sourceEntity.getCounterParty());
		temaExceptionEntity.setTradeDate(sourceEntity.getTradeDate());
		temaExceptionEntity.setExceptionType(sourceEntity.getExceptionType());
		temaExceptionEntity.setResolutionSteps(sourceEntity.getResolutionSteps());
		temaExceptionEntity.setStatus(sourceEntity.getStatus());
		temaExceptionEntity.setPriority(sourceEntity.getPriority());
		temaExceptionEntity.setDescription(sourceEntity.getDescription());
		temaExceptionEntity.setCreatedBy(sourceEntity.getCreatedBy());
		temaExceptionEntity.setCreatedAt(sourceEntity.getCreatedAt());
		temaExceptionEntity.setUpdatedBy(sourceEntity.getUpdatedBy());
		temaExceptionEntity.setUpdatedAt(sourceEntity.getUpdatedAt());
		temaExceptionEntity.setAssign("ASSIGN");
		temaExceptionEntity.setResolutionCount("0");
		return temaExceptionEntity;
	}

 

	public String fetchProcessId(String exceptionId, String exceptionType) {

 

		// Map<String, String> mapExceptionId = Collections.singletonMap("businessKey",
		// exceptionId);

 

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("businessKey", exceptionId);
		Map<String, Object> variablesMap = new HashMap<>();
		Map<String, Object> exceptionTypeMap = new HashMap<>();
		exceptionTypeMap.put("value", exceptionType);
		exceptionTypeMap.put("type", "String");
		variablesMap.put("ExceptionType", exceptionTypeMap);
		requestMap.put("variables", variablesMap);
		// Log the JSON request data
		System.out.println("JSON Request: " + requestMap);

 

		ObjectMapper objectMapper = new ObjectMapper();
		String exceptionIdJson = null;
		try {
			exceptionIdJson = objectMapper.writeValueAsString(requestMap);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 

		String externalApiUrl = "http://" + ipAddress
				+ ":8080/engine-rest/process-definition/key/ApprovalProcess/start";
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> requestEntity = new HttpEntity<>(exceptionIdJson, headers);
		ResponseEntity<String> response = postJsonToExternalApi(externalApiUrl, exceptionIdJson);
		return extractProcessssIdFromResponse(response.getBody());
	}

 

	public ResponseEntity<String> postJsonToExternalApi(String externalApiUrl, String requestBodyJson) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
		return restTemplate.postForEntity(externalApiUrl, requestEntity, String.class);
	}

 

// new migration code ends here	

 

	public String extractProcessssIdFromResponse(String responseBody) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonResponse;
		try {
			jsonResponse = objectMapper.readTree(responseBody);
		} catch (Exception e) {
			// Handle JSON parsing exception
			e.printStackTrace();
			return null;
		}
		String processId = jsonResponse.get("id").asText();
		return processId;
	}

 

	public String mapToJson(Map<String, String> map) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting map to JSON", e);
		}
	}

 

	public String stringtoJson(String data) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

 

//	public Optional<TemaMongoEntity> getExceptionDetails(String exceptionId) {
//		try {
//			return temaMongoRepository.findById(exceptionId);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Optional.empty();
//		}
//	}

 

	public TemaExceptionEntity getExceptionDetails(String exceptionId) {
		System.out.println("inside service " + exceptionId);
		Optional<TemaExceptionEntity> exceptionOptional = temaExceptionRepository.findById(exceptionId);

 

		TemaExceptionEntity exception = null;

 

		if (exceptionOptional.isPresent()) {
			exception = exceptionOptional.get();
		}

 

		System.out.println(exception);
		updateExceptionStatus(exception);
		return exception;
	}

 

	public List<TemaExceptionEntity> showAllException() {
		List<TemaExceptionEntity> exceptions = temaExceptionRepository.findAll();
		for (TemaExceptionEntity exception : exceptions) {
			updateExceptionStatus(exception);
		}
		return exceptions;
	}

 

	public void updateExceptionStatus(TemaExceptionEntity exception) {
		String processId = exception.getProcessId();
		String externalApiUrl = "http://" + ipAddress + ":8080/engine-rest/history/activity-instance?processInstanceId="
				+ processId + "&sortBy=startTime&sortOrder=desc";
		ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
//		Map<String, String> mapProcessId = Collections.singletonMap("processId", processId);
//		String ProcessIdJson = mapToJson(mapProcessId);
//		String externalApiUrl = "https://example.com/api/products";
//		ResponseEntity<String> response = postJsonToExternalApi(externalApiUrl, ProcessIdJson);
		String status = getExceptionStatus(response);
		exception.setStatus(status);
		System.out.println(exception.getStatus());
		if (!(exception.getStatus().equals("Open")))
			exception.setAssign("Assigned");
		System.out.println(exception.getAssign());
		temaExceptionRepository.save(exception);
	}

 

	public String getExceptionStatus(ResponseEntity<String> response) {
		String status = "Open";
		try {
			String responseBody = response.getBody();
//			if (responseBody != null) {
//				System.out.println(responseBody);
//				ObjectMapper objectMapper = new ObjectMapper();
//				JsonNode rootNode = objectMapper.readTree(responseBody);
//				
//				JsonNode assigneeNode = rootNode.get("assignee");
//				JsonNode activityNameNode = rootNode.get("activityName");
//				String activity = activityNameNode.asText();
//
//				if (assigneeNode != null) {
//					if (activity == "4-Eye check")
//						status = "Resolved";
//					else if (activity == "Complete")
//						status = "Closed";
//					else
//						status = "Pending";
//				}
//			}

 

			if (responseBody != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonArray = objectMapper.readTree(responseBody);
System.out.println(jsonArray);
				if (jsonArray.isArray() && jsonArray.size() > 0) {
					JsonNode firstObject = jsonArray.get(0);
					JsonNode activityNameNode = firstObject.get("activityName");
					JsonNode activityTypeNode = firstObject.get("activityType");
					JsonNode assigneeNode = firstObject.get("assignee");
					String activity = activityNameNode.asText();
					String activityType = activityTypeNode.asText();
					System.out.println("----------------- inside check Status---------------------------");
                    System.out.println("activity" + activity);                           
					//if (assigneeNode != null) {
						if (activity.equals("4-Eye check"))
							status = "Resolved";
						else if (activityType.equals("noneEndEvent"))
							status = "Closed";
						else if (activity.equals("Escalation") )
							status = "Pending";
						else if (activity.equals("Perform Task") || assigneeNode != null )
							status = "Open";
					}
				}
			//}

 

		} catch (Exception e) {
			System.out.println("inside getStatus of service");
			e.printStackTrace();
		}
		System.out.println(status);
		return status;
	}

 

	public String getProcessId(String exceptionId) {
		Optional<TemaExceptionEntity> exceptionOptional = temaExceptionRepository.findById(exceptionId);

 

		if (exceptionOptional.isPresent()) {
			TemaExceptionEntity exception = exceptionOptional.get();
			return exception.getProcessId();
		} else {
			// Handle the case where the exception with the given ID is not found
			// You might want to return a default value or throw an exception here
			return null; // or throw an exception
		}
	}

 

	public List<Map<String, Object>> getExceptionHistory(String exceptionId) {
		Optional<TemaExceptionEntity> exceptionOptional = temaExceptionRepository.findById(exceptionId);

 

		TemaExceptionEntity exception = null;

 

		if (exceptionOptional.isPresent()) {
			exception = exceptionOptional.get();
			System.out.println("--------------------------history----------------------");
			System.out.println(exception);
		}
		String processId = exception.getProcessId();
		String externalApiUrl = "http://" + ipAddress + ":8080/engine-rest/history/task?processInstanceId=" + processId +"&sortBy=startTime&sortOrder=asc";
		ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
		String responseBody = response.getBody();

 

		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> historyList = new ArrayList<>();

 

		try {
			JsonNode jsonArray = objectMapper.readTree(responseBody);
			System.out.println("-------------------------------------history----------------------------------------");
			System.out.println(jsonArray);

 

			if (jsonArray.isArray()) {
				System.out.println(" ---------------its working------------------");
				for (JsonNode jsonObject : jsonArray) {
					System.out.println(" ---------------its working------------------");
					// Extract the required attributes
					String taskName = jsonObject.get("name").asText();
					String taskId = jsonObject.get("id").asText();
					System.out.println(taskName);
					// String user = jsonObject.get("assignee").asText();
					String startTime = jsonObject.get("startTime").asText();
					String endTime = jsonObject.get("endTime").asText();
					// Create a map for the extracted object
					Map<String, Object> outerMap = new HashMap<>();
					outerMap.put("taskName", taskName);
					outerMap.put("startTime", startTime);
					Map<String, Object> extractedObject = getEcxeptionHistoryForUserGroup(processId, taskId);
                    outerMap.put("groupId", extractedObject.get("groupId"));
                    outerMap.put("groupTime", extractedObject.get("groupTime"));
                    outerMap.put("userId", extractedObject.get("userId"));
                    outerMap.put("userTime", extractedObject.get("userTime"));
                    outerMap.put("endTime", endTime);
					// outerMap.put("extractedObject", extractedObject);
					historyList.add(outerMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return historyList;

 

	}

 

	private Map<String, Object> getEcxeptionHistoryForUserGroup(String processId, String taskId) {
		  Map<String, Object> resultMap = new HashMap<>();
		String externalApiUrl = "http://" + ipAddress
				+ ":8080/engine-rest/history/identity-link-log?rootProcessInstanceId=" + processId + "&taskId=" + taskId
				+ "&operationType=add&type=candidate";
		ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
		String responseBody = response.getBody();
		System.out.println(" hi i am here heeehehehehe");
		System.out.println(responseBody);

 

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonArray = objectMapper.readTree(responseBody);

 

			if (jsonArray.isArray() && jsonArray.size() > 0) {
				JsonNode lastObject = jsonArray.get(jsonArray.size() - 1);

 

				String groupTime = lastObject.get("time").asText();
				String groupId = lastObject.get("groupId").asText();

 

				System.out.println("Time: " + groupTime);
				System.out.println("GroupId: " + groupId);
				   resultMap.put("groupTime", groupTime);
		           resultMap.put("groupId", groupId);

 

			} else {
				System.out.println("The JSON array is empty.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String externalApiUrl2 = "http://" + ipAddress
				+ ":8080/engine-rest/history/identity-link-log?rootProcessInstanceId=" + processId + "&taskId=" + taskId
				+ "&operationType=add&type=assignee";
		ResponseEntity<String> response2 = restTemplate.getForEntity(externalApiUrl2, String.class);
		String responseBody2 = response2.getBody();
		System.out.println(" hi i am here heeehehehehe");
		System.out.println(responseBody2);

 

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonArray = objectMapper.readTree(responseBody2);

 

			if (jsonArray.isArray() && jsonArray.size() > 0) {
				JsonNode lastObject = jsonArray.get(jsonArray.size() - 1);

 

				String userTime = lastObject.get("time").asText();
				String userId = lastObject.get("userId").asText();

 

				System.out.println("User Time: " + userTime);
				System.out.println("User Id: " + userId);
		           resultMap.put("userTime", userTime);
		           resultMap.put("userId", userId);
		//

 

			} else {
				System.out.println("The JSON array is empty.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



 

 

//		String externalApiUrl2 = "http://" + ipAddress
//				+ ":8080/engine-rest/history/identity-link-log?rootProcessInstanceId=" + processId + "&taskId=" + taskId
//				+ "&operationType=add&type=assignee";
//		System.out.println(externalApiUrl2);
//		System.out.println(" hi i am here heeehehehehe");
//		ResponseEntity<String> response2 = restTemplate.getForEntity(externalApiUrl2, String.class);
//		String responseBody2 = response2.getBody();
//		System.out.println(" hi i am here heeehehehehe");
//		System.out.println(responseBody2);
		return resultMap;
	}

 

	public String fetchTaskId(String processId) {
		String externalApiUrl = "http://" + ipAddress + ":8080/engine-rest/history/activity-instance?processInstanceId="
				+ processId + "&sortBy=startTime&sortOrder=desc";
		ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
		String responseBody = response.getBody();
		String taskId = null;
		if (responseBody != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				JsonNode jsonArray = objectMapper.readTree(responseBody);
				if (jsonArray.isArray() && jsonArray.size() > 0) {
					JsonNode firstObject = jsonArray.get(0);
					JsonNode taskIdNode = firstObject.get("taskId");
					taskId = taskIdNode.asText();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return taskId;
	}

 

	public void updateResolutionCount(String exceptionId, String resolutionCount) {
		TemaExceptionEntity entity = temaExceptionRepository.findById(exceptionId).orElse(null);
		if (entity != null) {
			entity.setResolutionCount(resolutionCount);
			temaExceptionRepository.save(entity);
		}
	}

 

	public ResponseEntity<String> deleteOtherUser(String taskId) {
		try {
			String externalApiUrl = "http://" + ipAddress + ":8080/engine-rest/task/" + taskId
					+ "/identity-links/delete";
			String jsonString = "{ \"type\":\"candidate\",\"groupId\":\"other\"}";
			System.out.println(jsonString);
			ResponseEntity<String> response = postJsonToExternalApi(externalApiUrl, jsonString);

 

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Exception sent from group to escalation !!!");
			} else {
				System.out.println("Inside else of controller of group to 4 eye check");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
	}

 

}