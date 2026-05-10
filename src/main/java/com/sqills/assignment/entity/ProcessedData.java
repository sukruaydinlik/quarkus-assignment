package com.sqills.assignment.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ProcessedData extends PanacheEntity {
    @Column(length = 1000)
    public String outputText;
}
