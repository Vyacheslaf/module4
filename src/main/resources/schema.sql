DROP TABLE IF EXISTS gift_certificate_tag;
DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS gift_certificate;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS "user";

drop table if exists gift_certificate_audit_log;
drop table if exists gift_certificate_tag_audit_log;
drop table if exists order_audit_log;
drop table if exists tag_audit_log;
drop table if exists user_audit_log;
drop table if exists revinfo;

create table if not exists gift_certificate (
	duration integer not null,
    price integer not null,
    create_date timestamp,
    id bigserial,
    last_update_date timestamp,
    description varchar(255),
    "name" varchar(255),
    primary key (id));

create table tag (
	id bigserial,
    "name" varchar(255) not null unique,
    primary key (id));

create table gift_certificate_audit_log (
	duration integer,
    price integer,
    rev integer not null,
    revtype smallint,
    create_date timestamp,
    id bigint not null,
    last_update_date timestamp,
    "description" varchar(255),
    "name" varchar(255),
    primary key (rev, id));

create table gift_certificate_tag (
	gift_certificate_id bigint not null,
    tag_id bigint not null,
    primary key (gift_certificate_id, tag_id),
	CONSTRAINT fk_gift_certificate
		FOREIGN KEY (gift_certificate_id)
		REFERENCES gift_certificate (id) ON DELETE CASCADE
										 ON UPDATE CASCADE,
	CONSTRAINT fk_tag
		FOREIGN KEY (tag_id)
		REFERENCES tag (id) ON DELETE CASCADE
							ON UPDATE CASCADE);

create table gift_certificate_tag_audit_log (
	rev integer not null,
    revtype smallint,
    gift_certificate_id bigint not null,
    tag_id bigint not null,
    primary key (rev, gift_certificate_id, tag_id));

drop type if exists roles;
create type roles as enum ('ADMIN','USER');
create table "user" (
	id bigserial,
    "password" varchar(60) not null,
    email varchar(255),
    "role" roles,
    username varchar(255),
    primary key (id));

create table "order" (
	"cost" integer not null,
    gift_certificate_id bigint not null,
    id bigserial,
    purchase_date timestamp,
    user_id bigint,
    primary key (id),
	CONSTRAINT fk_order_gift_certificate
		FOREIGN KEY (gift_certificate_id)
		REFERENCES gift_certificate (id) ON DELETE RESTRICT
										 ON UPDATE CASCADE,
	CONSTRAINT fk_order_user
		FOREIGN KEY (user_id)
		REFERENCES "user" (id) ON DELETE RESTRICT
							   ON UPDATE CASCADE);

create table order_audit_log (
	"cost" integer,
    rev integer not null,
    revtype smallint,
    gift_certificate_id bigint,
    id bigint not null,
    purchase_date timestamp,
    user_id bigint,
    primary key (rev, id));


create table revinfo (
	rev int4,
    revtstmp bigint,
    primary key (rev));

create table tag_audit_log (
	rev integer not null,
    revtype smallint,
    id bigint not null,
    "name" varchar(255),
    primary key (rev, id));

create table user_audit_log (
	rev integer not null,
    revtype smallint,
    id bigint not null,
    "password" varchar(60),
    email varchar(255),
    "role" roles,
    username varchar(255),
    primary key (rev, id));

alter table gift_certificate_audit_log add constraint FKo65ssi88vv9mtvfextnixgsp3 foreign key (rev) references revinfo (rev);
alter table gift_certificate_tag_audit_log add constraint FKafuegcf5o5a56b78p63fodlv3 foreign key (rev) references revinfo (rev);
alter table order_audit_log add constraint FK6nxn1ml05e54pqaffhm6vxqio foreign key (rev) references revinfo (rev);
alter table tag_audit_log add constraint FKqgeffaxm1ivtrrxkhkbh0iecw foreign key (rev) references revinfo (rev);
alter table user_audit_log add constraint FKmnmt76kncc7hpk2e46r12rm5k foreign key (rev) references revinfo (rev);

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence;

INSERT INTO "user" (username, email, "password", "role") VALUES ('admin', 'email', '$2a$10$b5dMeA7KVGp/Mjtki.R8KeD5SPTTq7lM6kx6ZBD2Nqjzqo8zJ/ora', 'ADMIN');