SET @parentId = 1909075032988282880;
-- 专家信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '专家信息管理', 1000, 2, '/training/expert', 'Expert', 'training/expert/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 专家信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909075032988282881, '列表', @parentId, 3, 'training:expert:list', 1, 1, 1, NOW()),
    (1909075032988282882, '详情', @parentId, 3, 'training:expert:detail', 2, 1, 1, NOW()),
    (1909075032988282883, '新增', @parentId, 3, 'training:expert:add', 3, 1, 1, NOW()),
    (1909075032988282884, '修改', @parentId, 3, 'training:expert:update', 4, 1, 1, NOW()),
    (1909075032988282885, '删除', @parentId, 3, 'training:expert:delete', 5, 1, 1, NOW()),
    (1909075032988282886, '导出', @parentId, 3, 'training:expert:export', 6, 1, 1, NOW());

