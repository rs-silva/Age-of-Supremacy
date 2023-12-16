package com.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bases")
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private int x_coordinate;

    private int y_coordinate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    private int score;

    @ElementCollection
    @CollectionTable(name = "bases_resources", joinColumns = @JoinColumn(name = "base_id"))
    @MapKeyColumn(name = "resource_name")
    @Column(name = "resource_quantity")
    private Map<String, Double> resources;

    @ElementCollection
    @CollectionTable(name = "bases_own_units", joinColumns = @JoinColumn(name = "base_id"))
    @MapKeyColumn(name = "unit_name")
    @Column(name = "unit_quantity")
    private Map<String, Integer> units;

    @OneToMany(mappedBy = "baseBeingSupported", cascade = CascadeType.ALL)
    private List<SupportArmy> supportUnits;

    private Timestamp lastResourcesUpdate;

    @OneToMany(mappedBy = "base", cascade = CascadeType.ALL)
    private List<Building> buildings;

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    @Override
    public String toString() {
        return "Base{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x_coordinate=" + x_coordinate +
                ", y_coordinate=" + y_coordinate +
                ", score=" + score +
                ", resources=" + resources +
                ", lastResourcesUpdate=" + lastResourcesUpdate +
                ", buildings=" + buildings +
                '}';
    }
}
