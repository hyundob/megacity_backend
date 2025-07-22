package com.example.megacity_back.entity;

import jakarta.persistence.*;


@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
}
