package com.toolkit.assetscan.bean.po;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RelationProjectResultsPo {
    private int id;
    private String project_uuid;
    private String task_uuid;
    private String effective_result_uuid;
}
