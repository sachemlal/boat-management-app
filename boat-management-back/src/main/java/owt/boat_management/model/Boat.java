package owt.boat_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
