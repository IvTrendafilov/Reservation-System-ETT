CREATE SEQUENCE hibernate_sequence START 1;

create table ett_user (
    id bigint primary key,
    name varchar not null,
    email varchar not null,
    authorities jsonb not null
);

create index user_authorities on ett_user using gin (authorities);
create index user_pk_index on ett_user (id);

create table facility (
    id bigint primary key,
    name varchar not null,
    facility_type jsonb not null,
    room_id int not null,
    disabled boolean not null default false,
    reserved boolean not null default false
);

create index facility_pk_index on facility (id);

create table device (
    id bigint primary key,
    code varchar not null,
    disabled boolean not null default false,
    reserved boolean not null default false,
    position jsonb
);

create index device_pk_index on device (id);

create table reservation (
    id bigint primary key,
    dtype varchar not null,
    reservee_id bigint not null references ett_user (id),
    "from" timestamp not null,
    "to" timestamp not null,
    remarks varchar,
    status varchar not null,
    facility_id bigint references facility (id)
);

create index reservation_pk_index on reservation (id);

create table reservation_device (
    device_id bigint not null references device (id),
    reservation_id bigint not null references reservation (id),
    primary key (device_id, reservation_id)
);

create index reservation_device_pk_index on reservation_device (device_id, reservation_id);

create table day_schedule (
    id bigint primary key,
    name varchar not null,
    working_times jsonb not null,
    is_closed boolean not null default false
);

create table week_schedule (
    id bigint primary key,
    name varchar not null,
    monday_id bigint not null references day_schedule (id),
    tuesday_id bigint not null references day_schedule (id),
    wednesday_id bigint not null references day_schedule (id),
    thursday_id bigint not null references day_schedule (id),
    friday_id bigint not null references day_schedule (id),
    saturday_id bigint not null references day_schedule (id),
    sunday_id bigint not null references day_schedule (id)
);

create table schedule (
    id bigint primary key,
    name varchar not null,
    "from" timestamp not null,
    "to" timestamp not null,
    week_schedule_id bigint not null references week_schedule (id)
);

create table settings (
    id bigint primary key,
    auto_acceptance_of_device_reservations boolean not null default false,
    auto_acceptance_of_facility_reservations boolean not null default false,
    max_devices_per_reservation int not null default 1,
    max_booking_time_length int not null default 180,
    lounge_schedule_id bigint references week_schedule (id)
);

insert into settings(id, auto_acceptance_of_device_reservations, auto_acceptance_of_facility_reservations, max_devices_per_reservation, max_booking_time_length)
values (nextval('hibernate_sequence'), false, false, 1, 180);

insert into ett_user(id, email, name, authorities)
values (nextval('hibernate_sequence'), 'p.hristov@student.utwente.net', 'Pavel Hristov', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]'),
 (nextval('hibernate_sequence'), 'i.trendafilov@student.utwente.net', 'Ivan Trendafilov', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]'),
 (nextval('hibernate_sequence'), 'j.a.pratdesabalopez@student.utwente.net', 'Jose', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]'),
 (nextval('hibernate_sequence'), 'v.y.tonchev@student.utwente.net', 'Viktor Tonchev', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]'),
 (nextval('hibernate_sequence'), 'b.belchev@student.utwente.net', 'Boris Belchev', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]'),
 (nextval('hibernate_sequence'), 'irvineverio@student.utwente.net', 'Irvine Verio', '[ "ADMIN", "USER", "GLOBAL_ADMIN" ]')