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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "support_armies")
public class SupportArmy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID ownerBaseId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "supporting_base_id")
    private Base baseBeingSupported;

    @ElementCollection
    @CollectionTable(name = "support_armies_units", joinColumns = @JoinColumn(name = "support_army_id"))
    @MapKeyColumn(name = "unit_name")
    @Column(name = "unit_quantity")
    private Map<String, Integer> units = new HashMap<>();

    @Override
    public String toString() {
        return "SupportArmy{" +
                "id=" + id +
                ", ownerBaseId=" + ownerBaseId +
                ", units=" + units +
                '}';
    }
}