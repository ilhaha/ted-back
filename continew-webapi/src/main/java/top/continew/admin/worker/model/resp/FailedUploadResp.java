package top.continew.admin.worker.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ilhaha
 * @Create 2025/12/3 10:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedUploadResp {

    private String idCard;

    private String errorMessage;
}
