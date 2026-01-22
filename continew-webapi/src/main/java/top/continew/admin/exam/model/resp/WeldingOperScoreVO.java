package top.continew.admin.exam.model.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2026/1/21 16:47
 */
@Data
public class WeldingOperScoreVO implements Serializable {


    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 焊接项目代码
     */
    @Schema(description = "焊接项目代码")
    private String projectCode;

    /**
     * 实操成绩
     */
    @Schema(description = "实操成绩")
    private Integer operScore;


}
