package com.osttra.repository.database2;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.osttra.entity.TemaMongoEntity;

@Repository
public interface TemaMongoRepository extends MongoRepository<TemaMongoEntity, String> {
	TemaMongoEntity findByExceptionId(String exceptionId);
	TemaMongoEntity findByProcessId(Object processId);

}
