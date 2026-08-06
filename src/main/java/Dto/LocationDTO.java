package Dto;

import lombok.Data;

@Data
public class LocationDTO {

    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Long timestamp;
    private String source;

}