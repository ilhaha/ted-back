SET @parentId = 1904051668896817152;
-- 考生报名表管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生报名表管理', 1000, 2, '/exam/enroll', 'Enroll', 'exam/enroll/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生报名表管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1904051668896817153, '列表', @parentId, 3, 'exam:enroll:list', 1, 1, 1, NOW()),
    (1904051668896817154, '详情', @parentId, 3, 'exam:enroll:detail', 2, 1, 1, NOW()),
    (1904051668896817155, '新增', @parentId, 3, 'exam:enroll:add', 3, 1, 1, NOW()),
    (1904051668896817156, '修改', @parentId, 3, 'exam:enroll:update', 4, 1, 1, NOW()),
    (1904051668896817157, '删除', @parentId, 3, 'exam:enroll:delete', 5, 1, 1, NOW()),
    (1904051668896817158, '导出', @parentId, 3, 'exam:enroll:export', 6, 1, 1, NOW());

