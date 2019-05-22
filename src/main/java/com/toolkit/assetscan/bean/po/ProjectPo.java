package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProjectPo {
    private int id;
    private String uuid;
    private String name;
    private String code;
    private String tasks;
    private int run_time_mode;
    private int output_mode;
    private String output_path;
    private int task_number;
    private int process_flag;  // 项目执行过程的标识
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp update_time;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
