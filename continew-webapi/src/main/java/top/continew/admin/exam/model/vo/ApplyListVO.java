package top.continew.admin.exam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/10 17:55
 */
@Data
public class ApplyListVO implements Serializable {


    /**
     * 报考班级名称
     */
    @Schema(description = "报考班级名称")
    private String className;

    /**
     * 报考考生名称
     */
    @Schema(description = "报考考生名称")
    private String candidateNames;
}
