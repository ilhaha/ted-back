package top.continew.admin.exam.model.dto;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2025/12/26 9:46
 */
@Data
public class EnrollWithClassDTO {

    private String className;

    private Long candidateId;

    private Long planId;
}
