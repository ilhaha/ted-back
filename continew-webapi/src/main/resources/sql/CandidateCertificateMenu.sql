SET @parentId = 1901452641513226240;
-- 考生证件管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生证件管理', 1000, 2, '/certificate/candidateCertificate', 'CandidateCertificate', 'certificate/candidateCertificate/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生证件管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1901452641513226241, '列表', @parentId, 3, 'certificate:candidateCertificate:list', 1, 1, 1, NOW()),
    (1901452641513226242, '详情', @parentId, 3, 'certificate:candidateCertificate:detail', 2, 1, 1, NOW()),
    (1901452641513226243, '新增', @parentId, 3, 'certificate:candidateCertificate:add', 3, 1, 1, NOW()),
    (1901452641513226244, '修改', @parentId, 3, 'certificate:candidateCertificate:update', 4, 1, 1, NOW()),
    (1901452641513226245, '删除', @parentId, 3, 'certificate:candidateCertificate:delete', 5, 1, 1, NOW()),
    (1901452641513226246, '导出', @parentId, 3, 'certificate:candidateCertificate:export', 6, 1, 1, NOW());

