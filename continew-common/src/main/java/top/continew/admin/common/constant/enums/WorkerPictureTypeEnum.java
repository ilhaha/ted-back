package top.continew.admin.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkerPictureTypeEnum {

    ID_CARD_BACK(0, "身份证反面"),
    ID_CARD_FRONT(1, "身份证正面"),
    PASSPORT_PHOTO(2, "一寸免冠照"),
    GENERAL_PHOTO(3, "普通图片");

    private final Integer value;
    private final String description;

}
