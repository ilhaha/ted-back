package top.continew.admin.training.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ilhaha
 * @Create 2025/11/5 16:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedExcelResultVO implements Serializable {

    private List<ParsedSuccessVO> successList;

    private List<ParsedErrorVO> failedList;
}
