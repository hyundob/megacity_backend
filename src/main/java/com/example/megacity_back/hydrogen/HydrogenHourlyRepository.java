package com.example.megacity_back.hydrogen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface HydrogenHourlyRepository extends JpaRepository<HydrogenHourly,String> {
    List<HydrogenHourly> findByTmGreaterThanEqualAndTmLessThanOrderByTmAsc(String from,String to);
}
