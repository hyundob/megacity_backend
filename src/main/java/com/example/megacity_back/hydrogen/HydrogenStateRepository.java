package com.example.megacity_back.hydrogen;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface HydrogenStateRepository extends JpaRepository<HydrogenState,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from HydrogenState s where s.systemId = 'MAIN'")
    Optional<HydrogenState> lockState();
}
