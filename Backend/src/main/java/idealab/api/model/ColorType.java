package idealab.api.model;

import javax.persistence.*;
import java.util.Set;


/**
 * This class holds the model for the color type associated with the print job. The fields
 * include color + whether or not it is available. It has a one to many relationship with
 * print jobs.
 */

@Entity
@Table(name = "color_type")
public class ColorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(targetEntity=PrintJob.class, mappedBy="colorTypeId")
    private Set<PrintJob> printJobs;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "available", nullable = false)
    private Boolean available;

    public ColorType(Integer id, String color) {
        this.color = color;
        this.available = true;
    }

    //getters and setters
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getAvailable() {
        return this.available;
    }
}

