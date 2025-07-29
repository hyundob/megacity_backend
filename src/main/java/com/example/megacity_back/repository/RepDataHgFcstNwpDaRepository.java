package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RepDataHgFcstNwpDaRepository extends CrudRepository<RepDataHgFcstNwpDa, FcstNwpDaId> {
    Optional<RepDataHgFcstNwpDa> findTopByOrderByFcstTmDesc();
}
