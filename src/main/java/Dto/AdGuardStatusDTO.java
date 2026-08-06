package Dto;

import lombok.Data;

@Data
public class AdGuardStatusDTO {

    private String version;
    private Integer dns_port;
    private Integer http_port;
    private Boolean protection_enabled;
    private Boolean running;

}