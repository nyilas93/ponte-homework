package hu.ponte.hr.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor
public class ImageEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private String type;
    @Lob
    private byte[] data;
    @Column(columnDefinition = "TEXT")
    private String sign;

    public ImageEntity(String name, String type, byte[] data, String sign) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.sign = sign;
    }
}
