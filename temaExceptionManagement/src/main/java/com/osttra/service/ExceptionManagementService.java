package com.osttra.service;



	import com.osttra.entity.SourceExceptionEntity;
	import com.osttra.entity.TemaExceptionEntity;
	import org.springframework.http.ResponseEntity;

	import java.util.List;
	import java.util.Map;

	public interface ExceptionManagementService {

	    List<SourceExceptionEntity> getAllFromSource();

	    void deleteAllItemsSource();

	    void deleteAllItemsTema();

	    SourceExceptionEntity addExceptionInSource(SourceExceptionEntity sourceData);

	    void migrateData();

	    ResponseEntity<String> deleteOtherUser(String taskId);

	    List<Map<String, Object>> getExceptionHistory(String exceptionId);

	    String fetchTaskId(String processId);

	    void updateResolutionCount(String exceptionId, String resolutionCount);

	    TemaExceptionEntity getExceptionDetails(String exceptionId);

	    List<TemaExceptionEntity> showAllException();
	
}
