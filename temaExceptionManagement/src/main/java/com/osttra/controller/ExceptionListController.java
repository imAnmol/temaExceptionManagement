package com.osttra.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.osttra.entity.TemaExceptionEntity;
import com.osttra.repository.temaDatabase.TemaExceptionRepository;
import com.osttra.service.ExceptionManagementServiceImp;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class ExceptionListController {
	
	String ipAddress = "10.196.22.55";
	
	@Autowired
	ExceptionManagementServiceImp exceptionManagementServiceImp;

	@Autowired
	TemaExceptionRepository temaExceptionRepository;

 

	@Autowired
	RestTemplate restTemplate;


	@GetMapping("/getAllExceptions")
	public ResponseEntity<?> getAllExceptionList() {
		try {
			List<TemaExceptionEntity> mongoData = exceptionManagementServiceImp.showAllException();
			if (mongoData.size() > 0) {
				return new ResponseEntity<List<TemaExceptionEntity>>(mongoData, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Data not available", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace(); // You can log the exception or handle it as needed
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving data from Tema: " + e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteAllSource")
    public void deleteAllItemsSource() {
		exceptionManagementServiceImp.deleteAllItemsSource();
    }
	
	@DeleteMapping("/deleteAllTema")
    public void deleteAllItemsTema() {
		exceptionManagementServiceImp.deleteAllItemsTema();
    }
	
	
	@GetMapping("/getUserAssignedExceptions/{userId}")
	public ResponseEntity<?> getUserAssignedExceptions(@PathVariable  String userId){
		String externalApiUrl1 = "http://"+ ipAddress +":8080/engine-rest/task?assignee=" + userId + "&name=Perform Task";
		String externalApiUrl2 = "http://"+ ipAddress +":8080/engine-rest/task?assignee=" + userId + "&name=Escalation";
        JsonNode apiDataArray1 = restTemplate.getForObject(externalApiUrl1 , JsonNode.class);
 
        if (apiDataArray1 == null || !apiDataArray1.isArray()) {
            return ResponseEntity.notFound().build();
        }
//        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl1, String.class);
//        String responseBody = response.getBody();
//        System.out.println("API Response: " + responseBody);
        
        List<TemaExceptionEntity> matchingExceptions = new ArrayList<>();   
        for (JsonNode item : apiDataArray1) {
            if (item.has("processInstanceId")) {
                String processInstanceIdFromApi = item.get("processInstanceId").asText();
                TemaExceptionEntity matchingException = temaExceptionRepository.findByProcessId(processInstanceIdFromApi);
                
                if (matchingException != null) {
                	System.out.println(matchingException);
                	exceptionManagementServiceImp.updateExceptionStatus(matchingException);
                    matchingExceptions.add(matchingException);
                }
            }
        } 
        
        JsonNode apiDataArray2 = restTemplate.getForObject(externalApiUrl2 , JsonNode.class);
        
        if (apiDataArray2 == null || !apiDataArray2.isArray()) {
            return ResponseEntity.notFound().build();
        }
//        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl1, String.class);
//        String responseBody = response.getBody();
//        System.out.println("API Response: " + responseBody);
        
          
        for (JsonNode item : apiDataArray2) {
            if (item.has("processInstanceId")) {
                String processInstanceIdFromApi = item.get("processInstanceId").asText();
                TemaExceptionEntity matchingException = temaExceptionRepository.findByProcessId(processInstanceIdFromApi);
                
                if (matchingException != null) {
                	System.out.println(matchingException);
                	exceptionManagementServiceImp.updateExceptionStatus(matchingException);
                    matchingExceptions.add(matchingException);
                }
            }
        } 

        return ResponseEntity.ok(matchingExceptions);
    
    
}
	
	
	
	@GetMapping("/getUserAssignedFourEyeCheckUpExceptions/{userId}")
	public ResponseEntity<?> getUserAssignedFourEyeCheckUpExceptions(@PathVariable  String userId){
		String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task?assignee=" + userId + "&name=4-eye check";
		RestTemplate restTemplate = new RestTemplate();
        JsonNode apiDataArray = restTemplate.getForObject(externalApiUrl, JsonNode.class);

        if (apiDataArray == null || !apiDataArray.isArray()) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
        String responseBody = response.getBody();
        System.out.println("API Response: " + responseBody);
        
        List<TemaExceptionEntity> matchingExceptions = new ArrayList<>();

        
        for (JsonNode item : apiDataArray) {
            if (item.has("processInstanceId")) {
                String processInstanceIdFromApi = item.get("processInstanceId").asText();
                
                TemaExceptionEntity matchingException =   temaExceptionRepository.findByProcessId(processInstanceIdFromApi);
                
                if (matchingException != null) {
                	System.out.println(matchingException);
                	exceptionManagementServiceImp.updateExceptionStatus(matchingException);
                    matchingExceptions.add(matchingException);
                }
            }
        }
        return ResponseEntity.ok(matchingExceptions);   
}

	
	@GetMapping("/getUnclaimedFourEye/{userId}")
	public ResponseEntity<?> getUnclaimedFourEye(@PathVariable String userId) {
       
            
            String initialApiUrl = "http://" + ipAddress + ":8080/engine-rest/group?member=" + userId;
            JsonNode apiDataArray = restTemplate.getForObject(initialApiUrl, JsonNode.class);
            
            if (apiDataArray == null || !apiDataArray.isArray()) {
                return ResponseEntity.notFound().build();
            }


            else {
                
                List<TemaExceptionEntity> matchingExceptions = new ArrayList<>();
                
                for (JsonNode item : apiDataArray) {
                	
                
                    if (item.has("id")) {
                        String groupId = item.get("id").asText();
                        
                        String groupApiUrl = "http://" + ipAddress + ":8080/engine-rest/task?candidateGroup=" + groupId;
                        try {
                          JsonNode apiDataArray2 = restTemplate.getForObject(groupApiUrl, JsonNode.class);

                          for (JsonNode item2 : apiDataArray2) {
                              if (item2.has("processInstanceId") && (item2.get("name").asText()).equals("4-Eye check" )) {
                            	  
                                  String processInstanceIdFromApi = item2.get("processInstanceId").asText();
                                  
                                  TemaExceptionEntity matchingException = temaExceptionRepository.findByProcessId(processInstanceIdFromApi);

                                  if (matchingException != null) {
                                      System.out.println(matchingException);
                                      matchingExceptions.add(matchingException);
                                  }
                              }
                          }
                      } catch (HttpStatusCodeException e) {
                         
                          System.err.println("Failed to fetch data for group: " + groupId);
                      }
                    }
                }
            return ResponseEntity.ok(matchingExceptions);
        }
        }
	
	@GetMapping("/getUnclaimedPerformTask/{userId}")
	public ResponseEntity<?> getUnclaimedPerformTask(@PathVariable String userId) {
       
            
            String initialApiUrl = "http://" + ipAddress + ":8080/engine-rest/group?member=" + userId;
            JsonNode apiDataArray = restTemplate.getForObject(initialApiUrl, JsonNode.class);
            
            if (apiDataArray == null || !apiDataArray.isArray()) {
                return ResponseEntity.notFound().build();
            }


            else {
                
                List<TemaExceptionEntity> matchingExceptions = new ArrayList<>();
                
                for (JsonNode item : apiDataArray) {
                	
                
                    if (item.has("id")) {
                        String groupId = item.get("id").asText();
                        
                        String groupApiUrl = "http://" + ipAddress + ":8080/engine-rest/task?candidateGroup=" + groupId;
                        try {
                          JsonNode apiDataArray2 = restTemplate.getForObject(groupApiUrl, JsonNode.class);

                          for (JsonNode item2 : apiDataArray2) {
                              if (item2.has("processInstanceId") && !(item2.get("name").asText()).equals("4-Eye check" ) ){
                            	  
                                  String processInstanceIdFromApi = item2.get("processInstanceId").asText();
                                  
                                  TemaExceptionEntity matchingException = temaExceptionRepository.findByProcessId(processInstanceIdFromApi);

                                  if (matchingException != null) {
                                      System.out.println(matchingException);
                                      matchingExceptions.add(matchingException);
                                  }
                              }
                          }
                      } catch (HttpStatusCodeException e) {
                         
                          System.err.println("Failed to fetch data for group: " + groupId);
                      }
                    }
                }
            return ResponseEntity.ok(matchingExceptions);
        }
        }
	
	
	

                
               
	
	@PostMapping("/assignException")
	public ResponseEntity<?> assignExceptionToGroup(@RequestBody Map<String, String> assignGroup) {

		try {

			String processId = exceptionManagementServiceImp.getProcessId(assignGroup.get("exceptionId"));

			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);

			System.out.println(taskId);

			assignGroup.put("type", "candidate");

			assignGroup.remove("exceptionId");

			System.out.println("process id is " + processId);

			exceptionManagementServiceImp.deleteOtherUser(taskId);

			System.out.println("task id is " + taskId);

			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/identity-links";

			//String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";

			String assignGroupJson = exceptionManagementServiceImp.mapToJson(assignGroup);
			System.out.println(assignGroupJson);

			System.out.println("in assignGroup Controller");

 

//			HttpHeaders headers = new org.springframework.http.HttpHeaders();

//			headers.setContentType(MediaType.APPLICATION_JSON);

//			HttpEntity<String> requestEntity = new HttpEntity<>(assignGroupJson, headers);

//          ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, requestEntity, String.class);

		

			ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl, assignGroupJson);

			

			if (response.getStatusCode().is2xxSuccessful()) {

				System.out.println(assignGroupJson);

				return ResponseEntity.ok("Data sent to Spring Boot and external API successfully");

			} else {

				System.out.println("inside assignGroup Controller If condition");

 

				return ResponseEntity.status(response.getStatusCode())

						.body("External API returned an error: " + response.getBody());

			}

		} catch (Exception e) {

 

			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

					.body("Error occurred while processing the request");

		}

	}

	@PostMapping("/claimUser")
	public ResponseEntity<?> assignUser(@RequestBody Map<String, String> assignUser) {
		try {
			// String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";
			System.out.print(assignUser);
			System.out.print(assignUser.get("exceptionId"));
			System.out.print(assignUser.get("userId"));
			String processId = exceptionManagementServiceImp.getProcessId(assignUser.get("exceptionId"));
			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);
			assignUser.remove("exceptionId");
			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/claim";
			String assignUserJson = exceptionManagementServiceImp.mapToJson(assignUser);
			System.out.println(assignUserJson);

//			HttpHeaders headers = new org.springframework.http.HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON);
//			HttpEntity<String> requestEntity = new HttpEntity<>(assignUserJson, headers);
//			ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, requestEntity, String.class);
			
			ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl, assignUserJson);
			
			if (response.getStatusCode().is2xxSuccessful()) {
				System.out.println(assignUserJson);
				return ResponseEntity.ok("Data sent to Spring Boot and external API successfully");
			} else {
				System.out.println("inside assignGroup Controller If condition");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
	}
	
	
	
	@PostMapping("/completedFourEyeCheckUp")
	public ResponseEntity<?> completedFourEyeCheckUp(@RequestBody Map<String, String> exceptionId) {
		try {
			// String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";
			String processId = exceptionManagementServiceImp.getProcessId(exceptionId.get("exceptionId"));
			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);
			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/complete";
			System.out.println("task id :" + taskId);
			String jsonString =  "{ \"variables\": { \"i\": { \"value\": 0 } } }";
			System.out.println(jsonString);
			 ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl,jsonString);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Group to Four Eye Check done !!!");
			} else {
				System.out.println("inside else of controller of groupt to 4 eye check");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
}
	
	
//    public ResponseEntity<?> getExceptionDetail(@PathVariable String exceptionId) {
//        try {
//            Optional<TemaMongoEntity> exceptionDetails = exceptionManagementService.getExceptionDetails(exceptionId);
//
//            if (exceptionDetails.isPresent()) {
//                return ResponseEntity.ok(exceptionDetails.get());
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
	
	@GetMapping("/get/{exceptionId}")
    public ResponseEntity<?> getExceptionDetail(@PathVariable String exceptionId) {
        try {
        	System.out.println(" inside get exception details"+ exceptionId);
            TemaExceptionEntity exceptionDetails = exceptionManagementServiceImp.getExceptionDetails(exceptionId);
            if (exceptionDetails != null) {
                return ResponseEntity.ok(exceptionDetails);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getHistory/{exceptionId}")
    public ResponseEntity<?> getExceptionHistory(@PathVariable String exceptionId) {
        try {
        	System.out.println(" inside get exception details"+ exceptionId);
        	List<Map<String, Object>> exceptionHistory = exceptionManagementServiceImp.getExceptionHistory(exceptionId);
        	if (exceptionHistory.size() > 0) {
				return new ResponseEntity<List<Map<String, Object>>>(exceptionHistory, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Data not available", HttpStatus.NOT_FOUND);
			}
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    } 

  
	@PostMapping("/groupToFourEyeCheck")
	public ResponseEntity<?> groupToFourEyeCheck(@RequestBody Map<String, String> exceptionId) {
		try {
			// String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";
			String processId = exceptionManagementServiceImp.getProcessId(exceptionId.get("exceptionId"));
			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);
			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/complete";
			System.out.println("task id :" + taskId);
			String jsonString =  "{ \"variables\": { \"i\": { \"value\": 0 } } }";
			System.out.println(jsonString);
			 ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl,jsonString);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Group to Four Eye Check done !!!");
			} else {
				System.out.println("inside else of controller of groupt to 4 eye check");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
}
	
	@PostMapping("/groupToEscalation")
	public ResponseEntity<?> groupToEscalation(@RequestBody Map<String, String> exceptionId) {
		try {
			// String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";
			String processId = exceptionManagementServiceImp.getProcessId(exceptionId.get("exceptionId"));
			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);
			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/complete";
			System.out.println("task id :" + taskId);
			String jsonString =  "{ \"variables\": { \"i\": { \"value\": 1 } } }";
			System.out.println(jsonString);
			 ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl,jsonString);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Exception sent from group to escalation !!!");
			} else {
				System.out.println("inside else of controller of groupt to 4 eye check");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
}
	
	@PostMapping("/escalationToFourEyeCheck")
	public ResponseEntity<?> escalationToFourEyeCheck(@RequestBody Map<String, String> exceptionId) {
		System.out.println("hi");
		try {
			// String externalApiUrl = "https://jsonplaceholder.typicode.com/posts";
			String processId = exceptionManagementServiceImp.getProcessId(exceptionId.get("exceptionId"));
			System.out.println(processId);
			String taskId = exceptionManagementServiceImp.fetchTaskId(processId);
			String externalApiUrl = "http://"+ ipAddress +":8080/engine-rest/task/" + taskId + "/complete";
			System.out.println("task id :" + taskId);
			String jsonString =  "{ \"variables\": { \"i\": { \"value\": 3 } } }";
			 ResponseEntity<String> response = exceptionManagementServiceImp.postJsonToExternalApi(externalApiUrl,jsonString);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Exception sent from group to escalation !!!");
			} else {
				System.out.println("inside else of controller of groupt to 4 eye check");
				return ResponseEntity.status(response.getStatusCode())
						.body("External API returned an error: " + response.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the request");
		}
}
	
	@PostMapping("/updateResolutionCount/{exceptionId}")
    public ResponseEntity<?> updateResolutionCount(@PathVariable String exceptionId, @RequestBody String resolutionCount) {
        try {
            exceptionManagementServiceImp.updateResolutionCount(exceptionId, resolutionCount);
            return ResponseEntity.ok("Attribute updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while updating the attribute");
        }
    }
}