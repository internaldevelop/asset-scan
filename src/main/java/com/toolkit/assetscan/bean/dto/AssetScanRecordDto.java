package com.toolkit.assetscan.bean.dto;

import com.toolkit.assetscan.bean.po.AssetScanDataPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssetScanRecordDto extends AssetScanDataPo {
    private String asset_name;
    private String asset_ip;
    private String creator_name;
    private String creator_account;
}
