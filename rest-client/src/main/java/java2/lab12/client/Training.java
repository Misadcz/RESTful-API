package java2.lab12.client;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.time.LocalDate;
import java.sql.Date;


@XmlRootElement
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Training
{

    private Long id;
    private String exercise;
    private String today;
    private Long weight;
    private Long entry;
    private String ago;

}
