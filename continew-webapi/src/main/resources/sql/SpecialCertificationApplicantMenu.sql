SET @parentId = 1909150048526336000;
-- 特种设备人员资格申请管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '特种设备人员资格申请管理', 1000, 2, '/exam/specialCertificationApplicant', 'SpecialCertificationApplicant', 'exam/specialCertificationApplicant/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 特种设备人员资格申请管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909150048526336001, '列表', @parentId, 3, 'exam:specialCertificationApplicant:list', 1, 1, 1, NOW()),
    (1909150048526336002, '详情', @parentId, 3, 'exam:specialCertificationApplicant:detail', 2, 1, 1, NOW()),
    (1909150048526336003, '新增', @parentId, 3, 'exam:specialCertificationApplicant:add', 3, 1, 1, NOW()),
    (1909150048526336004, '修改', @parentId, 3, 'exam:specialCertificationApplicant:update', 4, 1, 1, NOW()),
    (1909150048526336005, '删除', @parentId, 3, 'exam:specialCertificationApplicant:delete', 5, 1, 1, NOW()),
    (1909150048526336006, '导出', @parentId, 3, 'exam:specialCertificationApplicant:export', 6, 1, 1, NOW());

