package com.osttra.repository.sourceExceptionDatabase;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.osttra.entity.SourceExceptionEntity;

@Repository
public interface SourceExceptionRepository extends MongoRepository<SourceExceptionEntity, String> {

}
