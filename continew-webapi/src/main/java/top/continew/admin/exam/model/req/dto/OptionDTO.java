package top.continew.admin.exam.model.req.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    /** 原业务字段，保留兼容其他模块 */
    private String option;

    /** 选项内容，用于 Excel 导出/展示 */
    private String question;

    /** 是否正确答案 */
    private Boolean isCorrect;

    /** 可选：嵌套子选项或者模板使用 */
    @Schema(description = "选项列表")
    private List<OptionDTO> options;

    /** 便捷构造器，用 boolean 直接创建 */
    public OptionDTO(String question, boolean isCorrect) {
        this.question = question;
        this.isCorrect = isCorrect;
    }
}
