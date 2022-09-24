ALTER TABLE actor RENAME COLUMN first_name TO ${new_first_name_column_name};
ALTER TABLE actor RENAME COLUMN last_name TO ${new_last_name_column_name};

create table film_description (film_id smallint not null, description clob, primary key (film_id));

insert into film_description(film_id, description)
        select film_id, description from film;

alter table film drop column description;

insert into film_comment values  (1, E'hostname=${hostname}\nport=${port}\nusername=${username}\npassword=${password}\nenv.POSTGRES_DB=${env.POSTGRES_DB}\nenv.TEST_VALUE=${env.TEST_VALUE}');