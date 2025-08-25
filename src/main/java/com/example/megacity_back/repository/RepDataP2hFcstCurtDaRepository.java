package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepDataP2hFcstCurtDaRepository extends CrudRepository<RepDataP2hFcstCurtDa, FcstCurtDaId> {
    List<RepDataP2hFcstCurtDa> findByFcstTmStartingWith(String fcstTmPrefix);
}
