package com.example.megacity_back.hydrogen;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDateTime;
import java.util.*;
public interface HydrogenControlRepository extends JpaRepository<HydrogenControl,String> {
    Optional<HydrogenControl> findFirstByEffectiveAtLessThanEqualOrderByEffectiveAtDescSequenceDesc(LocalDateTime at);
    List<HydrogenControl> findByEffectiveAtGreaterThanAndEffectiveAtLessThanOrderByEffectiveAtAscSequenceAsc(LocalDateTime from, LocalDateTime to);
    List<HydrogenControl> findTop50ByOrderBySequenceDesc();
    @Query("select coalesce(max(c.sequence),0) from HydrogenControl c") long lastSequence();
}
