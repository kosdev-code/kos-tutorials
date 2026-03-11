package com.kondra.device.mgmt.data;

import lombok.Data;

@Data
public class DeviceManagementInfo {
    private String country;
    private String physicalAddress;
    private String owner; // the common name of the owning organization
}
