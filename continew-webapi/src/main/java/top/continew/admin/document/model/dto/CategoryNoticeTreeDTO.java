package top.continew.admin.document.model.dto;

import lombok.Data;

/**
 * @author ilhaha
 * @Create 2026/5/7 13:39
 */
@Data
public class CategoryNoticeTreeDTO {

    private String categoryName;

    private Long categoryId;

    private Long noticeId;

    private String title;

    private Integer examLevel;

}
