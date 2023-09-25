package com.osttra.repository.database1;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.osttra.entity.SourceMongoEntity;

@Repository
public interface SourceMongoRepository extends MongoRepository<SourceMongoEntity, String> {

}
