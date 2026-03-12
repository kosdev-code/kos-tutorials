package com.kondra.device.mgmt.data;

import com.kosdev.kos.sdk.annotations.Sdk;

import lombok.Data;

@Data
@Sdk
public class DeviceManagementInfo {
    private String country;
    private String physicalAddress;
    private String owner; // the common name of the owning organization
}
