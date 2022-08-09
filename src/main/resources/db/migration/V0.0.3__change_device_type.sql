alter table device
drop column type;

create table device_type (
    id bigint primary key,
    image_class varchar not null,
    name varchar not null
);

alter table device
add type_id bigint not null references device_type (id);