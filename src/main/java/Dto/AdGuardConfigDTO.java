package Dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdGuardConfigDTO {

    private List<Map<String, Object>> filters;

    private List<String> user_rules;

    private Integer interval;

    private Boolean enabled;

}