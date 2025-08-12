package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataReKpxJejuSukubMRepository extends JpaRepository<RepDataReKpxJejuSukubM, Long> {
    Optional<RepDataReKpxJejuSukubM> findTopByOrderByTmDesc();
    List<RepDataReKpxJejuSukubM> findByTmStartingWith(String tm);
}
