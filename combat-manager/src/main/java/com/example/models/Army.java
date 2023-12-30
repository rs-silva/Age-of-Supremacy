package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "armies")
public class Army {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID ownerPlayerId;

    private UUID ownerBaseId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @ElementCollection
    @CollectionTable(name = "armies_units", joinColumns = @JoinColumn(name = "army_id"))
    @MapKeyColumn(name = "unit_name")
    @Column(name = "unit_quantity")
    private Map<String, Integer> units = new HashMap<>();

}
