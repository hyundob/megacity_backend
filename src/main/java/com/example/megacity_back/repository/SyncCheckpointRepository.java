package com.example.megacity_back.repository;

import com.example.megacity_back.entity.SyncCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncCheckpointRepository extends JpaRepository<SyncCheckpoint, String> {
}
