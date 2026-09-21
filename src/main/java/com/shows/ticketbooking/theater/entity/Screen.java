package com.shows.ticketbooking.theater.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "screens",
       uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_theater_screen_name",
            columnNames = {"theater_id", "screen_name"}
        )
       })
@Getter
@Setter
@NoArgsConstructor
public class Screen {
    
    @Id
    @Column(name = "screen_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long screenId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @NotBlank
    @Column(name = "screen_name", nullable = false, length = 50)
    private String screenName;

    @NotBlank
    @Column(name = "screen_type", nullable = false, length = 50)
    private String screenType;

}
