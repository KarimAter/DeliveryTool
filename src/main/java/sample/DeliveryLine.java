package sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeliveryLine {
    private String actionType, code, name, tech, band, feederType, currentRf, targetRf, currentSm, targetSm, u9Soln, txMode, antennaType;
    private int numberOfAddedSectors;
    private HashMap<String, KeyData> generatedKeysMap;
    Map<String, KeyData> filteredKeysMap;

    private String actionTypeKey;
    private String u9SolnKey;
    private int numberOfCurrentRfs, numberOfTargetRfs;
    private Integer numberOfCurrentCabinets;
    private Integer numberOfTargetCabinets;
    private boolean standAlone;
    private int currentNumberOfIndoorModules = 0;
    private int targetNumberOfIndoorModules = 0;
    private int numberOfTargetSms, numberOfCurrentSms;


    DeliveryLine() {
        generatedKeysMap = new HashMap<>();
    }

    private void hardwareExtractor(String hardware, int currentTarget) {
        if (hardware != null) {
            if (!hardware.equals("-") && !hardware.equals("")) {

                Stream.of(hardware.replace("+", " ")
                        .replaceAll("\\s+", " ")
                        .split(" "))
                        .forEach(s -> {
                            int quantity = Character.getNumericValue(s.charAt(0));
                            int index = 1;
                            if (quantity > 9 || quantity < 0) {
                                quantity = 1;
                                index = 0;
                            }
                            String type = s.substring(index);
                            if (currentTarget == -1)
                                generatedKeysMap.put(type, generateKeyData(-1 * quantity));
                            else {
                                generatedKeysMap.merge(type, generateKeyData(quantity), KeyData::sum);
                            }

                        });
            }
        }
    }

    void finish() {
        applyKeys();
        filteredKeysMap = generatedKeysMap.entrySet().stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue().getQuantity() != 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        int z = 1;
    }

    private void applyKeys() {

        // to get number of needed FUFAS or diversity kits in case of U9 sharing on diversity
        generateU9Accessories();

        checkNeededAlarmBox();

        calculateFMFAs();

        // to get number of needed fiber and power cables for double system modules
        systemModuleKeyGenerator(this.targetSm, 1);
        systemModuleKeyGenerator(this.currentSm, -1);

        addJumpers();
    }

    private void calculateFMFAs() {
        if (this.feederType.equals("FDL")) {
            currentNumberOfIndoorModules = numberOfCurrentSms;
            targetNumberOfIndoorModules = numberOfTargetSms;
            // todo:check if FPFC counts
        } else {
            currentNumberOfIndoorModules = numberOfCurrentRfs + numberOfCurrentSms;
            targetNumberOfIndoorModules = numberOfTargetRfs + numberOfTargetSms;
        }

    }

    private void checkNeededAlarmBox() {
        // todo: recheck for standalone sites
        if (standAlone || (tech.equals("2G") && actionTypeKey.equals("NST")))
            generatedKeysMap.put(actionTypeKey, generateKeyData(1));
    }

    private void addJumpers() {
        if (actionTypeKey.equals("NST") || actionTypeKey.equals("NSR") || u9SolnKey.equals("P12"))
            generatedKeysMap.put(actionTypeKey + Constants.ANTENNA_TYPE_MAP.get(antennaType), generateKeyData(2 * numberOfAddedSectors));
    }

    private void generateU9Accessories() {
        if (u9SolnKey != null) {
            if (!u9SolnKey.equals("P12")) {
                Integer delaU9Rfs = generatedKeysMap.entrySet().stream().filter(stringKeyDataEntry -> stringKeyDataEntry.getKey().contains("FXDA")
                        || stringKeyDataEntry.getKey().contains("FXDB"))
                        .map(Map.Entry::getValue).map(KeyData::getQuantity).reduce(0, Integer::sum);

                if (u9SolnKey.equals("SHR")) {
                    // Number of needed Fibers and their types for shared RFs
                    if (this.feederType.equals("FDR"))
                        generatedKeysMap.put("FUFAS_U9", new KeyData(delaU9Rfs, "U9"));
                    else generatedKeysMap.put("FUFAY_U9", new KeyData(delaU9Rfs, "U9"));

                    // to calculate number of needed FUFAS in case of U9 sharing
                    if (tech.equals("2G")) {
                        numberOfTargetCabinets = target2GCabinetCounter(this.targetSm);
                        numberOfCurrentCabinets = target2GCabinetCounter(this.currentSm);
                        int deltaCabinets = numberOfTargetCabinets - numberOfCurrentCabinets;
                        generatedKeysMap.merge("FUFAS_U9", new KeyData(deltaCabinets, "U9"), KeyData::sum);
                    }

                }

                if (u9SolnKey.equals("DIV")) {
                    // to calculate number of diversity kits in case of U9 diversity
                    generatedKeysMap.put("RDSA", new KeyData(delaU9Rfs, "U9"));
                    // to calculate number of FPFC in case of U9 diversity
                    generatedKeysMap.put("FPFC", new KeyData((int) Math.ceil(numberOfTargetRfs / 4), "U9"));
                }
            } else numberOfAddedSectors = numberOfAddedSectors * 2;
        }
    }

    private void systemModuleKeyGenerator(String sm, int dir) {
        if (sm.contains("2FSMF"))
            this.generatedKeysMap.merge("FUFAS", generateKeyData(3 * dir), KeyData::sum);
        else if (sm.contains("2FSM")) {
            this.generatedKeysMap.merge("FUFAS", generateKeyData(2 * dir), KeyData::sum);
            this.generatedKeysMap.merge("FPCB", generateKeyData(dir), KeyData::sum);
        }


    }

    private Integer target2GCabinetCounter(String targetSm) {
        if (targetSm != null) {
            if (!targetSm.equals("-") && !targetSm.equals("")) {
                return Stream.of(targetSm.replace("+", " ")
                        .replaceAll("\\s+", " ")
                        .split(" "))
                        .filter(s -> !s.equals("FBBA") || !s.equals("FBBC"))
                        .map(s -> {
                            int quantity = Character.getNumericValue(s.charAt(0));
                            if (quantity > 9 || quantity < 0) {
                                quantity = 1;
                            }
                            return quantity;
                        })
                        .reduce(0, Integer::sum);
            }
        }


        return 0;
    }

    private int moduleCounter(String module) {
        if (module != null) {
            if (!module.equals("-") && !module.equals("")) {

                return Stream.of(module.replace("+", " ")
                        .replaceAll("\\s+", " ")
                        .split(" "))
                        .filter(s -> !s.contains("FBB"))
                        .map(s -> {
                            int quantity = Character.getNumericValue(s.charAt(0));
                            if (quantity > 9 || quantity < 0) {
                                quantity = 1;
                            }
                            return quantity;
                        })
                        .reduce(0, Integer::sum);


//                Stream.of(targetSm.replace("+", " ")
//                        .replaceAll("\\s+", " ")
//                        .split(" "))
//                        .forEach(s -> {
//                            int quantity = Character.getNumericValue(s.charAt(0));
//                            if (quantity > 9 || quantity < 0) {
//                                quantity = 1;
//                            }
//                            numberOfCabinets[0] = numberOfCabinets[0] + quantity;
//                        });
            }
        }

        return 0;
    }

    private KeyData generateKeyData(int quantity) {
        KeyData keyData = new KeyData(tech);
        keyData.setQuantity(quantity);
        return keyData;
    }


    public Map<String, KeyData> getFilteredKeysMap() {
        return filteredKeysMap;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getU9Soln() {
        return u9Soln;
    }

    void setU9Soln(String u9Soln) {
        u9SolnKey = Constants.U9_SOLUTION_MAP.get(u9Soln);
        if (u9SolnKey == null)
            u9SolnKey = "";
        this.u9Soln = u9Soln;
    }

    public String getTxMode() {
        return txMode;
    }

    public void setTxMode(String txMode) {
        this.txMode = txMode;
    }

    public String getAntennaType() {
        return antennaType;
    }

    void setAntennaType(String antennaType) {
        if (antennaType != null)
            this.antennaType = antennaType;
        else this.antennaType = "";
    }

    public int getNumberOfAddedSectors() {
        return numberOfAddedSectors;
    }

    public void setNumberOfAddedSectors(int numberOfAddedSectors) {
        this.numberOfAddedSectors = numberOfAddedSectors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentRf() {
        return currentRf;
    }

    public void setCurrentRf(String currentRf) {
        this.currentRf = currentRf.toUpperCase();
        numberOfCurrentRfs = moduleCounter(currentRf);
        hardwareExtractor(this.currentRf, -1);
    }

    public String getTargetRf() {
        return targetRf;
    }

    void setTargetRf(String targetRf) {
        this.targetRf = targetRf.toUpperCase();
        numberOfTargetRfs = moduleCounter(targetRf);
        hardwareExtractor(this.targetRf, 1);
    }


    public String getCurrentSm() {
        return currentSm;
    }


    public void setCurrentSm(String currentSm) {
        this.currentSm = currentSm.toUpperCase();
        numberOfCurrentSms = moduleCounter(currentSm);
        hardwareExtractor(this.currentSm, -1);
    }

    public String getTargetSm() {
        return targetSm;
    }

    public void setTargetSm(String targetSm) {
        this.targetSm = targetSm.toUpperCase();
        numberOfTargetSms = moduleCounter(targetSm);
        hardwareExtractor(this.targetSm, 1);
    }


    public String getFeederType() {
        return feederType;
    }

    public void setFeederType(String feederType) {
        String feeder = Constants.FEEDER_TYPE_MAP.get(feederType);
        if (feeder != null)
            this.feederType = feeder;
        else this.feederType = "";
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        actionTypeKey = Constants.ACTION_TYPE_MAP.get(actionType);
        this.actionType = actionType;
    }


    public int getCurrentNumberOfIndoorModules() {
        return currentNumberOfIndoorModules;
    }

    public int getTargetNumberOfIndoorModules() {
        return targetNumberOfIndoorModules;
    }

    public void setFilteredKeysMap(Map<String, KeyData> filteredKeysMap) {
        this.filteredKeysMap = filteredKeysMap;
    }
}
