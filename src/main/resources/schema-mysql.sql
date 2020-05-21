create table token (
    token varchar(128) not null,
	data text not null,
	issue_timestamp bigint not null,
	expire_timestamp bigint not null,
	primary key(token)
);