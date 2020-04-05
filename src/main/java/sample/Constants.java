package sample;

import java.util.LinkedHashMap;

public class Constants {

    public static final String databasePath = "C://Ater//Development//Delivery Tool//HwItems.db";


    public static final LinkedHashMap<String, String> ACTION_TYPE_MAP = new LinkedHashMap<String, String>() {
        {
            put("2nd Carrier", "2CR");
            put("CE Upgrade", "CEU");
            put("Delivery", "DEL");
            put("Dismantle", "DIS");
            put("Dual Stack", "DUS");
            put("Faulty", "FAL");
            put("IUB Upgrade", "IUU");
            put("New Sector", "NSR");
            put("New Site", "NST");
            put("PA", "POU");
            put("Full IP", "FIP");
            put("3rd Carrier.", "3CR");
            put("Packet Abis.", "PCA");
            put("BB Upgrade.", "BBU");
            put("U900 Upgrade", "UUP");
            put("BW Upgrade", "BWU");
            put("4X4 Upgrade", "4X4");
            //tODO: CARRIER AGGR
        }
    };
    public static final LinkedHashMap<String, String> U9_SOLUTION_MAP = new LinkedHashMap<String, String>() {
        {
            put("12port", "P12");
            put("Diversity", "DIV");
            put("Sharing", "SHR");
            put("Mixed", "MIX");

        }
    };

    public static final LinkedHashMap<String, String> BAND_MAP = new LinkedHashMap<String, String>() {
        {
            put("900", "G");
            put("1800", "D");
            put("2100", "U");
        }
    };

    public static final LinkedHashMap<String, String> FEEDER_TYPE_MAP = new LinkedHashMap<String, String>() {
        {
            put("Feeder", "FDR");
            put("Feederless", "FDL");
        }
    };

    public static final LinkedHashMap<String, String> TECHNOLOGY_MAP = new LinkedHashMap<String, String>() {
        {
            put("2G", "2G");
            put("3G", "3G");
            put("4G", "LTE");
            put("U900", "U9");
        }
    };

    public static final LinkedHashMap<String, String> ANTENNA_TYPE_MAP = new LinkedHashMap<String, String>() {
        {
            put("Old", "OLD");
            put("New", "NEW");
        }
    };
    public static final LinkedHashMap<String, String> TX_MODE_MAP = new LinkedHashMap<String, String>() {
        {
            put("ATM", "ATM");
            put("IP", "IP");
            put("DualStack", "DS");
        }
    };
    public static final LinkedHashMap<String, String> PO_MAP = new LinkedHashMap<String, String>() {
        {
            put("USED", "USED");
            put("NEW", "NEW");
        }
    };
}
