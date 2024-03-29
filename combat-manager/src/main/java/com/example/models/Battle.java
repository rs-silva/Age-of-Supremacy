package com.example.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "battles")
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private UUID baseId;

    private int groundDefensePower;

    private int armoredDefensePower;

    private int airDefensePower;

    private int defenseHealthPoints;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private List<Army> armies;

    @Override
    public String toString() {
        return "Battle{" +
                "id=" + id +
                ", baseId=" + baseId +
                ", groundDefensePower=" + groundDefensePower +
                ", antiTankDefensePower=" + armoredDefensePower +
                ", antiAirDefensePower=" + airDefensePower +
                ", defenseHealthPoints=" + defenseHealthPoints +
                '}';
    }
}
