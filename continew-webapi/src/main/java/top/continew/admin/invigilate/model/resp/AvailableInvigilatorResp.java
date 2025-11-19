package top.continew.admin.invigilate.model.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ilhaha
 * @Create 2025/11/18 16:20
 */
@Data
public class AvailableInvigilatorResp implements Serializable {

    private Long id;

    private String nickname;
}
