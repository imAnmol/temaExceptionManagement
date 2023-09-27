package com.osttra.repository.temaDatabase;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.osttra.entity.TemaExceptionEntity;

@Repository
public interface TemaExceptionRepository extends MongoRepository<TemaExceptionEntity, String> {
	TemaExceptionEntity findByExceptionId(String exceptionId);
	TemaExceptionEntity findByProcessId(Object processId);

}
