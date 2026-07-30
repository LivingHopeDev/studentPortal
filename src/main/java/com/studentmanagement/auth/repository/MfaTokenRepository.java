package com.studentmanagement.auth.repository;

import com.studentmanagement.auth.model.MfaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MfaTokenRepository extends JpaRepository<MfaToken, UUID> {

    Optional<MfaToken> findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(UUID userId);

    void deleteByUserId(UUID userId);

}
