package top.continew.admin.training.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/12/1 14:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelParseResultVO implements Serializable {

    private List<ExcelRowSuccessVO> successList;

    private List<ExcelRowErrorVO> failedList;
}
