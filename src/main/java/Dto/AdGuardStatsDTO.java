package Dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdGuardStatsDTO {

    private Long num_dns_queries;

    private Long num_blocked_filtering;

    private Boolean protection_enabled;

    private List<Map<String, Integer>> top_clients;

    private List<Map<String, Integer>> top_blocked_domains;

    private List<Map<String, Integer>> top_queried_domains;

}