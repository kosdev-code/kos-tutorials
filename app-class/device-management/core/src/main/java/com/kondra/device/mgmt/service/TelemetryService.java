package com.kondra.device.mgmt.service;

import com.kondra.device.mgmt.data.DeviceManagementInfo;
import com.kosdev.kos.commons.core.service.AbstractService;
import com.kosdev.kos.commons.core.service.trouble.Trouble;
import com.kosdev.kos.commons.core.service.trouble.TroubleListener;
import com.kosdev.kos.commons.core.service.trouble.TroubleMatcher;
import com.kosdev.kos.core.service.analytics.AnalyticsDefaultChannelExporter;
import com.kosdev.kos.core.service.analytics.AnalyticsExportBean;
import com.kosdev.kos.core.service.log.LogArchiveFile;
import com.kosdev.kos.core.service.log.LogExporter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

@Slf4j
public class TelemetryService extends AbstractService
        implements AnalyticsDefaultChannelExporter, LogExporter, TroubleListener {

    private static final List<String> ADDRESSES = List.of(
            "110 N Wacker Dr, Chicago, IL 60606",
            "1441 Gardiner Ln, Louisville, KY 40213",
            "5505 Blue Lagoon Dr, Miami, FL 33126",
            "1 Amy's Dr, Dallas, TX 75240",
            "1 Dave Thomas Blvd, Dublin, OH 43017",
            "1155 Perimeter Center W, Atlanta, GA 30338",
            "3 First Cook Rd, Milpitas, CA 95035",
            "2107 Lamar Ave, Memphis, TN 38114",
            "400 Quality Dr, Irvine, CA 92618",
            "9 Inspire Blvd, San Antonio, TX 78216");

    private static final List<String> OWNERS = List.of(
            "McDonald's",
            "KFC",
            "Burger King",
            "Taco Bell",
            "Wendy's",
            "Chick-fil-A",
            "In-N-Out Burger",
            "Popeyes",
            "Five Guys",
            "Shake Shack");

    // get troubles
    // get serial number

    @Getter
    private DeviceManagementInfo deviceManagementInfo;

    public TelemetryService() {

    }


    public void syncWithServer() {
        deviceManagementInfo = getInfoFromServer();
    }

    private DeviceManagementInfo getInfoFromServer() {

        Random random = new Random();

        DeviceManagementInfo info = new DeviceManagementInfo();
        info.setCountry("United States");
        info.setPhysicalAddress(ADDRESSES.get(random.nextInt(ADDRESSES.size())));
        info.setOwner(OWNERS.get(random.nextInt(OWNERS.size())));

        return info;
    }




    // Exporting analytics
    @Override
    public void exportAnalytics(String channel, List<AnalyticsExportBean> beans) throws Exception {

        for (AnalyticsExportBean bean : beans) {

            log.info("Exporting analytics for channel {}", bean.getData());
        }

    }





    // Exporting logs

    @Override
    public String getExporterName() {
        return "KondraDM";
    }

    @Override
    public boolean isExporterActive() {
        return true;
    }

    @Override
    public void exportLogArchive(LogArchiveFile file, InputStream is) throws Exception {
        log.info("Exporting log archive {} to the cloud", file.getFileName());
    }





    // Listen for troubles and their states
    @Override
    public TroubleMatcher getMatcher() {
        // only the matchers that return true will get the TroubleAware callbacks
        return (Trouble trouble) -> {
            return true;

        };
    }

    // trouble aware callbacks
    @Override
    public void onTroubleAdded(Trouble trouble) {
        log.info("Trouble added: {} sending to cloud", trouble.getType());
    }

    @Override
    public void onTroubleRemoved(Trouble trouble) {
        log.info("Trouble removed: {} sending to cloud", trouble.getType());
    }

    @Override
    public void onTroubleResolved(Trouble trouble, boolean success) {
        log.info("Trouble resolved: {} sending to cloud", trouble.getType());
    }


}
