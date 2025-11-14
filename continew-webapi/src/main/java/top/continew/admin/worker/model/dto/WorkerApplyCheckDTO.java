package top.continew.admin.worker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerApplyCheckDTO {
    /**
     * 报名状态（WorkerApplyCheckConstants）
     */
    private String status;

    /**
     * 完整身份证号（仅当在当前班级找到时返回）
     */
    private String idCardNumber;
}