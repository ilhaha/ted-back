package top.continew.admin.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ilhaha
 * @Create 2025/11/3 10:46
 */
@Getter
public enum OrgClassType {

    WORKER_TYPE(0), INSPECTORS_TYPE(1);

    Integer classType;

    OrgClassType(Integer classType) {
        this.classType = classType;
    }
}
