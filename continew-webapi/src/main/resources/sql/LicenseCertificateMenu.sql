SET @parentId = 2004073114565419008;
-- 人员及许可证书信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '人员及许可证书信息管理', 1000, 2, '/exam/licenseCertificate', 'LicenseCertificate', 'exam/licenseCertificate/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 人员及许可证书信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2004073114565419009, '列表', @parentId, 3, 'exam:licenseCertificate:list', 1, 1, 1, NOW()),
    (2004073114565419010, '详情', @parentId, 3, 'exam:licenseCertificate:detail', 2, 1, 1, NOW()),
    (2004073114565419011, '新增', @parentId, 3, 'exam:licenseCertificate:add', 3, 1, 1, NOW()),
    (2004073114565419012, '修改', @parentId, 3, 'exam:licenseCertificate:update', 4, 1, 1, NOW()),
    (2004073114565419013, '删除', @parentId, 3, 'exam:licenseCertificate:delete', 5, 1, 1, NOW()),
    (2004073114565419014, '导出', @parentId, 3, 'exam:licenseCertificate:export', 6, 1, 1, NOW());

