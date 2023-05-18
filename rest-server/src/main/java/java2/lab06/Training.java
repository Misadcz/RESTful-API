package java2.lab06;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
@Table(name = "Training")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Training {

    @Include
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Long id;
    private String exercise;
    private String today;
    private Double weight;
    private Long entry;
    private String ago;
}
