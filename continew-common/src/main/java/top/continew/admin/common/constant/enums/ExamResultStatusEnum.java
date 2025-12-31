package top.continew.admin.common.constant.enums;

public enum ExamResultStatusEnum {
    FAILED(0),
    PASSED(1),
    NOT_ENTERED(2);


    private final int value;

    ExamResultStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
