alter table device
    add column deleted_on timestamp default null;

alter table facility
    add column deleted_on timestamp default null;