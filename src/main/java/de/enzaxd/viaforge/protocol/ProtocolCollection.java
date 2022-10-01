package de.enzaxd.viaforge.protocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public enum ProtocolCollection {

    R1_19_2(new ProtocolVersion(760, "1.19.1-1.19.2")),
    R1_19(new ProtocolVersion(759, "1.19")),

    R1_18_2(new ProtocolVersion(758, "1.18.2")),
    R1_18_1(new ProtocolVersion(757, "1.18-1.18.1")),
    
    R1_17_1(new ProtocolVersion(756, "1.17.1")),
    R1_17(new ProtocolVersion(755, "1.17")),

    R1_16_5(new ProtocolVersion(754, "1.16.4-1.16.5")),
    R1_16_3(new ProtocolVersion(753, "1.16.3")),
    R1_16_2(new ProtocolVersion(751, "1.16.2")),
    R1_16_1(new ProtocolVersion(736, "1.16.1")),
    R1_16(new ProtocolVersion(735, "1.16")),

    R1_15_2(new ProtocolVersion(578, "1.15.2")),
    R1_15_1(new ProtocolVersion(575, "1.15.1")),
    R1_15(new ProtocolVersion(573, "1.15")),

    R1_14_4(new ProtocolVersion(498, "1.14.4")),
    R1_14_3(new ProtocolVersion(490, "1.14.3")),
    R1_14_2(new ProtocolVersion(485, "1.14.2")),
    R1_14_1(new ProtocolVersion(480, "1.14.1")),
    R1_14(new ProtocolVersion(477, "1.14")),

    R1_13_2(new ProtocolVersion(404, "1.13.2")),
    R1_13_1(new ProtocolVersion(401, "1.13.1")),
    R1_13(new ProtocolVersion(393, "1.13")),

    R1_12_2(new ProtocolVersion(340, "1.12.2")),
    R1_12_1(new ProtocolVersion(338, "1.12.1")),
    R1_12(new ProtocolVersion(335, "1.12")),

    R1_11_1(new ProtocolVersion(316, "1.11.1-1.11.2")),
    R1_11(new ProtocolVersion(315, "1.11")),

    R1_10(new ProtocolVersion(210, "1.10.x")),

    R1_9_4(new ProtocolVersion(110, "1.9.3-1.9.4")),
    R1_9_2(new ProtocolVersion(109, "1.9.2")),
    R1_9_1(new ProtocolVersion(108, "1.9.1")),
    R1_9(new ProtocolVersion(107, "1.9")),

    R1_8(new ProtocolVersion(47, "1.8.x"));

    private ProtocolVersion version;

    private ProtocolCollection(ProtocolVersion version) {
        this.version = version;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public static ProtocolVersion getProtocolById(int id) {
        for (ProtocolCollection coll : values())
            if (coll.getVersion().getVersion() == id)
                return coll.getVersion();
        return null;
    }
}
