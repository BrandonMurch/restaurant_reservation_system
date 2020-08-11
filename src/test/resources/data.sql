insert into loginable(id, username, first_name, last_name, phone_number,
terms_and_conditions, ROLE_TYPE)
values(10001, 'thisisanemail@email.com', 'John', 'Doe', '+61 304940380808',
1, 'USER');

insert into booking(id, end_time, party_size, start_time, user_id, date)
values(10000, '20201211 20:00:00.00',  4, '20201211 17:00:00.00', 10001, '20201211');
insert into booking(id, end_time, party_size, start_time, user_id, date)
values(10003, '20201210 23:00:00.00',  2, '20201210 20:00:00.00', 10001, '20201210');
insert into booking(id, end_time, party_size, start_time, user_id, date)
values(10004, '20201211 23:00:00.00',  2, '20201211 20:00:00.00', 10001, '20201211');