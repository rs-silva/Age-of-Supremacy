package com.example.models;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "support_army_events")
public class SupportArmyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID ownerBaseId;

    private UUID originBaseId;

    private UUID destinationBaseId;

    @ElementCollection
    @CollectionTable(name = "support_armies_units", joinColumns = @JoinColumn(name = "support_army_id"))
    @MapKeyColumn(name = "unit_name")
    @Column(name = "unit_quantity")
    private Map<String, Integer> units = new HashMap<>();

    private Timestamp completionTime;

}
