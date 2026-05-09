package com.ncp.team3.crawl.domain;

import java.util.Arrays;

public enum PopplyCategory {
    FASHION(19, "패션"),
    BEAUTY_HEALTH(20, "뷰티/헬스"),
    FOOD_BEVERAGE(21, "푸드/음료"),
    KITCHEN_APPLIANCE(22, "키친/가전"),
    INTERIOR_LIVING(23, "인테리어/리빙"),
    FAMILY_LIFE(24, "패밀리/라이프"),
    KIDS_TOY(25, "키즈/완구"),
    TRAVEL_LEISURE_SPORTS(26, "여행/레저/스포츠"),
    PET(27, "반려동물"),
    DIGITAL_GAME_ESPORTS(28, "디지털/게임/e스포츠"),
    ENTERTAINMENT_CREATOR(29, "연예/크리에이터"),
    CONTENT_CULTURE(30, "콘텐츠/문화"),
    CHARACTER_IP(31, "캐릭터/IP"),
    GOODS(32, "소품/굿즈"),
    EXHIBITION(33, "전시"),
    PUBLIC_NONPROFIT(34, "공공/비영리"),
    BRAND_CAMPAIGN(35, "브랜드/캠페인"),
    ETC(36, "기타"),
    FESTIVAL(37, "페스티벌"),
    CONCERT(38, "콘서트"),
    MUSICAL(39, "뮤지컬"),
    PLAY(40, "연극");

    private final int id;
    private final String name;

    PopplyCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String nameOf(Integer id) {
        if (id == null) {
            return ETC.name;
        }

        return Arrays.stream(values())
                .filter(category -> category.id == id)
                .map(category -> category.name)
                .findFirst()
                .orElse(ETC.name);
    }

    public static String embeddingNameOf(String name) {
        if (name == null || name.isBlank()) {
            return ETC.name;
        }

        return name.replace("/", ", ");
    }

    public String displayName() {
        return name;
    }
}
