package com.urly.urlyservices.db.repository;

import com.urly.urlyservices.db.entity.LongToSequenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LongToSequenceIdRepository extends JpaRepository<LongToSequenceId, Long> {

    Optional<LongToSequenceId> findByLongUrl(String longUrl);

    Optional<LongToSequenceId> findBySequenceId(Long sequenceId);
}
