package top.continew.admin.training.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.continew.admin.worker.model.entity.WorkerApplyDO;

/**
 * @author ilhaha
 * @Create 2025/11/5 16:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedSuccessVO extends WorkerApplyDO {

    private String excelName;

    private Integer rowNum;

    /**
     * aesPhone
     */
    private String encFieldA;

    /**
     * aesIdCard
     */
    private String encFieldB;
}
