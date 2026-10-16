CREATE DATABASE IF NOT EXISTS campus
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

create table campus.comment
(
    id          int auto_increment comment '评论主键'
        primary key,
    note_id     int                                   not null comment '笔记ID，逻辑关联note.id',
    user_id     int                                   not null comment '用户ID，评论用户ID，逻辑关联users.id',
    content     varchar(1000)                         not null comment '评论内容',
    status      varchar(20) default '正常'            not null comment '评论状态 ：1 正常 / 2 删除',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '评论表';

create table campus.favorite_record
(
    id          int auto_increment comment '收藏记录主键'
        primary key,
    user_id     int                                not null comment '用户ID，逻辑关联 users.id',
    note_id     int                                not null comment '笔记ID，逻辑关联 note.id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_favorite_user_note
        unique (user_id, note_id)
)
    comment '收藏记录表';

create table campus.like_record
(
    id          int auto_increment comment '点赞记录主键'
        primary key,
    user_id     int                                not null comment '用户ID，逻辑关联 users.id',
    note_id     int                                not null comment '笔记ID，逻辑关联 note.id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_like_user_note
        unique (user_id, note_id)
)
    comment '点赞记录表';

create table campus.note
(
    id         int auto_increment comment '笔记主键'
        primary key,
    user_id    int                                   not null comment '作者ID，逻辑关联 users.id',
    title      varchar(100)                          not null comment '笔记标题',
    summary    varchar(300)                          not null comment '笔记摘要',
    context    text                                  not null comment '笔记正文',
    status     varchar(20) default '草稿'            not null comment '笔记状态：草稿/待审核/已发布/已拒绝',
    view_count bigint      default 0                 not null comment '浏览量',
    created_at datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '笔记表';

create table campus.note_image
(
    id          int auto_increment comment '图片主键'
        primary key,
    note_id     int                                not null comment '笔记ID，逻辑关联 note.id',
    image_url   varchar(500)                       not null comment '图片访问路径',
    sort_order  int      default 0                 not null comment '图片顺序',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '笔记图片表';

create table campus.users
(
    id          int auto_increment comment '主键ID'
        primary key,
    username    varchar(50)                            not null comment '登录名',
    password    varchar(255) default '123456'          not null comment '密码',
    gender      tinyint unsigned                       not null comment '性别，1 男，2 女',
    image       varchar(300)                           null comment '头像url',
    nickname    varchar(50)                            not null comment '昵称',
    profile     varchar(500)                           null comment '个人简介',
    role        varchar(20)  default 'user'            not null comment '用户角色：1 user/2 admin',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint username
        unique (username)
)
    comment '用户表';