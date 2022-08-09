create table day_schedule_exception (
    id bigint primary key,
    date date not null,
    working_times jsonb not null,
    is_closed boolean not null default false
);

create index day_schedule_exception_date_idx on day_schedule_exception (date);