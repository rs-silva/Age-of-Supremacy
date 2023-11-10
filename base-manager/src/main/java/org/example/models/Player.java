package org.example.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "players")
public class Player {

    @Id
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String username;

    private Integer totalScore;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Base> baseList;

    public void addBase(Base base) {
        this.baseList.add(base);
    }

}
