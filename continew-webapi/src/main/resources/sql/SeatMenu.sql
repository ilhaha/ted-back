SET @parentId = 1921765792606965760;
-- 座位表管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '座位表管理', 1000, 2, '/exam/seat', 'Seat', 'exam/seat/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 座位表管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1921765792606965761, '列表', @parentId, 3, 'exam:seat:list', 1, 1, 1, NOW()),
    (1921765792606965762, '详情', @parentId, 3, 'exam:seat:detail', 2, 1, 1, NOW()),
    (1921765792606965763, '新增', @parentId, 3, 'exam:seat:add', 3, 1, 1, NOW()),
    (1921765792606965764, '修改', @parentId, 3, 'exam:seat:update', 4, 1, 1, NOW()),
    (1921765792606965765, '删除', @parentId, 3, 'exam:seat:delete', 5, 1, 1, NOW()),
    (1921765792606965766, '导出', @parentId, 3, 'exam:seat:export', 6, 1, 1, NOW());

